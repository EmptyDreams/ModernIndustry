package top.kmar.mi.api.graphics.parser

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import top.kmar.mi.api.exception.TransferException
import top.kmar.mi.api.graphics.DocumentCmpt
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptRegister
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.container.PairIntObj
import top.kmar.mi.api.utils.expands.countStartSpace
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

/**
 * `mig`文件解析器
 *
 * [在线文档](https://kmar.top/posts/e0217013/)
 *
 * @author EmptyDreams
 */
object GuiFileParser {

    fun registryAll(event: GuiLoader.MIGuiRegistryEvent) {
        var count = eachAllFiles("gui/pugs/common", false, event)
        if (FMLCommonHandler.instance().side.isClient)
            count += eachAllFiles("gui/pugs/client", true, event)
        MISysInfo.print("共扫描并注册 $count 个GUI文件")
    }

    private fun eachAllFiles(base: String, isClient: Boolean, event: GuiLoader.MIGuiRegistryEvent): Int {
        var count = 0
        Loader.instance().activeModList.forEach { mod ->
            CraftingHelper.findFiles(mod, "assets/${mod.modId}/mi_files/$base", { true },
                { _, file ->
                    Loader.instance().setActiveModContainer(mod)
                    try {
                        registryTarget(mod.modId, file, isClient, event)
                        ++count
                        true
                    } catch (e: Throwable) {
                        MISysInfo.err("注册指定GUI[$file]时出现异常", e)
                        false
                    }
                }, true, true)
        }
        return count
    }

    /** 解析并注册一个文件代表的GUI */
    private fun registryTarget(
        modid: String, path: Path, isClient: Boolean,
        event: GuiLoader.MIGuiRegistryEvent
    ) {
        if ("pug" != path.extension) return
        val key = ResourceLocation(modid, path.nameWithoutExtension)
        val root = parseTargetFile(path)
        if (isClient) event.registryClient(key, root)
        else event.registry(key, root)
    }

    private fun parseTargetFile(path: Path): DocumentCmpt {
        val root = DocumentCmpt()
        var preEle: Cmpt = root
        var preLevel = -1
        fun parseLine(content: String) {
            // 构建Cmpt对象
            val (index0, length) = content.countStartSpace()
            val (index1, tag) = content.getTag(index0)
            val (index2, id) = content.getID(index1)
            val attributes = content.getAttributes(index2)
            val level = length shr 2
            attributes.id = id
            attributes["level"] = level.toString()
            val cmptObj = CmptRegister.buildServiceCmpt<Cmpt>(tag, attributes)
            // 将Cmpt插入到树中
            if (level > preLevel) {
                preEle.addChild(cmptObj)
            } else if (length == preLevel) {
                preEle.parent.addChild(cmptObj)
            } else {
                do {
                    preEle = preEle.parent
                } while (preEle.attributes["level"].toInt() >= level)
                preEle.addChild(cmptObj)
            }
            preEle = cmptObj
            preLevel = level
        }
        Files.lines(path).forEachOrdered {
            try {
                parseLine(it)
            } catch (e: Exception) {
                throw TransferException.instance("当前行内容：$it", e)
            }
        }
        return root
    }

    /** 获取字符串中的Tag */
    private fun String.getTag(start: Int): PairIntObj<String> {
        for (i in start until length) {
            if (!this[i].isLetter() && this[i] != '-')
                return PairIntObj(i, substring(start until i))
        }
        return PairIntObj(length, substring(start))
    }

    /** 获取字符串中的ID */
    private fun String.getID(start: Int): PairIntObj<String> {
        if (length == start || this[start] != '#') return PairIntObj(start, "")
        for (i in start + 1 until length) {
            if (!this[i].isLetter() && this[i] != '-')
                return PairIntObj(i, substring(start + 1 until i))
        }
        return PairIntObj(length, substring(start + 1))
    }

    /** 获取属性列表 */
    private fun String.getAttributes(start: Int): CmptAttributes {
        val result = CmptAttributes()
        if (length == start || this[start] != '(') return result
        val index = lastIndexOf(')')
        val value = substring(start + 1 until index)
        value.split(',').stream()
            .filter { it.isNotBlank() }
            .forEach {
                val mid = it.indexOf('=')
                if (mid == -1) {
                    result[it.trim()] = ""
                } else {
                    val key = it.substring(0 until mid).trim()
                    val left = it.indexOf('"')
                    val text = if (left != -1) {
                        val right = it.lastIndexOf('"')
                        it.substring(left + 1 until right)
                    } else {
                        it.substring(mid + 1).trim()
                    }
                    result[key] = text
                }
            }
        return result
    }

}