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
import top.kmar.mi.api.graphics.components.interfaces.ComplexCmptExp
import top.kmar.mi.api.graphics.parser.cache.IParserCache
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.countStartSpace
import top.kmar.mi.api.utils.floorDiv2
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

    private val expMap = Object2ObjectOpenHashMap<ResourceLocation, MutableList<Node>>()

    fun load(key: ResourceLocation): List<Node>? {
        if (key in expMap) return expMap[key]
        return try {
            val location = ResourceLocation(
                key.resourceDomain, "gui/style/${key.resourcePath}.styl"
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
        for ((exp, values) in list) {
            root.queryCmptAll(exp).stream()
                .map { it.client.style }
                .forEach {
                    for (parse in values) {
                        parse(it)
                    }
                }
        }
    }

    private fun parseTargetFile(location: ResourceLocation, key: ResourceLocation): List<Node> {
        val resourceManager = Minecraft.getMinecraft().resourceManager
        val result = expMap.computeIfAbsent(key) { LinkedList() }
        BufferedReader(ReaderUTF8(resourceManager.getResource(location).inputStream)).use { reader ->
            val builder = CmptExpBuilder()
            var endWithContinue = false
            var preLevel = -1
            val valueList = LinkedList<IParserCache>()
            var prevContent = ""

            fun export(clear: Boolean) {
                if (valueList.isEmpty()) return
                val tmp = ArrayList(valueList)
                builder.toExp { result.add(Node(it, tmp)) }
                if (clear) valueList.clear()
            }

            reader.lines().filter { it.isNotBlank() }.forEachOrdered {  content ->
                val (_, length) = content.countStartSpace()
                val level = length.floorDiv2()
                // 判断上一条语句是属性还是exp
                if (!endWithContinue && level <= preLevel) {
                    valueList.add(IParserCache.build(prevContent.trim()))
                    if (level != preLevel) {
                        export(true)
                        for (i in 0 until preLevel - level)
                            builder.prev()
                    }
                } else {
                    export(true)
                    val text = prevContent.trimEnd()
                    endWithContinue = text.endsWith(',')
                    if (level > preLevel) {
                        for (i in 0 until level - preLevel)
                            builder.next()
                    }
                    text.split(',')
                        .filter { it.isNotBlank() }
                        .map { ComplexCmptExp(it) }
                        .forEach { builder.addExp(it) }
                }
                preLevel = level
                prevContent = content
            }
            valueList.add(IParserCache.build(prevContent.trim()))
            export(false)
        }
        return result
    }

    data class Node(val exp: ComplexCmptExp, val cache: List<IParserCache>)

    // 在切换资源包时清空缓存
    @JvmStatic
    @SubscribeEvent
    fun onResourcesBake(event: ModelBakeEvent) {
        expMap.clear()
    }

    init {
        ClientCommandHandler.instance.registerCommand(object : CommandBase() {
            override fun getName() = "clearMiGraphics"

            override fun getUsage(sender: ICommandSender) = "commands.debug.usage"

            override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
                if (args.isEmpty()) expMap.clear()
                else args.forEach { expMap.remove(ResourceLocation(it)) }
                sender.sendMessage(object : TextComponentBase() {
                    override fun getUnformattedComponentText() = "成功清理graphics缓存"
                    override fun createCopy() = this
                })
            }
        })
    }

}