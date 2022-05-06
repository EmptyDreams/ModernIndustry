package top.kmar.mi.api.graph.modules

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.gui.client.RuntimeTexture
import top.kmar.mi.api.utils.data.math.Size2D
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.util.*

/**
 * GUI背景板
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class BackgroundPanelClient(
    x: Int, y: Int, width: Int, height: Int
) : GeneralPanelClient(x, y, width, height) {

    override fun paint(painter: GuiPainter) {
        val texture = getTexture(width, height).bindTexture()
        painter.drawTexture(0, 0, width, height, texture)
    }

    // ---------- 绘制&缓存图像 ---------- //

    companion object {

        private val cache = WeakHashMap<Size2D, RuntimeTexture>()

        fun getTexture(width: Int, height: Int) = getTexture(Size2D(width, height))

        /**
         * 绘制GUI背景图像
         *
         * @param size 图像尺寸（包含边界），长宽均必须大于等于 20
         *
         * @throws IllegalArgumentException 如果传入的尺寸过小
         */
        fun getTexture(size: Size2D): RuntimeTexture {
            if (size.width < 20 || size.height < 20)
                throw IllegalArgumentException("该函数仅能绘制尺寸大于等于20x20的图像")
            return cache.computeIfAbsent(size) {
                run {
                    val image = BufferedImage(size.width, size.height, 6)
                    val painter = image.createGraphics()
                    paintBlackBorder(size, painter)
                    paintCenter(size, painter)
                    paintChunk(size, painter)
                    painter.dispose()
                    RuntimeTexture.instanceNoCache(image)
                }
            }
        }

        /** 绘制四周黑色边框 */
        private fun paintBlackBorder(size: Size2D, painter: Graphics) {
            with(painter) {
                color = Color.BLACK
                drawRect(3, 0, size.width - 7, 2)   //上侧
                drawLine(0, 3, 0, size.height - 6)      //左侧
                drawLine(1, 1, 2, 1)                    //左上角
                drawLine(size.width - 4, 2, size.width - 4, 2)  //右上角
                drawRect(size.width - 3, 3, 2, 2)
                drawLine(size.width - 1, 5, size.width - 1, size.height - 4)    //右侧
                drawRect(4, size.height - 3, size.width - 7, 2) //下侧
                drawLine(size.width - 3, size.height - 3, size.width - 2, size.height - 3)  //右下角
                drawLine(3, size.height - 3, 3, size.height - 3)    //左下角
                drawRect(1, size.height - 5, 2, 2)
            }
        }

        /** 绘制白色和灰色区块 */
        private fun paintChunk(size: Size2D, painter: Graphics) {
            with(painter) {
                color = Color.WHITE
                fillRect(3, 2, size.width - 7, 3)
                fillRect(1, 3, 3, size.height - 8)
                drawRect(4, 5, 2, 2)
                color = Color(85, 85, 85)
                fillRect(4, size.height - 5, size.width - 7, 3)
                fillRect(size.width - 4, 5, 3, size.height - 8)
                drawRect(size.width - 6, size.height - 7, 2, 2)
            }
        }

        /** 绘制中央区域 */
        private fun paintCenter(size: Size2D, painter: Graphics) {
            with(painter) {
                color = Color(198, 198, 198)
                fillRect(4, 5, size.width - 8, size.height - 10)
                drawLine(3, size.height - 5, 3, size.height - 4)
                drawLine(size.width - 4, 3, size.width - 4, 4)
            }
        }

    }

}