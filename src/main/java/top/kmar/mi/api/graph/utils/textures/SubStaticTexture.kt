package top.kmar.mi.api.graph.utils.textures

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.utils.bindTexture
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.LazyThreadSafetyMode.PUBLICATION

/**
 * 子材质管理
 * @author EmptyDreams
 */
class SubStaticTexture(
    /** 材质资源路径 */
    val location: ResourceLocation,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) : AbstractTexture() {

    private var image: BufferedImage? = null
    private val subImage by lazy(PUBLICATION) { image!!.getSubimage(x, y, width, height) }

    constructor(
        modid: String, path: String,
        x: Int, y: Int, width: Int, height: Int
    ) : this(ResourceLocation(modid, path), x, y, width, height)

    constructor(
        path: String,
        x: Int, y: Int, width: Int, height: Int
    ) : this(ModernIndustry.MODID, path, x, y, width, height)

    override fun loadTexture(resourceManager: IResourceManager) {
        deleteGlTexture()
        image = getImage(resourceManager, location)
        TextureUtil.uploadTextureImageAllocate(glTextureId, image!!, false, true)
    }

    /**
     * 将材质绘制到画笔中
     * @param painter 画笔
     * @param x 在画笔中的X轴坐标
     * @param y 在画笔中的Y轴坐标
     */
    fun draw(painter: GuiPainter, x: Int = 0, y: Int = 0) {
        bindTexture()
        painter.drawTexture(x, y, this.x, this.y, width, height)
    }

    /**
     * 将材质绘制到画笔中
     * @see draw
     */
    fun draw(graphics: Graphics, x: Int = 0, y: Int = 0) {
        graphics.drawImage(subImage, x, y, null)
    }

    companion object {

        private val imageMap = Object2ObjectOpenHashMap<ResourceLocation, BufferedImage>()

        private fun getImage(resourceManager: IResourceManager, location: ResourceLocation) =
            imageMap.computeIfAbsent(location) { _ ->
                resourceManager.getResource(location).inputStream.use { ImageIO.read(it) }
            }

    }

}