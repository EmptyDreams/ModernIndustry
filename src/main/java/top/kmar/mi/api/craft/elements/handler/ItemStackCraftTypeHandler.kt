package top.kmar.mi.api.craft.elements.handler

import net.minecraft.item.ItemStack
import top.kmar.mi.api.utils.match

/**
 * [ItemStack]的handler
 * @author EmptyDreams
 */
object ItemStackCraftTypeHandler : ICraftTypeHandler<ItemStack> {

    override val type = ItemStack::class.java

    override fun defValue(): ItemStack = ItemStack.EMPTY

    override fun grow(original: ItemStack, value: ItemStack): ItemStack {
        original.grow(value.count)
        return original
    }

    override fun shrink(original: ItemStack, value: ItemStack): ItemStack {
        original.shrink(value.count)
        return original
    }

    override fun isEmpty(value: ItemStack) = value.isEmpty

    override fun canMerge(left: ItemStack, right: ItemStack) = left.match(right)

}