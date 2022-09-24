package top.kmar.mi.api.graphics.components.interfaces

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.net.handler.MessageSender
import top.kmar.mi.api.net.message.graphics.GraphicsAddition
import top.kmar.mi.api.net.message.graphics.GraphicsMessage
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

    /** 接收从服务端发送的信息 */
    fun receive(message: IDataReader) {}

    /** 发送信息到服务端 */
    fun send2Service(message: IDataReader) {
        val pack = GraphicsMessage.create(message, GraphicsAddition(service.id))
        MessageSender.send2Server(pack)
    }

    /** 渲染所有子控件 */
    fun renderChildren(graphics: GuiGraphics) {
        service.eachAllChildren {
            val client = it.client
            val g = graphics.createGraphics(style.x, style.y, style.width, style.height)
            client.render(g)
        }
    }

    /** 渲染这个控件及子控件 */
    fun render(graphics: GuiGraphics)

    /** 查找鼠标所指的控件（子控件） */
    fun searchCmpt(x: Int, y: Int): Cmpt {
        val pos = Point2D(x, y)
        val result = service.eachChildren {
            val cl = it.client
            if (pos in cl) cl.searchCmpt(x, y) else null
        }
        return result ?: service
    }

    operator fun contains(pos: Point2D) = pos in style.area

}