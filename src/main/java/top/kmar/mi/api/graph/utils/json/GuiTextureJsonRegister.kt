package top.kmar.mi.api.graph.utils.json

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import jdk.internal.util.xml.impl.ReaderUTF8
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.register.others.AutoLoader

/**
 * GUI JSON注册机
 * @author EmptyDreams
 */
@AutoLoader
object GuiTextureJsonRegister {

    init {
        registryJson(ResourceLocation(ModernIndustry.MODID, "textures/gui/options.json"))
    }

    private val valueMap = Object2ObjectOpenHashMap<String, TextureInfo>()

    @JvmStatic
    fun registryJson(key: ResourceLocation) {
        val json = readJson(key)
        when (val version = json.get("version").asInt) {
            1 -> parseJsonV1(json.getAsJsonObject("fileList"))
            else -> throw IllegalArgumentException("不支持的Json版本：$version")
        }
    }

    /**
     * 获取指定资源
     * @throws NullPointerException 如果指定资源不存在
     */
    @JvmStatic
    operator fun get(modid: String, key: String) = valueMap[modid]!![key]

    private fun parseJsonV1(json: JsonObject) {
        val modid = json.get("modid").asString
        val image = json.get("image").asString
        val option = ResourceLocation(modid, json.get("json").asString)
        val property = readJson(option)
        TextureParserV1.parse(property, image, valueMap.computeIfAbsent(modid) { TextureInfo(modid) })
    }

    private val parse = JsonParser()

    @JvmStatic
    fun readJson(path: ResourceLocation): JsonObject {
        val stream = Minecraft.getMinecraft().resourceManager.getResource(path).inputStream
        return JsonReader(ReaderUTF8(stream)).use { parse.parse(it).asJsonObject }
    }

}