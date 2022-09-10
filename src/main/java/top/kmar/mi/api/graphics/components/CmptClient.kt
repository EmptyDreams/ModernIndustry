package top.kmar.mi.api.graphics.components

import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.utils.data.math.Point2D

/**
 * 控件的客户端接口
 * @author EmptyDreams
 */
interface CmptClient {

    /** 服务端对象，一个客户端对象对应且仅对应一个服务端对象 */
    val service: Cmpt

    /** 样式表 */
    val style: GraphicsStyle

    /** 渲染所有子控件 */
    fun renderChildern(graphics: GuiGraphics) {
        service.eachAllChildren {
            val client = it.client
            val g = graphics.createGraphics(style.x, style.y, style.width, style.height)
            client.render(g)
        }
    }

    /** 渲染这个控件及子控件 */
    fun render(graphics: GuiGraphics)

    /**
     * 遍历在指定位置的控件，当指定位置有多个控件时使用DFS遍历，该函数不会遍历其自身
     *
     * @param function 返回非`null`值会使遍历中断
     * @return 返回`function`的返回值
     */
    fun eachChildrenAtPoint(x: Int, y: Int, function: (CmptClient) -> CmptClient?): CmptClient? {
        val pos = Point2D(x - style.x, y - style.y)
        return service.eachChildren {
            it.client.run {
                if (pos in style.area)
                    (function(this) ?: eachChildrenAtPoint(x, y, function))?.service
                else null
            }
        }?.client
    }

}