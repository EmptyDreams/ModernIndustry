package top.kmar.mi.api.graphics.parser

import com.google.gson.JsonParser
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import jdk.internal.util.xml.impl.ReaderUTF8
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.ComplexCmptExp
import top.kmar.mi.api.graphics.parser.cache.IParserCache
import top.kmar.mi.api.utils.MISysInfo
import java.util.*

/**
 * GUI的样式表解析器
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
                key.resourceDomain, "gui/style/${key.resourcePath}.json"
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
        val reader = ReaderUTF8(resourceManager.getResource(location).inputStream)
        val json = reader.use { JsonParser().parse(it) }.asJsonObject
        val style = json.getAsJsonArray("style")
        val result = expMap.computeIfAbsent(key) { LinkedList() }
        for (item in style) {
            val obj = item.asJsonObject
            val exp = ComplexCmptExp(obj["exp"].asString)
            val list = LinkedList<IParserCache>()
            for (tmp in obj.getAsJsonArray("value")) {
                val ele = tmp.asString
                try {
                    list.add(IParserCache.build(ele))
                } catch (e: Exception) {
                    MISysInfo.err("在处理json文件的该行时遇到错误：$ele", e)
                }
            }
            result.add(Node(exp, list))
        }
        return result
    }

    data class Node(val exp: ComplexCmptExp, val cache: List<IParserCache>)

    @JvmStatic
    @SubscribeEvent
    fun onResourcesBake(event: ModelBakeEvent) {
        expMap.clear()
    }

}