package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.slots.BackpackSlot
import top.kmar.mi.api.graphics.utils.*
import top.kmar.mi.api.regedits.others.AutoCmpt
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 玩家背包
 * @author EmptyDreams
 */
@AutoCmpt("backpack")
class BackpackCmpt(attribute: CmptAttributes) : Cmpt(attribute) {

    override fun initClientObj() = BackpackCmptClient()
    override fun buildNewObj() = BackpackCmpt(attributes.copy())

    var player: EntityPlayer? = null
    var priority: Int by attribute.toIntDelegate()
    /** 活动的九个物品栏 */
    val activeSlots by lazy(NONE) {
        Array(9) {
            BackpackSlot(this, priority, player!!, it)
        }
    }
    /** 背包栏 */
    val mainSlots by lazy(NONE) {
        Array(3) { y ->
            Array(9) { x ->
                BackpackSlot(this, priority.shl(2), player!!, 9 + x + y * 9)
            }
        }
    }

    override fun installParent(parent: Cmpt, gui: BaseGraphics) {
        super.installParent(parent, gui)
        player = gui.player
        activeSlots.forEach { gui.installSlot(it) }
        for (slots in mainSlots) {
            slots.forEach { gui.installSlot(it) }
        }
    }

    override fun uninstallParent(oldParent: Cmpt, gui: BaseGraphics) {
        super.uninstallParent(oldParent, gui)
        val start = activeSlots.first().slot.slotNumber
        gui.uninstallSlots(start until start + 36)
    }

    override fun receive(message: NBTBase) {
        val nbt = message as NBTTagCompound
        val x = nbt.getInteger("x")
        val y = nbt.getInteger("y")
        initSlotsPos(x, y)
    }

    companion object {

        @SideOnly(Side.CLIENT)
        @JvmStatic
        val textureLib = ResourceLocation("textures/gui/container/creative_inventory/tab_inventory.png")

    }

    @SideOnly(Side.CLIENT)
    inner class BackpackCmptClient : ICmptClient {

        override val service = this@BackpackCmpt
        override val style = GraphicsStyle(service).apply {
            position = PositionEnum.ABSOLUTE
            widthCalculator = InheritSizeMode { it.width }
            heightCalculator = FixedSizeMode(18 * 4 + 4 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1)
            bottom = 8
            backgroundColor = IntColor.gray
            borderTop.color = IntColor(55, 55, 55)
            borderLeft.color = borderTop.color
            borderBottom.color = IntColor.white
            borderRight.color = IntColor.white
        }

        override fun render(graphics: GuiGraphics) {
            val firstSlot = mainSlots[0][0]
            val offsetX = (style.width - (18 * 9)) shr 1
            val fontHeight = graphics.fontRenderer.FONT_HEIGHT + 1
            val startX = offsetX + style.x + 1
            val startY = style.y + 1 + fontHeight
            // 网络通信
            if (startX != firstSlot.xPos || startY != firstSlot.yPos) {
                initSlotsPos(startX, startY)
                val message = NBTTagCompound().apply {
                    setInteger("x", startX)
                    setInteger("y", startY)
                }
                send2Service(message)
            }
            with(graphics) {
                drawString(offsetX, 0, I18n.format("key.categories.inventory"))
                bindTexture(textureLib)
                drawTexture256(offsetX, fontHeight, 8, 53, 162, 76)
            }
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
            it.xPos = startX + 18 * i
        }
    }

}