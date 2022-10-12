package top.kmar.mi.api.graphics.parser

import com.google.gson.JsonParser
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import jdk.internal.util.xml.impl.ReaderUTF8
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.ComplexCmptExp
import top.kmar.mi.api.graphics.parser.cache.IParserCache
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.removeAllSpace
import java.util.*

/**
 * GUI的样式表解析器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
object GuiStyleParser {

    private var count = 0

    private val expMap = Object2ObjectOpenHashMap<ResourceLocation, MutableList<Node>>()

    init {
        reload()
    }

    fun reload() {
        expMap.clear()
        count = 0
        for (key in GuiLoader.keyIterator()) {
            try {
                val location = ResourceLocation(
                    key.resourceDomain, "gui/style/${key.resourcePath}.json"
                )
                parseTargetFile(location, key)
            } catch (e: Exception) {
                MISysInfo.err("为[$key]加载样式表的过程中出现错误", e)
            }
        }
        MISysInfo.print("共加载了 $count 个样式文件")
    }

    /** 初始化指定树中的所有控件的样式表 */
    fun initStyle(key: ResourceLocation, root: Cmpt) {
        val list = expMap[key] ?: return
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

    private val parser = JsonParser()

    private fun parseTargetFile(location: ResourceLocation, key: ResourceLocation) {
        val resourceManager = Minecraft.getMinecraft().resourceManager
        val json =
            ReaderUTF8(resourceManager.getResource(location).inputStream).use { parser.parse(it) }.asJsonObject
        val style = json.getAsJsonArray("style")
        for (item in style) {
            val obj = item.asJsonObject
            val exp = ComplexCmptExp(obj["exp"].asString)
            val list = LinkedList<IParserCache>()
            for (tmp in obj.getAsJsonArray("value")) {
                val ele = tmp.asString.removeAllSpace()
                list.add(IParserCache.build(ele))
            }
            expMap.computeIfAbsent(key) { LinkedList() }.add(Node(exp, list))
        }
        ++count
    }

    data class Node(val exp: ComplexCmptExp, val cache: List<IParserCache>)

}