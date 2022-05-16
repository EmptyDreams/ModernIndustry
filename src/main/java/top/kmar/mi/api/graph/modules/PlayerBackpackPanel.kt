package top.kmar.mi.api.graph.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPanelClient
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.utils.GeneralPanel
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.graph.utils.managers.LineSlotPanelManager
import top.kmar.mi.api.graph.utils.managers.LineSlotPanelManagerClient
import top.kmar.mi.api.graph.utils.managers.RectSlotPanelManager
import top.kmar.mi.api.graph.utils.managers.RectSlotPanelManagerClient

/**
 * 玩家背包控件
 * @author EmptyDreams
 */
open class PlayerBackpackPanel(
    val x: Int, val y: Int,
    /** 绑定的玩家对象 */
    val player: EntityPlayer,
    /** slot起始分配ID */
    val slotStartIndex: Int
) : GeneralPanel() {

    val length = 18

    /** 背包栏起始ID */
    protected open val backpackSlots = RectSlotPanelManager(
        slotStartIndex + 9, 9, 3, x, y, length) { pos, id ->
        SlotHandle(player.inventory, id, pos.x, pos.y, id - 9 - slotStartIndex)
    }
    /** 快捷栏起始ID */
    protected open val lnkSlots = LineSlotPanelManager(
        slotStartIndex, 9, x, 18 * 3 + 5 + y, length) { pos, id ->
        SlotHandle(player.inventory, id, pos.x, pos.y, id - slotStartIndex)
    }

    override fun onAdd2Container(father: IPanelContainer) {
        lnkSlots.onAdd2Container(father)
        backpackSlots.onAdd2Container(father)
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        throw UnsupportedOperationException("[${this::class.simpleName}]不支持移除")
    }

    /**
     * 放入物品到玩家背包或快捷操作栏
     * @param stack 要放入的物品（内部不会修改）
     * @param flip 是否反向遍历，为`true`时先从快捷操作栏末端开始，否则从背包起始端开始
     * @return 未成功放入的物品
     */
    fun putStack(stack: ItemStack, flip: Boolean): ItemStack {
        return if (flip) {
            var surplus = putStack2Lnk(stack, true)
            if (!surplus.isEmpty) surplus = putStack2Backpack(surplus, false)
            surplus
        } else {
            var surplus = putStack2Backpack(stack, false)
            if (!surplus.isEmpty) surplus = putStack2Lnk(surplus, true)
            surplus
        }
    }

    /**
     * 从玩家背包或快捷操作栏中取出物品
     * @param index slot编号下标（相对于总体）
     * @param maxCount 最多取出的量
     * @throws IndexOutOfBoundsException 如果输入的下标不在管理器管理范围内
     */
    fun fetchStack(index: Int, maxCount: Int = Int.MAX_VALUE): ItemStack {
        if (index in backpackSlots) return fetchStackFromBackpack(index, maxCount)
        if (index in lnkSlots) return fetchStackFromLnk(index, maxCount)
        throw IndexOutOfBoundsException("输入的下标[$index]不在管理器的管理范围内")
    }

    /**
     * 向玩家背包放置物品
     * @see RectSlotPanelManager.putStack
     */
    fun putStack2Backpack(stack: ItemStack, flip: Boolean) = backpackSlots.putStack(stack, flip)

    /**
     * 向玩家快捷操作栏放置物品
     * @see LineSlotPanelManager.putStack
     */
    fun putStack2Lnk(stack: ItemStack, flip: Boolean) = lnkSlots.putStack(stack, flip)

    /**
     * 通过坐标从玩家背包取出物品
     * @see RectSlotPanelManager.fetchStack
     */
    fun fetchStackFromBackpack(x: Int, y: Int, maxCount: Int = Int.MAX_VALUE) =
        backpackSlots.fetchStack(x, y, maxCount)

    /**
     * 通过下标（相对于总体）从玩家背包取出物品
     * @see RectSlotPanelManager.getLocation
     * @see RectSlotPanelManager.fetchStack
     */
    fun fetchStackFromBackpack(index: Int, maxCount: Int = Int.MAX_VALUE): ItemStack {
        val pos = backpackSlots.getLocation(index)
        return backpackSlots.fetchStack(pos.x, pos.y, maxCount)
    }

    /**
     * 通过下标（相对于总体）从玩家快捷操作栏取出物品
     * @see LineSlotPanelManager.fetchStack
     */
    fun fetchStackFromLnk(index: Int, maxCount: Int = Int.MAX_VALUE) =
        lnkSlots.fetchStack(index, maxCount)

    protected class SlotHandle(
        inventoryIn: IInventory, id: Int, xPos: Int, yPos: Int, val index: Int
    ) : Slot(inventoryIn, id, xPos, yPos) {

        override fun getStack(): ItemStack = inventory.getStackInSlot(index)

        override fun putStack(stack: ItemStack) {
            inventory.setInventorySlotContents(index, stack)
            onSlotChanged()
        }

        override fun decrStackSize(amount: Int): ItemStack = inventory.decrStackSize(index, amount)

    }

}

/**
 * 玩家背包控件的客户端实现
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class PlayerBackpackPanelClient(
    x: Int, y: Int,
    player: EntityPlayer,
    slotStartIndex: Int
) : PlayerBackpackPanel(x, y, player, slotStartIndex), IPanelClient {

    override val width = length * 9
    override val height = length * 4 + 5

    /** 背包栏起始ID */
    override val backpackSlots = RectSlotPanelManagerClient(
        slotStartIndex + 9, 9, 3, 0, 0, length) { pos, id ->
        SlotHandle(player.inventory, id, pos.x, pos.y, id - 9 - slotStartIndex)
    }
    /** 快捷栏起始ID */
    override val lnkSlots = LineSlotPanelManagerClient(
        slotStartIndex, 9, 0, 18 * 3 + 5 + y, length) { pos, id ->
        SlotHandle(player.inventory, id, pos.x, pos.y, id - slotStartIndex)
    }

    override fun paint(painter: GuiPainter) {
        painter.paintPanel(backpackSlots)
        painter.paintPanel(lnkSlots)
    }

}