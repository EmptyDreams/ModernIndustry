package top.kmar.mi.api.graph.interfaces

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.utils.GuiPainter

/**
 * 所有控件的客户端接口
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
interface IPaneClient : IPanel {

    val x: Int
    val y: Int
    val width: Int
    val height: Int

    override fun isClient() = true

    /** 向画笔中绘制图形 */
    fun paint(painter: GuiPainter)

}