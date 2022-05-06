package top.kmar.mi.api.graph.utils.json

import com.google.gson.JsonObject
import top.kmar.mi.api.utils.data.math.Rect2D

object TextureParserV1 {

    fun parse(json: JsonObject, image: String,  info: TextureInfo) {
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