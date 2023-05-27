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
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.utils.exps.ComplexCmptExp
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.graphics.utils.style.StyleSheet
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.expands.countStartSpace
import top.kmar.mi.api.utils.expands.floorDiv2
import top.kmar.mi.api.utils.expands.trimEndAt
import java.io.BufferedReader
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

    /** 初始化指定树中的所有控件的样式表 */
    fun initStyle(key: ResourceLocation, root: Cmpt) {
        val list = load(key) ?: return
    }

    private fun parseTargetFile(location: ResourceLocation, key: ResourceLocation): StyleSheet {
        val resourceManager = Minecraft.getMinecraft().resourceManager
        val result = expMap.computeIfAbsent(key) { StyleSheet() }
        BufferedReader(ReaderUTF8(resourceManager.getResource(location).inputStream)).use { reader ->
            val builder = CmptExpBuilder()
            val lines = LinkedList<String>()

            fun parseStyle() {
                val node = StyleNode()
                lines.forEach { StyleStatementParser(it, node) }
                builder.toExp { result.add(it, node) }
            }

            fun parseCmptExp(level: Int) {
                builder.goto(level)
                lines.forEach { line ->
                    line.splitToSequence(',')
                        .filter { it.isNotBlank() }
                        .forEach { builder.addExp(ComplexCmptExp(it)) }
                }
            }

            var preLevel = 0
            reader.lines()
                .filter { it.isNotBlank() }
                .forEachOrdered {
                    val (index, count) = it.countStartSpace()
                    val level = count.floorDiv2()
                    when {
                        level == preLevel -> lines += it.trimEndAt(index)
                        level > preLevel -> {
                            parseCmptExp(preLevel)
                            lines.clear()
                        }
                        else -> {
                            parseStyle()
                            lines.clear()
                        }
                    }
                    preLevel = level
                }
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