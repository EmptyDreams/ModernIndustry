package top.kmar.mi.api.graph.utils.managers

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.client.renderer.texture.TextureUtil
import top.kmar.mi.api.gui.client.RuntimeTexture
import top.kmar.mi.api.utils.data.math.Size2D
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.Closeable

/**
 * 管理材质缓存
 * @author EmptyDreams
 */
class TextureCacheManager(private val builder: (Size2D, Graphics) -> Unit) : Closeable {

    private val cacheMap = Object2ObjectOpenHashMap<Size2D, RuntimeTexture>()

    operator fun get(size: Size2D): RuntimeTexture {
        return cacheMap.computeIfAbsent(size) {
            val image = BufferedImage(size.width, size.height, 6)
            val graphics = image.createGraphics()
            builder(size, graphics)
            graphics.dispose()
            RuntimeTexture.instanceNoCache(image)
        }
    }

    override fun close() {
        for ((_, value) in cacheMap) {
            TextureUtil.deleteTexture(value.glTextureId)
        }
    }

}