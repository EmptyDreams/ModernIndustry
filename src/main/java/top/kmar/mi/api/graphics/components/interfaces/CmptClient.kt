package top.kmar.mi.api.graphics.components.interfaces

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataOperator
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
@SideOnly(Side.CLIENT)
interface CmptClient {

    /** 服务端对象，一个客户端对象对应且仅对应一个服务端对象 */
    val service: Cmpt

    /** 样式表 */
    val style: GraphicsStyle

    /** 接收从服务端发送的信息 */
    fun receive(message: IDataReader) {}

    /**
     * 发送信息到服务端
     * @param message 要发送的内容
     * @param isEvent 是否为事件通信
     */
    fun send2Service(message: IDataOperator, isEvent: Boolean = false) {
        val content = ByteDataOperator(message.size() + 1)
        content.writeBoolean(isEvent)
        content.writeData(message)
        val pack = GraphicsMessage.create(content, GraphicsAddition(service.id))
        MessageSender.send2Server(pack)
    }

    /** 渲染所有子控件 */
    fun renderChildren(graphics: GuiGraphics) {
        service.eachAllChildren {
            val client = it.client
            val style = client.style
            if (!style.display.isDisplay()) return@eachAllChildren
            val width = style.width()
            val height = style.height()
            if (width <= 0 || height <= 0) return@eachAllChildren
            val g = graphics.createGraphics(style.x, style.y, width, height)
            if (style.overflowHidden) g.scissor()
            client.render(g)
            if (style.overflowHidden) g.unscissor()
        }
    }

    /** 渲染这个控件及子控件 */
    fun render(graphics: GuiGraphics) {
        renderBackground(graphics)
        renderBorder(graphics)
        renderChildren(graphics)
    }

    /** 渲染背景 */
    fun renderBackground(graphics: GuiGraphics) {
        with(style) {
            graphics.fillRect(0, 0, width(), height(), backgroundColor)
        }
    }

    /** 渲染描边 */
    fun renderBorder(graphics: GuiGraphics) {
        with(style) {
            val width = this.width()
            val height = this.height()
            with(graphics) {
                fillRect(0, 0, width, borderTop.weight, borderTop.color)
                fillRect(width - borderRight.weight, 0, borderRight.weight, height, borderRight.color)
                fillRect(0, height - borderBottom.weight, width, borderBottom.weight, borderBottom.color)
                fillRect(0, 0, borderRight.weight, height, borderLeft.color)
            }
        }

    }

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