package top.kmar.mi.api.graph.interfaces

import net.minecraft.item.ItemStack

/**
 * （可能）含有Slot的控件
 * @author EmptyDreams
 */
interface ISlotPanel {

    /** 判断指定下标是否在当前管理器的范围内 */
    operator fun contains(index: Int): Boolean

    /**
     * 尝试放置指定的物品
     * @param stack 要合并的物品（函数内部不会修改该值）
     * @param flip 是否反向遍历
     * @return 没有成功放入的物品
     */
    fun putStack(stack: ItemStack, flip: Boolean): ItemStack

    /**
     * 通过下标（相对于总体）从玩家背包取出物品
     * @param index 要操作的Slot的下标
     * @param maxCount 要取出的物品的最大数量
     * @return 成功取出的物品
     */
    fun fetchStack(index: Int, maxCount: Int = Int.MAX_VALUE): ItemStack

}