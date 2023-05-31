package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphicsClient
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.modes.DisplayModeEnum
import top.kmar.mi.api.graphics.utils.modes.PositionEnum
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.graphics.utils.style.StyleSheet
import top.kmar.mi.api.net.messages.GraphicsMessage
import top.kmar.mi.api.utils.container.CacheContainer
import top.kmar.mi.api.utils.data.math.Point2D
import java.util.*

/**
 * 控件的客户端接口
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
abstract class CmptClient(
    /** 服务端对象，一个客户端对象对应且仅对应一个服务端对象 */
    val service: Cmpt
) {

    val gui: BaseGraphicsClient
        get() = service.gui!!.client

    /** 样式表 ID */
    internal var styleIdList: IntList = IntArrayList(0)
    private val sheet: StyleSheet
        get() = gui.style
    /** 样式 */
    open var style = StyleNode()
        get() {
            if (styleIdList.isEmpty()) {
                styleIdList = sheet.getIndex(this)
                val style = defaultStyle()
                style.merge(sheet.getStyle(styleIdList))
                field = style
            }
            return field
        }

    /** 横向布局更新标记，当值为 flag 时，访问 width 会重新计算值 */
    private var xLayoutUpdateFlag = true
    /** 纵向布局更新标记，当值为 flag 时，访问 height 会重新计算值 */
    private var yLayoutUpdateFlag = true
    /** 分组信息 */
    internal val group = CacheContainer<List<List<CmptClient>>> {
        val result = LinkedList<LinkedList<CmptClient>>()
        var prevDisplay = DisplayModeEnum.NONE
        for (cmpt in service.childrenIterator()) {
            val style = cmpt.client.style
            val display = style.display
            if (display == DisplayModeEnum.NONE || style.position != PositionEnum.RELATIVE)
                continue
            if (display != prevDisplay) {
                result.add(LinkedList())
                prevDisplay = display
            }
            result.last += cmpt.client
        }
        result
    }

    /** 相对于其父元素的 X 轴坐标 */
    var x: Int = 0
        /** 请勿手动修改 */
        internal set
        get() {
            parent.typesetting()
            return field
        }
    /** 相对于其父元素的 Y 轴坐标 */
    var y: Int = 0
        /** 请勿手动修改 */
        internal set
        get() {
            parent.typesetting()
            return field
        }
    /** 控件宽度（content + padding） */
    var width: Int = 0
        get() {
            if (xLayoutUpdateFlag) {
                field = style.width(this)
                xLayoutUpdateFlag = false
            }
            return field
        }
        set(value) {
            if (value != field) {
                field = value
                group.clear()
                xLayoutUpdateFlag = true
            }
        }
    /** 控件高度（content + padding） */
    var height: Int = 0
        get() {
            if (yLayoutUpdateFlag) {
                field = style.height(this)
                yLayoutUpdateFlag = false
            }
            return field
        }
        set(value) {
            if (value != field) {
                field = value
                group.clear()
                yLayoutUpdateFlag = true
            }
        }

    /** 控件占用空间的宽度（content + padding + margin） */
    val spaceWidth: Int
        get() = width + style.marginLeft + style.marginRight
    /** 控件占用空间的宽度（content + padding + margin） */
    val spaceHeight: Int
        get() = height + style.marginTop + style.marginBottom
    /** 控件 content 区域宽度 */
    val contentWidth: Int
        get() = width - style.paddingLeft - style.paddingRight
    /** 控件 content 区域高度 */
    val contentHeight: Int
        get() = height - style.paddingTop - style.paddingBottom

    val parent: CmptClient
        get() = service.parent.client

    /** 标记横向布局更新 */
    fun markXLayoutUpdate() {
        xLayoutUpdateFlag = true
        group.clear()
    }

    /** 标记纵向布局更新 */
    fun markYLayoutUpdate() {
        yLayoutUpdateFlag = true
        group.clear()
    }

    open fun typesetting() {
        if (group.isInit) return
        val list = group.get()
        style.alignHorizontal(this, list)
        style.alignVertical(this, list)
    }

    /** 获取缺省的样式，该函数的结果不应当被缓存 */
    protected open fun defaultStyle(): StyleNode = StyleNode()

    /** 接收从服务端发送的信息 */
    open fun receive(message: NBTBase) {}

    /**
     * 发送信息到服务端
     * @param message 要发送的内容
     * @param isEvent 是否为事件通信
     */
    fun send2Service(message: NBTBase, isEvent: Boolean = false) {
        require(!isEvent || message is NBTTagString) { "当进行事件通信时 message 应当为 NBTTagString" }
        val content = NBTTagCompound()
        content.setBoolean("event", isEvent)
        content.setTag("data", message)
        GraphicsMessage.sendToServer(content, service.id)
    }

    /** 渲染所有子控件 */
    open fun renderChildren(graphics: GuiGraphics) {
        service.eachAllChildren {
            val client = it.client
            val style = client.style
            if (!style.display.isDisplay()) return@eachAllChildren
            val width = contentWidth
            val height = contentHeight
            if (width <= 0 || height <= 0) return@eachAllChildren
            val g = graphics.createGraphics(x + style.paddingLeft, y + style.paddingTop, width, height)
            //if (style.overflowHidden)
            g.scissor()
            client.render(g)
            //if (style.overflowHidden)
            g.unscissor()
        }
    }

    /** 渲染这个控件及子控件 */
    open fun render(graphics: GuiGraphics) {
        renderBackground(graphics)
        renderBorder(graphics)
        renderChildren(graphics)
    }

    /** 渲染背景 */
    open fun renderBackground(graphics: GuiGraphics) {
        with(graphics) {
            fillRect(0, 0, width, height, style.backgroundColor)
        }
    }

    /** 渲染描边 */
    open fun renderBorder(graphics: GuiGraphics) {
        with(style) {
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

    operator fun contains(pos: Point2D): Boolean =
        pos.x >= x && pos.y >= y && pos.x < x + width && pos.y < y + height

}