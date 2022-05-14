package top.kmar.mi.api.graph.utils.json

import com.google.gson.JsonObject
import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.utils.data.math.Rect2D

/**
 * 一代材质Json解析器
 *
 * 列表Json格式及解释：
 *
 * ```
 * {
 *  "version": 1,
 *  "fileList": {
 *      "[name0]": {
 *          "modid": [String],  //可选，不写表明从注册时输入的modid继承
 *          "image": [String],  //必填，材质路径
 *          "json": [String],   //必填，Json路径
 *      },
 *      "[name1]": { ... }
 *      ...
 *  }
 * }
 * ```
 *
 * 子Json格式及解释：
 *
 * ```
 * {
 *  "[key0]": {  //[key]是资源的键值，对应 GuiTextureJsonRegister[modid, key] 中的 key
 *      "x": [Int],     //资源在材质中的X轴坐标
 *      "y": [Int],     //资源在材质中的Y轴坐标
 *      "width": [Int], //资源的宽度
 *      "height": [Int] //资源的高度
 *  },
 *  "[key1]": { ... }
 *  ...
 * }
 * ```
 *
 * @author EmptyDreams
 */
object TextureParserV1 {

    fun parse(json: JsonObject, modid: String, valueMap: MutableMap<String, TextureInfo>) {
        for ((_, value) in json.entrySet()) {
            val option = value.asJsonObject
            val image = option["image"].asString
            val resourceModid = if (option.has("modid")) option["modid"].asString else modid
            val resource = ResourceLocation(resourceModid, option["json"].asString)
            val property = GuiTextureJsonRegister.readJson(resource)
            val info = valueMap.computeIfAbsent(modid) { TextureInfo(modid) }
            parseSubJson(property, image, info)
        }
    }

    private fun parseSubJson(json: JsonObject, image: String, info: TextureInfo) {
        for ((key, value) in json.entrySet()) {
            info.write(image, key, readRect(value.asJsonObject))
        }
    }

    fun readRect(json: JsonObject): Rect2D {
        val x = json["x"].asInt
        val y = json["y"].asInt
        val width = json["width"].asInt
        val height = json["height"].asInt
        return Rect2D(x, y, width, height)
    }

}