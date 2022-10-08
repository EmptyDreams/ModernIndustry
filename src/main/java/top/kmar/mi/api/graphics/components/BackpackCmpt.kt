package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.slots.BackpackSlot
import top.kmar.mi.api.graphics.utils.*
import top.kmar.mi.api.register.others.AutoCmpt
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 玩家背包
 * @author EmptyDreams
 */
@AutoCmpt("backpack")
class BackpackCmpt(attribute: CmptAttributes) : Cmpt(attribute) {

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

    companion object {

        @SideOnly(Side.CLIENT)
        @JvmStatic
        val textureLib = ResourceLocation("textures/gui/container/creative_inventory/tab_inventory.png")

    }

    @SideOnly(Side.CLIENT)
    inner class BackpackCmptClient : CmptClient {

        override val service = this@BackpackCmpt
        override val style = GraphicsStyle(service).apply {
            position = PositionEnum.ABSOLUTE
            width = InheritSizeMode { service.parent.client.style.width() }
            height = FixedSizeMode(18 * 4 + 4)
            bottom = 7
            backgroundColor = IntColor.gray
            borderTop.color = IntColor(55, 55, 55)
            borderLeft.color = borderTop.color
            borderBottom.color = IntColor.white
            borderRight.color = IntColor.white
        }

        override fun render(graphics: GuiGraphics) {
            val firstSlot = mainSlots[0][0]
            val offsetX = (style.width() - (18 * 9)) shr 1
            val startX = offsetX + style.x + 1
            val startY = style.y + 1
            if (startX != firstSlot.xPos || startY != firstSlot.yPos) {
                initSlotsPos(startX, startY)
                val message = ByteDataOperator().apply {
                    writeVarInt(startX)
                    writeVarInt(startY)
                }
                send2Service(message)
            }
            graphics.bindTexture(textureLib)
            graphics.drawTexture(offsetX, 0, 8, 53, 162, 76)
            renderChildren(graphics)
        }

    }

    private fun initSlotsPos(startX: Int, startY: Int) {
        var y = startY
        for (slots in mainSlots) {
            for ((i, it) in slots.withIndex()) {
                it.yPos = y
                it.xPos = startX + 18 * i
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