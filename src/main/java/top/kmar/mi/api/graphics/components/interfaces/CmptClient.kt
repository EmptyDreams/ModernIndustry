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
import top.kmar.mi.api.graphics.utils.modes.AutoSizeMode
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.graphics.utils.style.StyleSheet
import top.kmar.mi.api.net.messages.GraphicsMessage
import top.kmar.mi.api.utils.container.CacheContainer
import top.kmar.mi.api.utils.data.math.Point2D
import top.kmar.mi.api.utils.data.mutable.MutableData2D
import top.kmar.mi.api.utils.expands.correct
import java.util.*
import kotlin.math.roundToInt

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
        @JvmName("_set x")
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
        @JvmName("_set y")
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
        /** 请勿手动调用该函数 */
        @JvmName("_set width")
        internal set(value) {
            if (value != field) {
                field = value
                xLayoutUpdateFlag = true
                isTypeset = false
                scrollRange.clear()
            }
        }
        get() {
            if (xLayoutUpdateFlag) {
                field = style.width(this)
                xLayoutUpdateFlag = false
            }
            return field
        }
    /** 控件高度（content + padding） */
    var height: Int = 0
        /** 请勿手动调用该函数 */
        @JvmName("_set height")
        internal set(value) {
            if (value != field) {
                field = value
                yLayoutUpdateFlag = true
                isTypeset = false
                scrollRange.clear()
            }
        }
        get() {
            if (yLayoutUpdateFlag) {
                field = style.height(this)
                yLayoutUpdateFlag = false
            }
            return field
        }

    /** 滚动范围限制 */
    private val scrollRange = CacheContainer {
        val yRange: IntRange = if (style.height is AutoSizeMode) {
            0 .. 0
        } else {
            val childrenHeight = group.height
            0 .. (childrenHeight - contentHeight).coerceAtLeast(0)
        }
        val xRange: IntRange = if (style.width is AutoSizeMode) {
            0 .. 0
        } else {
            val childrenWidth = group.width
            0 .. (childrenWidth - contentWidth).coerceAtLeast(0)
        }
        MutableData2D(xRange, yRange)
    }
    /** Y 轴滚动条 */
    @Suppress("DuplicatedCode")
    var scrollY: Int = 0
        set(value) {
            val item = scrollRange().y.correct(value)
            if (item != field) {
                val dif = item - field
                group.forEach { line ->
                    line.forEach { it.y -= dif }
                }
                field = item
            }
        }
    /** X 轴滚动条 */
    @Suppress("DuplicatedCode")
    var scrollX: Int = 0
        set(value) {
            val item = scrollRange().x.correct(value)
            if (item != field) {
                val dif = item - field
                group.forEach { line ->
                    line.forEach { it.x -= dif }
                }
                field = item
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
        get() {
            val scroll = if (style.overflowX.isScroll) 14 else 0
            return width - style.paddingLeft - style.paddingRight - scroll
        }
    /** 控件 content 区域高度 */
    val contentHeight: Int
        get() {
            val scroll = if (style.overflowY.isScroll) 14 else 0
            return height - style.paddingTop - style.paddingBottom - scroll
        }

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
        style.alignX(this, group)
        style.alignY(this, group)
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
            val width = client.contentWidth
            val height = client.contentHeight
            if (width <= 0 || height <= 0) return@eachAllChildren
            val overflowX = style.overflowX
            val overflowY = style.overflowY
            val clip = overflowX.isClip || overflowY.isClip
            val g = graphics.createGraphics(client.x, client.y, width, height)
            if (clip) g.scissor()
            client.render(g)
            if (clip) g.unscissor()
        }
    }

    /** 渲染这个控件及子控件 */
    open fun render(graphics: GuiGraphics) {
        renderBackground(graphics)
        renderBorder(graphics)
        renderChildren(graphics)
        if (style.overflowX.isScroll)
            renderScrollX(graphics.createGraphics(0, height - 14, width, 14))
        if (style.overflowY.isScroll)
            renderScrollY(graphics.createGraphics(width - 14, 0, width, height))
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
            if (pos in cl) cl.searchCmpt(x - cl.x, y - cl.y) else null
        }
        return result ?: service
    }

    operator fun contains(pos: Point2D): Boolean =
        pos.x >= x && pos.y >= y && pos.x < x + width && pos.y < y + height

    /** 绘制垂直的滚动条 */
    private fun renderScrollY(graphics: GuiGraphics) {
        val percent = scrollY.toFloat() / scrollRange().y.last
        val blockPos = (percent * (contentHeight - 17)).roundToInt() + 1
        renderBackground(graphics)
        with(graphics) {
            bindTexture(BaseGraphicsClient.textureKey)
            if (scrollRange().y.last == 0)
                drawTexture64(1, blockPos, 12, 13, 12, 15)
            else
                drawTexture64(1, blockPos, 0, 13, 12, 15)
        }
    }

    /** 绘制水平的滚动条 */
    private fun renderScrollX(graphics: GuiGraphics) {
        val percent = scrollX.toFloat() / scrollRange().x.last
        val blockPos = (percent * (contentWidth - 17)).roundToInt() + 1
        renderBackground(graphics)
        with(graphics) {
            bindTexture(BaseGraphicsClient.textureKey)
            if (scrollRange().x.last == 0)
                drawTexture64(blockPos, 1, 0, 28 , 15, 12)
            else
                drawTexture64(blockPos, 1, 0, 40, 15, 12)
        }
    }

    /** 绘制滚动条背景 */
    private fun renderScrollBackground(graphics: GuiGraphics) {
        with(graphics) {
            // 绘制中央背景色块
            fillRect(0, 0, width, height, IntColor.gray)
            // 绘制左侧和顶部的阴影
            fillRect(0, 1, 1, height - 2, IntColor.lightBlack)
            fillRect(0, 0, width - 1, 1, IntColor.lightBlack)
            // 绘制右侧和底部的高亮
            fillRect(width - 1, 1, 1, height - 2, IntColor.white)
            fillRect(1, height - 1, width - 1, 1, IntColor.white)
        }
    }

}