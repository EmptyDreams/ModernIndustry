package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphicsClient
import top.kmar.mi.api.graphics.utils.CmptClientGroup
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.graphics.utils.style.StyleSheet
import top.kmar.mi.api.net.messages.GraphicsMessage
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
    internal val group = CmptClientGroup(service)

    /** 相对于其父元素的 X 轴坐标 */
    var x: Int = 0
        /** 请勿手动修改 */
        internal set(value) {
            field = value
            localX = Int.MIN_VALUE
            localY = Int.MIN_VALUE
        }
        get() {
            parent.typeset()
            return field
        }
    /** 相对于其父元素的 Y 轴坐标 */
    var y: Int = 0
        /** 请勿手动修改 */
        internal set(value) {
            field = value
            localY = Int.MIN_VALUE
            globalY = Int.MIN_VALUE
        }
        get() {
            parent.typeset()
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
                xLayoutUpdateFlag = true
                isTypeset = false
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
                yLayoutUpdateFlag = true
                isTypeset = false
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

    /** 组件相对于 GUI 的 X 坐标 */
    open var localX: Int = Int.MIN_VALUE
        internal set
        get() {
            if (field == Int.MIN_VALUE)
                field = parent.localX + x
            return field
        }
    /** 组件相对于 GUI 的 Y 坐标 */
    open var localY: Int = Int.MIN_VALUE
        internal set
        get() {
            if (field == Int.MIN_VALUE)
                field = parent.localY + y
            return field
        }
    /** 组件相对于窗体的 X 坐标 */
    open var globalX: Int = Int.MIN_VALUE
        internal set
        get() {
            if (field == Int.MIN_VALUE)
                field = parent.globalX + x
            return field
        }
    /** 组件相对于窗体的 Y 坐标 */
    open var globalY: Int = Int.MIN_VALUE
        internal set
        get() {
            if (field == Int.MIN_VALUE)
                field = parent.globalY + y
            return field
        }

    val parent: CmptClient
        get() = service.parent.client

    /** 标记横向布局更新 */
    fun markXLayoutUpdate() {
        xLayoutUpdateFlag = true
        group.clear()
        isTypeset = false
    }

    /** 标记纵向布局更新 */
    fun markYLayoutUpdate() {
        yLayoutUpdateFlag = true
        group.clear()
        isTypeset = false
    }

    private var isTypeset = false
    /** 自动排版 */
    open fun typeset() {
        if (isTypeset) return
        isTypeset = true
        style.alignHorizontal(this, group)
        style.alignVertical(this, group)
        group.absoluteList.forEach {
            val style = it.style
            when {
                "left" in style -> it.x = style.left + style.marginLeft
                "right" in style -> it.x = contentWidth - it.width - style.right - style.marginRight
                else -> it.x = style.marginLeft
            }
            when {
                "top" in style -> it.y = style.top + style.marginTop
                "bottom" in style -> it.y = contentHeight - it.height - style.bottom - style.marginBottom
                else -> it.y = style.marginTop
            }
        }
        group.fixedList.forEach {
            val style = it.style
            val offsetX = when {
                "left" in style -> style.left
                "right" in style -> gui.width - it.width - style.right - style.marginRight
                else -> 0
            }
            it.x = offsetX - parent.localX
            val offsetY = when {
                "top" in style -> style.top
                "bottom" in style -> gui.height - it.height - style.bottom - style.marginBottom
                else -> 0
            }
            it.y = offsetY - parent.localY
        }
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
            val width = client.width
            val height = client.height
            if (width <= 0 || height <= 0) return@eachAllChildren
            val g = graphics.createGraphics(client.x, client.y, width, height)
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