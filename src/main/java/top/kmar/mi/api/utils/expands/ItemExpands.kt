/** 与物品有管的操作的封装 */
package top.kmar.mi.api.utils.expands

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler

/**
 * 将指定物品放入[ItemStackHandler]的指定区域内
 * @param range 下标范围
 * @param stacks 要放入的[ItemStack]，传入的对象可能被修改
 * @param simulate 是否为模拟，为`true`时不需改内部数据
 * @return 剩余的物品列表，该列表中的对象可能会与传入的[ItemStack]为同一对象
 */
fun ItemStackHandler.insertItems(
    range: IntRange, simulate: Boolean, vararg stacks: ItemStack
): MutableList<ItemStack> {
    return insertItems(range, stacks.iterator(), stacks.size, simulate)
}

/**
 * 将指定物品放入[ItemStackHandler]的指定区域内
 * @param range 下标范围
 * @param stacks 要放入的[ItemStack]，传入的对象可能被修改
 * @param simulate 是否为模拟，为`true`时不需改内部数据
 * @return 剩余的物品列表，该列表中的对象可能会与传入的[ItemStack]为同一对象
 */
fun ItemStackHandler.insertItems(
    range: IntRange, stacks: Collection<ItemStack>, simulate: Boolean
): MutableList<ItemStack> {
    return insertItems(range, stacks.iterator(), stacks.size, simulate)
}

private fun ItemStackHandler.insertItems(
    range: IntRange, itor: Iterator<ItemStack>, size: Int, simulate: Boolean
): MutableList<ItemStack> {
    if (size == 0) return mutableListOf()
    var index = 0
    var stack = itor.next()
    for (i in range) {
        while (stack.isEmpty) {
            if (++index == size) return mutableListOf()
            stack = itor.next()
        }
        stack = insertItem(i, stack, simulate)
    }
    if (stack.isEmpty && !itor.hasNext()) return mutableListOf()
    val result = ArrayList<ItemStack>(size - index)
    if (!stack.isEmpty) result += stack
    itor.asSequence()
        .filter { !it.isEmpty }
        .forEach { result += it }
    return result
}
/** 深度复制列表 */
fun List<ItemStack>.deepClone(): MutableList<ItemStack> {
    val list = java.util.ArrayList<ItemStack>(size)
    forEach { list.add(it.copy()) }
    return list
}
/** 判断两个stack能否合并 */
fun ItemStack.match(stack: ItemStack) =
    isEmpty || stack.isEmpty || (
                    (!hasSubtypes || metadata == stack.metadata) &&
                    isItemEqual(stack) &&
                            ItemStack.areItemStackTagsEqual(this, stack)
            )
/** 拷贝Stack并将拷贝后的Stack的count修改为指定值 */
fun ItemStack.copy(count: Int): ItemStack {
    val result = copy()
    result.count = count
    return result
}
/**
 * 创建一个新的ItemStack
 * @receiver [Item]
 */
fun Item.newStack(amount: Int = 1) = ItemStack(this, amount)