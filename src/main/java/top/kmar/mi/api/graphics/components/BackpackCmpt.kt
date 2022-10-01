package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.slots.BackpackSlot
import top.kmar.mi.api.graphics.utils.*
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.toInt
import java.awt.Color
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 玩家背包
 * @author EmptyDreams
 */
@AutoCmpt("backpack")
class BackpackCmpt(private val attribute: CmptAttributes) : Cmpt(attribute.id) {

    override fun initClientObj() = BackpackCmptClient()

    var player: EntityPlayer? = null
        set(value) {
            if (field == null) field = value
        }

    /** 活动的九个物品栏 */
    val activeSlots by lazy(NONE) {
        Array(9) {
            BackpackSlot(this, attribute["index", "200"].toInt(), player!!, it)
        }
    }

    /** 背包栏 */
    val mainSlots by lazy(NONE) {
        Array(3) { y ->
            Array(9) { x ->
                BackpackSlot(this, attribute["index", "500"].toInt(), player!!, 9 + x + y * 9)
            }
        }
    }

    override fun installParent(parent: Cmpt) {
        super.installParent(parent)
        activeSlots.forEach { installSlot(it) }
        for (slots in mainSlots) {
            slots.forEach { installSlot(it) }
        }
    }

    override fun receive(message: IDataReader) {
        val x = message.readVarInt()
        val y = message.readVarInt()
        initSlotsPos(x, y)
    }

    @SideOnly(Side.CLIENT)
    inner class BackpackCmptClient : CmptClient {

        override val service = this@BackpackCmpt
        override val style = GraphicsStyle(service).apply {
            position = PositionEnum.ABSOLUTE
            width = InheritSizeMode { service.parent.client.style.width() }
            height = FixedSizeMode(18 * 4 + 4)
            bottom = 7
            backgroundColor = Color(139, 139, 139)
            borderTop.color = Color(55, 55, 55)
            borderLeft.color = borderTop.color
            borderBottom.color = Color.WHITE
            borderRight.color = Color.WHITE
        }

        override fun render(graphics: GuiGraphics) {
            val firstSlot = mainSlots[0][0]
            val offsetX = (style.width() - (18 * 9)) shr 1
            val startX = offsetX + style.x
            val startY = style.y
            if (startX != firstSlot.xPos || startY != firstSlot.yPos) {
                initSlotsPos(startX, startY)
                val message = ByteDataOperator().apply {
                    writeVarInt(startX)
                    writeVarInt(startY)
                }
                send2Service(message)
            }
            renderContent(graphics, offsetX, 0)
            renderChildren(graphics)
        }

        private fun renderContent(graphics: GuiGraphics, startX: Int, startY: Int) {
            val width = 18 * 9
            with (style) {
                val background = backgroundColor.toInt()
                // 绘制物品栏背景
                graphics.fillRect(startX, startY, width, 18 * 3, background)
                graphics.fillRect(startX, startY + 18 * 3 + 4, width, 18, background)
                // 绘制横条
                for (y in mainSlots.indices) {
                    val pos = y * 18
                    graphics.fillRect(startX, pos, width, 1, borderTop.color.toInt())
                    graphics.fillRect(startX, pos + 17, width, 1, borderBottom.color.toInt())
                }
                graphics.fillRect(startX, 18 * 3 + 4, width, 1, borderTop.color.toInt())
                graphics.fillRect(startX, 18 * 3 + 4 + 17, width, 1, borderBottom.color.toInt())
                // 绘制竖条
                for (x in 0 until 9) {
                    val pos = startX + x * 18
                    val y = 18 * 3 + 4
                    graphics.fillRect(pos, 0, 1, 18 * 3, borderLeft.color.toInt())
                    graphics.fillRect(pos, y, 1, 18, borderLeft.color.toInt())
                    graphics.fillRect(pos + 17, 0, 1, 18 * 3, borderRight.color.toInt())
                    graphics.fillRect(pos + 17, y, 1, 18, borderRight.color.toInt())
                }
                // 绘制交叉点
                for (y in 0 until 3) {
                    for (x in 0 until 9) {
                        graphics.fillRect(startX + x * 18 + 17, y * 18, 1, 1, background)
                        graphics.fillRect(startX + x * 18, y * 18 + 17, 1, 1, background)
                    }
                }
                for (x in 0 until 9) {
                    val y = 18 * 3 + 4
                    graphics.fillRect(startX + x * 18 + 17, y, 1, 1, background)
                    graphics.fillRect(startX + x * 18, y + 17, 1, 1, background)
                }
            }
        }

    }

    private fun initSlotsPos(startX: Int, startY: Int) {
        var y = startY + 1
        for (slots in mainSlots) {
            for ((i, it) in slots.withIndex()) {
                it.yPos = y
                it.xPos = startX + 18 * i + 1
            }
            y += 18
        }
        y += 4
        for ((i, it) in activeSlots.withIndex()) {
            it.yPos = y
            it.xPos = startX + 18 * i + 1
        }
    }

}