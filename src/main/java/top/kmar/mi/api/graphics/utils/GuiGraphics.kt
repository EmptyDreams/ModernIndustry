package top.kmar.mi.api.graphics.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import top.kmar.mi.api.graphics.BaseGraphicsClient
import top.kmar.mi.api.utils.data.math.Rect2D

/**
 * 画笔
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class GuiGraphics(
    /** 绘制区域起始X坐标（相对于窗体） */
    val x: Int,
    /** 绘制区域起始Y坐标（相对于窗体） */
    val y: Int,
    /** 绘制区域宽度 */
    val width: Int,
    /** 绘制区域高度 */
    val height: Int,
    /** 画笔所属的GUI窗体 */
    val container: BaseGraphicsClient
) {

    /** 超出控件绘制区域的内容是否裁剪 */
    var overflowHidden = true

    /** 以指定点为中心绘制字符串 */
    fun drawStringCenter(centerX: Int, centerY: Int, text: String, color: Int = 0) {
        val render = Minecraft.getMinecraft().fontRenderer
        val width = render.getStringWidth(text)
        val height = render.FONT_HEIGHT
        val x = centerX - (width shr 1)
        val y = centerY - (height shr 1)
        drawString(x, y, text, color)
    }

    /** 绘制一个字符串 */
    fun drawString(x: Int, y: Int, text: String, color: Int = 0) {
        scissor()
        val left = this.x + x
        val top = this.y + y
        Minecraft.getMinecraft().fontRenderer.drawString(text, left, top, color)
        unscissor()
    }

    /** 装载指定材质 */
    fun bindTexture(location: ResourceLocation) {
        container.mc.textureManager.bindTexture(location)
    }

    /**
     * 绘制材质
     * @param x 绘制区域X坐标
     * @param y 绘制区域Y坐标
     * @param u 要绘制的内容在材质中的X坐标
     * @param v 要绘制的内容在材质中的Y坐标
     * @param width 要绘制的内容的宽度
     * @param height 要绘制的内容的宽度
     */
    fun drawTexture(x: Int, y: Int, u: Int, v: Int, width: Int, height: Int) {
        scissor()
        val left = this.x + x
        val top = this.y + y
        GlStateManager.color(1f, 1f, 1f, 1f)
        container.drawTexturedModalRect(left, top, u, v, width, height)
        unscissor()
    }

    /** 按照指定颜色填充矩形 */
    fun fillRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        val left = x + this.x
        val top = y + this.y
        val right = left + width
        val bottom = top + height
        scissor()
        Gui.drawRect(left, top, right, bottom, color)
        unscissor()
    }

    /**
     * 创建一个子画笔并继承当前画笔的设置
     * @param x 子画笔的X轴坐标（相对于窗体）
     * @param y 子画笔的Y轴坐标（相对于窗体）
     * @param width 子画笔宽度
     * @param height 子画笔高度
     */
    fun createGraphics(x: Int, y: Int, width: Int, height: Int): GuiGraphics {
        val thisRect = Rect2D(this.x, this.y, this.width, this.height)
        val thatRect = Rect2D(x, y, width, height)
        val rect = thisRect.intersect(thatRect)
        return GuiGraphics(rect.x, rect.y, rect.width, rect.height, container).apply {
            overflowHidden = this@GuiGraphics.overflowHidden
        }
    }

    /** 裁剪区域 */
    private val clipRect: Rect2D by lazy(LazyThreadSafetyMode.NONE) {
        val mc = Minecraft.getMinecraft()
        val res = ScaledResolution(mc)
        val scaleViewX = mc.displayWidth / res.scaledWidth_double
        val scaleViewY = mc.displayHeight / res.scaledHeight_double
        val realX = ((x + container.guiLeft) * scaleViewX).toInt()
        val realY = (mc.displayHeight - (y + container.guiTop + height) * scaleViewY).toInt()
        val realWidth = if (width > 0) (width * scaleViewX).toInt() else -1
        val realHeight = if (height > 0) (height * scaleViewY).toInt() else -1
        Rect2D(realX, realY, realWidth, realHeight)
    }

    /**
     * 通知GL开启裁剪
     * @return 是否进行了裁剪
     */
    private fun scissor(): Boolean {
        if (!clipRect.isEffective ||
            clipRect.endX < 0 || clipRect.endY < 0 ||
            !overflowHidden) return false
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(clipRect.x, clipRect.y, clipRect.width, clipRect.height)
        return true
    }

    /** 通知GL结束裁剪 */
    private fun unscissor() = GL11.glDisable(GL11.GL_SCISSOR_TEST)

}