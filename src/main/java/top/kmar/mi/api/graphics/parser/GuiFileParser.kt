package top.kmar.mi.api.graphics.parser

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import org.apache.commons.io.FilenameUtils
import top.kmar.mi.api.exception.TransferException
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptRegister
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.container.PairIntObj
import top.kmar.mi.api.utils.countStartSpace
import top.kmar.mi.api.utils.removeAllSpace
import java.nio.file.Files
import java.nio.file.Path

/**
 * `mig`文件解析器
 * @author EmptyDreams
 */
object GuiFileParser {

    private var count = 0

    fun registryAll(event: GuiLoader.MIGuiRegistryEvent) {
        Loader.instance().activeModList.forEach { mod ->
            CraftingHelper.findFiles(
                mod, "assets/${mod.modId}/gui/mig", { true },
                { root, file ->
                    Loader.instance().setActiveModContainer(mod)
                    val relative = root.relativize(file).toString()
                    if ("mig" != FilenameUtils.getExtension(relative)) return@findFiles true
                    try {
                        parseTargetFile(file, event)
                    } catch (e: Exception) {
                        MISysInfo.err("处理目标文件[$relative]时发生异常", e)
                    }
                    true
                }, true, true)
        }
    }

    private fun parseTargetFile(path: Path, register: GuiLoader.MIGuiRegistryEvent) {
        var key: ResourceLocation? = null
        val root = BaseGraphics.DocumentCmpt(null)
        var preEle: Cmpt = root
        var preLevel = -1
        var client = false
        fun parseLine(content: String) {
            if (content.startsWith('@')) {
                if (content == "@client") client = true
                else if (key != null) throw IllegalArgumentException("同一个文件中出现了多次@语句")
                else key = ResourceLocation(content.substring(1))
                return
            }
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
        ++count
        if (client) register.registryClient(key!!, root)
        else register.registry(key!!, root)
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
            .map { it.removeAllSpace() }
            .forEach {
                val mid = it.indexOf('=')
                val key = it.substring(0 until mid)
                val text = if (it[mid + 1] == '"')
                                it.substring(mid + 2 until it.length - 1)
                            else it.substring(mid + 1)
                result[key] = text
            }
        return result
    }

}