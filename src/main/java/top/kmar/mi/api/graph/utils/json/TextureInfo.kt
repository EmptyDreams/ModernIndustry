package top.kmar.mi.api.graph.utils.json

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.graph.utils.textures.SubStaticTexture
import top.kmar.mi.api.utils.data.math.Rect2D

/**
 * 材质信息
 * @author EmptyDreams
 */
class TextureInfo(val modid: String) {

    private val textureMap = Object2ObjectOpenHashMap<String, Pair<SubStaticTexture, Rect2D>>()

    fun write(image: String, key: String, value: Rect2D) {
        val texture = SubStaticTexture(buildLocation(image), value.x, value.y, value.width, value.height)
        textureMap[key] = Pair(texture, value)
    }

    fun read(key: String): Pair<SubStaticTexture, Rect2D> = textureMap[key]!!

    operator fun get(key: String) = read(key)

    fun buildLocation(path: String) = ResourceLocation(modid, path)

}