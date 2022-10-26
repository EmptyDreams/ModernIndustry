package top.kmar.mi.api.craft.elements

import com.google.common.base.Predicate
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreIngredient

/**
 * 基于[ItemStack]的合成表元素
 * @author EmptyDreams
 */
class ItemCraftElement : ICraftElement {

    private val ingredient: Predicate<ItemStack>
    private val count: Int

    /** 通过[ItemStack]列表构建一个元素，列表中的任意一个元素与输入相匹配即代表匹配 */
    constructor(count: Int, vararg stacks: ItemStack) {
        this.count = count
        ingredient = Ingredient.fromStacks(*stacks)
    }

    /** 通过[ItemStack]列表构建一个元素，列表中的任意一个元素与输入相匹配即代表匹配 */
    constructor(vararg stacks: ItemStack) : this(1, *stacks)

    /** 通过矿物词典构建一个元素，输入的物品含有指定矿物词典即代表匹配 */
    constructor(count: Int, oreName: String) {
        this.count = count
        ingredient = OreIngredient(oreName)
    }

    /** 通过矿物词典构建一个元素，输入的物品含有指定矿物词典即代表匹配 */
    constructor(oreName: String) : this(1, oreName)

    override fun match(input: Any): Boolean {
        if (input !is ItemStack) return false
        return input.count >= count && ingredient.apply(input)
    }

    override fun <T : Any> reduce(input: T): T {
        input as ItemStack
        input.shrink(count)
        return input
    }

}