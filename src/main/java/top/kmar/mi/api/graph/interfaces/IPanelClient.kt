package top.kmar.mi.api.graph.interfaces

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.utils.data.math.Point2D
import top.kmar.mi.api.utils.data.math.Size2D

/**
 * 所有控件的客户端接口
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
interface IPanelClient : IPanel {

    val x: Int
    val y: Int
    val width: Int
    val height: Int

    val size: Size2D
        get() = Size2D(width, height)
    val pos: Point2D
        get() = Point2D(x, y)

    override fun isClient() = true

    /** 向画笔中绘制图形 */
    fun paint(painter: GuiPainter)

}