package top.kmar.mi.api.graphics.components

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.slots.ItemSlot
import top.kmar.mi.api.graphics.utils.CodeSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.floorDiv2

/**
 * 多个Slot组成的矩阵
 * @author EmptyDreams
 */
@AutoCmpt("matrix")
class SlotMatrixCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    /** slot起始下标 */
    var index: Int by attributes.toIntDelegate()
    /** slot的X轴数量 */
    var xCount: Int by attributes.toIntDelegate()
    /** slot的Y轴数量 */
    var yCount: Int by attributes.toIntDelegate()
    /** 优先级 */
    var priority: Int by attributes.toIntDelegate(100)
    /** 每个slot的尺寸 */
    var size: Int by attributes.toIntDelegate(18)
    /** 是否允许输入 */
    var input: Boolean
        get() = attributes["input", "true"].toBoolean()
        set(value) {
            attributes["input"] = value.toString()
        }
    /** slot的数量 */
    val count: Int
        get() = xCount * yCount

    var handler: ItemStackHandler? = null
        set(value) {
            if (field == null) field = value
        }
    val slots by lazy(LazyThreadSafetyMode.NONE) {
        val priority = this.priority
        val input = this.input
        Array(yCount) { y ->
            Array(xCount) { x ->
                ItemSlot(this, priority, handler!!, index + x + yCount * y).apply {
                    canPutIn = input
                }
            }
        }
    }

    override fun initClientObj() = SlotMatrixCmptClient()

    override fun buildNewObj() = SlotMatrixCmpt(attributes.copy())

    operator fun get(x: Int, y: Int): ItemSlot = slots[y][x]

    override fun receive(message: IDataReader) {
        val x = message.readVarInt()
        val y = message.readVarInt()
        syncPos(x, y)
    }

    override fun installParent(parent: Cmpt) {
        for (list in slots) {
            list.forEach { installSlot(it) }
        }
    }

    override fun uninstallParent(oldParent: Cmpt) {
        for (list in slots) {
            list.forEach { uninstallSlot(it) }
        }
    }

    @SideOnly(Side.CLIENT)
    inner class SlotMatrixCmptClient : CmptClient {

        override val service = this@SlotMatrixCmpt
        override val style = GraphicsStyle(service).apply {
            backgroundColor = IntColor.gray
            borderTop.color = IntColor.darkGray
            borderLeft.color = IntColor.darkGray
            borderRight.color = IntColor.white
            borderBottom.color = IntColor.white
            widthCalculator = CodeSizeMode { size * xCount }
            heightCalculator = CodeSizeMode { size * yCount }
        }

        override fun render(graphics: GuiGraphics) {
            val offset = (size - 18).floorDiv2() + 1
            syncPos(style.x + offset, style.y + offset)
            super.render(graphics)
        }

        override fun renderBackground(graphics: GuiGraphics) {
            with(style) {
                val left = borderLeft.weight
                val top = borderTop.weight
                val width = this.width - left - borderRight.weight
                val height = this.height - top - borderBottom.weight
                graphics.fillRect(left, top, width, height, backgroundColor)
            }
        }

        override fun renderBorder(graphics: GuiGraphics) {
            val size = service.size
            with(style) {
                for (i in 0 until yCount) {
                    // 绘制上边框
                    graphics.fillRect(0, i * size, width, borderTop.weight, borderTop.color)
                    // 绘制下边框
                    graphics.fillRect(
                        0, (i + 1) * size - borderBottom.weight,
                        width, borderBottom.weight,
                        borderBottom.color
                    )
                }
                for (i in 0 until xCount) {
                    // 绘制左边框
                    graphics.fillRect(i * size, 0, borderLeft.weight, height, borderLeft.color)
                    // 绘制右边框
                    graphics.fillRect(
                        (i + 1) * size - borderRight.weight, 0,
                        borderRight.weight, height,
                        borderRight.color
                    )
                }
                val rightWidth = borderRight.weight
                val rightHeight = borderTop.weight
                val bottomWidth = borderLeft.weight
                val bottomHeight = borderBottom.weight
                for (i in 0 until yCount) {
                    for (k in 0 until xCount) {
                        graphics.fillRect(
                            (k + 1) * size - rightWidth, i * size,
                            rightWidth, rightHeight,
                            backgroundColor
                        )
                        graphics.fillRect(
                            k * size, (i + 1) * size - bottomHeight,
                            bottomWidth, bottomHeight,
                            backgroundColor
                        )
                    }
                }
            }
        }

    }

    private fun syncPos(x: Int, y: Int) {
        if (this[0, 0].xPos != x || this[0, 0].yPos != y) {
            if (WorldUtil.isClient()) {
                val message = ByteDataOperator(5).apply {
                    writeVarInt(x)
                    writeVarInt(y)
                }
                client.send2Service(message)
            }
            val size = this.size
            for (i in slots.indices) {
                val list = slots[i]
                val yPos = i * size + y
                for (k in list.indices) {
                    list[k].xPos = k * size + x
                    list[k].yPos = yPos
                }
            }
        }
    }

}