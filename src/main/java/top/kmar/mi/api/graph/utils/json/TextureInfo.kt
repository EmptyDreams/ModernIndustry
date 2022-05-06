package top.kmar.mi.api.graph.utils.json

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.ITextureObject
import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.utils.data.math.Rect2D

/**
 * 材质信息
 * @author EmptyDreams
 */
class TextureInfo(val modid: String) {

    private val textureMap = Object2ObjectOpenHashMap<String, Pair<ITextureObject, Rect2D>>()

    fun write(image: String, key: String, value: Rect2D) {
        textureMap[key] = Pair(getTexture(image), value)
    }

    fun read(key: String): Pair<ITextureObject, Rect2D> = textureMap[key]!!

    operator fun get(key: String) = read(key)

    fun getTexture(image: String): ITextureObject =
        Minecraft.getMinecraft().textureManager.getTexture(buildLocation(image))

    fun buildLocation(path: String) = ResourceLocation(modid, path)

}