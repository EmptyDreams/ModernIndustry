package top.kmar.mi.api.graphics.components

import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics

/**
 * 控件的客户端接口
 * @author EmptyDreams
 */
abstract class CmptClient(
    /** 服务端对象，一个客户端对象对应且仅对应一个服务端对象 */
    val service: Cmpt
) {

    /** 样式表 */
    val style = GraphicsStyle()

    /** 渲染所有子控件 */
    protected fun renderChildern(graphics: GuiGraphics) {
        service.forEachAllChildren {
            val client = it.client
            val g = graphics.createGraphics(style.x, style.y, style.width, style.height)
            client.render(g)
        }
    }

    /** 渲染这个控件及子控件 */
    abstract fun render(graphics: GuiGraphics)

}