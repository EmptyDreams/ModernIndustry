package top.kmar.mi.api.graph.utils.managers

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.gui.client.RuntimeTexture
import top.kmar.mi.api.utils.data.math.Size2D
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.Closeable

/**
 * 带自定义参数的材质缓存管理
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class ExpandTextureCacheManager<T>(
    /**
     * 材质绘制器，在第一次加载材质时绘制材质
     *
     * 参数列表从左到右依次是：材质大小、自定义参数、画笔
     *
     * 其中画笔不需要手动调用[Graphics.dispose]
     */
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

    operator fun set(size: Size2D, data: T, texture: RuntimeTexture) {
        val key = Key(size, data)
        cacheMap[key]?.deleteGlTexture()
        cacheMap[key] = texture
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