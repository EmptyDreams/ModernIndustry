package top.kmar.mi.api.graphics.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import top.kmar.mi.api.graphics.BaseGraphicsClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
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

    /** 文字渲染器 */
    var fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRenderer

    /** 以指定点为中心绘制字符串 */
    fun drawStringCenter(centerX: Int, centerY: Int, text: String, color: IntColor = IntColor.black) {
        val render = Minecraft.getMinecraft().fontRenderer
        val width = render.getStringWidth(text)
        val height = render.FONT_HEIGHT
        val x = centerX - (width shr 1)
        val y = centerY - (height shr 1)
        drawString(x, y, text, color)
    }

    /** 绘制一个字符串 */
    fun drawString(x: Int, y: Int, text: String, color: IntColor = IntColor.black) {
        val left = this.x + x
        val top = this.y + y
        fontRenderer.drawString(text, left, top, color.value)
    }

    /** 装载指定材质 */
    fun bindTexture(location: ResourceLocation) {
        container.mc.textureManager.bindTexture(location)
    }

    /** 绘制256*256的材质 */
    fun drawTexture256(x: Int, y: Int, u: Int, v: Int, width: Int, height: Int) {
        drawTexture(x, y, u, v, width, height, 256, 256)
    }

    /** 绘制32*32的材质 */
    fun drawTexture32(x: Int, y: Int, u: Int, v: Int, width: Int, height: Int) {
        drawTexture(x, y, u, v, width, height, 32, 32)
    }

    /**
     * 绘制自定义尺寸的材质
     * @param x 绘制区域X坐标
     * @param y 绘制区域Y坐标
     * @param u 要绘制的内容在材质中的X坐标
     * @param v 要绘制的内容在材质中的Y坐标
     * @param width 要绘制的内容的宽度
     * @param height 要绘制的内容的宽度
     * @param textureWidth 材质文件宽度
     * @param textureHeight 材质文件高度
     */
    fun drawTexture(x: Int, y: Int, u: Int, v: Int, width: Int, height: Int, textureWidth: Int, textureHeight: Int) {
        val left = this.x + x
        val top = this.y + y
        GlStateManager.color(1f, 1f, 1f)
        Gui.drawModalRectWithCustomSizedTexture(
            left, top, u.toFloat(), v.toFloat(),
            width, height, textureWidth.toFloat(), textureHeight.toFloat()
        )
    }

    /** 按照指定颜色填充矩形 */
    fun fillRect(x: Int, y: Int, width: Int, height: Int, color: IntColor) {
        if (width <= 0 || height <= 0) return
        val left = x + this.x
        val top = y + this.y
        val right = left + width
        val bottom = top + height
        Gui.drawRect(left, top, right, bottom, color.value)
    }

    /**
     * 绘制等腰梯形
     * @param x 绘制区域起始X轴坐标
     * @param y 绘制区域起始Y轴坐标
     * @param length 短边长度
     * @param cout 梯形的高
     * @param direction 短边方向
     * @param color 填充色
     */
    fun fillTrapezoidal(x: Int, y: Int, length: Int, cout: Int, direction: Direction2DEnum, color: IntColor) {
        if (length < 1 || cout < 1) return
        fun drawHelper(
            startX: Int, startY: Int, xSize: Int, ySize: Int,
            xStep: Int, yStep: Int, xSizeStep: Int, ySizeStep: Int
        ) {
            var u = startX
            var v = startY
            var width = xSize
            var height = ySize
            for (i in 0 until cout) {
                fillRect(u, v, width, height, color)
                u += xStep
                v += yStep
                width += xSizeStep
                height += ySizeStep
            }
        }
        val startX = x + cout - 1
        val startY = y + cout - 1
        when (direction) {
            Direction2DEnum.UP ->
                drawHelper(startX, y, length, 1, -1, 1, 2, 0)
            Direction2DEnum.DOWN ->
                    drawHelper(startX, startY, length, 1, -1, -1, 2, 0)
            Direction2DEnum.LEFT ->
                drawHelper(x, startY, 1, length, 1, -1, 0, 2)
            Direction2DEnum.RIGHT ->
                drawHelper(startX, startY, 1, length, -1, -1, 0, 2)
        }
    }

    /**
     * 绘制等腰三角形
     * @param x 绘制区域起始X坐标
     * @param y 绘制区域起始Y坐标
     * @param height 三角形的高
     * @param direction 三角形朝向
     * @param color 填充颜色
     */
    fun fillTriangle(x: Int, y: Int, height: Int, direction: Direction2DEnum, color: IntColor) {
        fillTrapezoidal(x, y, 1, height, direction, color)
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
        return GuiGraphics(rect.x, rect.y, rect.width, rect.height, container)
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
    fun scissor(): Boolean {
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(clipRect.x, clipRect.y, clipRect.width, clipRect.height)
        return true
    }

    /** 通知GL结束裁剪 */
    fun unscissor() = GL11.glDisable(GL11.GL_SCISSOR_TEST)

}