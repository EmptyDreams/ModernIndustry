package top.kmar.mi.api.graph.utils.managers

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.gui.client.RuntimeTexture
import top.kmar.mi.api.utils.data.math.Size2D
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.Closeable

/**
 *
 * @author EmptyDreams
 */
class ExpandTextureCacheManager<T>(
    private val painter: (Size2D, T, Graphics) -> Unit
) : Closeable {

    private val cacheMap = Object2ObjectOpenHashMap<Key<T>, RuntimeTexture>()

    operator fun get(size: Size2D, data: T): RuntimeTexture {
        val key = Key(size, data)
        return cacheMap.computeIfAbsent(key) {
            val image = BufferedImage(size.width, size.height, 6)
            val graphics = image.createGraphics()
            painter(size, data, graphics)
            graphics.dispose()
            RuntimeTexture.instanceNoCache(image)
        }
    }

    override fun close() {
        for ((_, texture) in cacheMap) {
            texture.deleteGlTexture()
        }
        cacheMap.clear()
    }

    private class Key<T>(
        val size: Size2D,
        val data: T
    ) {

        override fun equals(other: Any?): Boolean {
            return other is Key<*> && size == other.size && data == other.data
        }

        override fun hashCode(): Int {
            return size.hashCode() * 31 + data.hashCode()
        }
    }

}