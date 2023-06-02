package top.kmar.mi.api.graphics.parser

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import jdk.internal.util.xml.impl.ReaderUTF8
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentBase
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.exps.ComplexCmptExp
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.graphics.utils.style.StyleSheet
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.expands.*
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.util.*

/**
 * GUI的样式表解析器
 *
 * [在线文档](https://kmar.top/posts/e0217013/)
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
@EventBusSubscriber(Side.CLIENT)
object GuiStyleParser {

    private val expMap = Object2ObjectOpenHashMap<ResourceLocation, StyleSheet>()

    /** 加载指定样式文件 */
    fun load(key: ResourceLocation): StyleSheet? {
        if (key in expMap) return expMap[key]
        return try {
            val location = ResourceLocation(
                key.resourceDomain, "mi_files/gui/styles/${key.resourcePath}.styl"
            )
            parseTargetFile(location, key)
        } catch (e: Exception) {
            MISysInfo.err("为[$key]加载样式表的过程中出现错误", e)
            null
        }
    }

    private fun parseTargetFile(location: ResourceLocation, key: ResourceLocation): StyleSheet {
        val resourceManager = Minecraft.getMinecraft().resourceManager
        val result = expMap.computeIfAbsent(key) { StyleSheet() }
        BufferedReader(ReaderUTF8(resourceManager.getResource(location).inputStream)).use { reader ->
            val builder = CmptExpBuilder()
            var node = StyleNode()
            var editStyle = false

            /** 解析选择表达式或样式表达式 */
            fun parseExpAndStyle(str: String, level: Int) {
                val isStyle = StyleStatementParser(str, node)
                if (isStyle) editStyle = true
                else {
                    if (editStyle) {
                        builder.toExp { result.add(it, node) }
                        node = StyleNode()
                        editStyle = false
                    }
                    builder.goto(level)
                    str.splitToSequence(',')
                        .filter { it.isNotBlank() }
                        .forEach { builder.addExp(ComplexCmptExp(it)) }
                }
            }

            /** 解析 @ 语句 */
            fun parseAt(content: String, level: Int) {
                val list = content.split(' ').filter { it.isNotBlank() }
                if (list.size != 2) throw IllegalArgumentException("@ 语句格式固定为 @xxx xxx")
                val (command, value) = list
                builder.goto(level)
                when (command) {
                    "import" -> {
                        val path = value.substringBetween('"')
                        val split = path.indexOf(':')
                        val otherKey =
                            if (split == -1) key.copy(path = path)
                            else ResourceLocation(path.substring(0, split), path.substring(split + 1))
                        val otherSheet = load(otherKey) ?: throw FileNotFoundException("没有找到指定的样式文件：$path")
                        builder.toExp {
                            otherSheet.forEach { (exp, node) -> result.add(it + exp, node) }
                        }
                    }
                    else -> throw IllegalArgumentException("不支持的 @ 语句：$command")
                }
            }

            reader.lines()
                .filter { it.isNotBlank() }
                .forEachOrdered {
                    val (index, count) = it.countStartSpace()
                    val level = count.floorDiv2()
                    val content = it.trimEndAt(index)
                    if (content.startsWith('@')) parseAt(content, level)
                    else parseExpAndStyle(content, level)
                }
            if (editStyle) builder.toExp { result.add(it, node) }
        }
        return result
    }

    // 在切换资源包时清空缓存
    @JvmStatic
    @SubscribeEvent
    fun onResourcesBake(event: ModelBakeEvent) {
        expMap.clear()
    }

    init {
        ClientCommandHandler.instance.registerCommand(object : CommandBase() {
            override fun getName() = "clear-mi-graphics"

            override fun getUsage(sender: ICommandSender) = "commands.debug.usage"

            override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
                if (args.isEmpty()) expMap.clear()
                else args.forEach { expMap.remove(ResourceLocation(it)) }
                sender.sendMessage(object : TextComponentBase() {
                    override fun getUnformattedComponentText() = "成功清理 graphics 缓存"
                    override fun createCopy() = this
                })
            }
        })
    }

}