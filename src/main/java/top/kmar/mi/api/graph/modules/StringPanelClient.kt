package top.kmar.mi.api.graph.modules

import net.minecraft.client.Minecraft
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.utils.data.math.Point2D

/**
 * 字符串显示控件
 * @author EmptyDreams
 */
class StringPanelClient(
    x: Int, y: Int, width: Int, height: Int,
    /** 要显示的文字 */
    var text: String = "",
    /** 文字颜色 */
    var color: Int = 0,
    /** 文字位置样式 */
    var locationStyle: LocationStyle = LocationStyle.CENTER
) : GeneralPanelClient(x, y, width, height) {

    val textWidth: Int
        get() = Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
    val textHeight: Int = 10

    override fun paint(painter: GuiPainter) {
        val textLocation = locationStyle(this)
        painter.drawString(textLocation.x, textLocation.y, text, color)
    }

    enum class LocationStyle(
        private val calculator: (StringPanelClient) -> Point2D
    ) {

        /** 左上角 */
        LEFT_AND_UP({ Point2D(0, 0) }),
        /** 右下 */
        LEFT_AND_DOWN({ Point2D(0, it.height - it.textHeight) }),
        /** 左侧居中 */
        LEFT_AND_CENTER({ Point2D(0, it.height.shr(1) - it.textHeight.shr(1)) }),
        /** 右上角 */
        RIGHT_AND_UP({ Point2D(it.width - it.textWidth, 0) }),
        /** 右下角 */
        RIGHT_AND_DOWN({ Point2D(it.width - it.textWidth, it.height - it.textHeight) }),
        /** 右侧居中 */
        RIGHT_AND_CENTER({
            val x = it.width - it.textWidth
            val y = it.height.shr(1) - it.textHeight.shr(1)
            Point2D(x, y)
        }),
        /** 中央靠上 */
        CENTER_AND_UP({ Point2D(it.width.shr(1) - it.textWidth.shr(1), 0) }),
        /** 中央靠下 */
        CENTER_AND_DOWN({
            val x = it.width.shr(1) - it.textWidth.shr(1)
            val y = it.height - it.textHeight
            Point2D(x, y)
        }),
        /** 居中 */
        CENTER({
            val x = it.width.shr(1) - it.textWidth.shr(1)
            val y = it.height.shr(1) - it.textHeight.shr(1)
            Point2D(x, y)
        });

        operator fun invoke(panel: StringPanelClient) = calculator(panel)

    }

}