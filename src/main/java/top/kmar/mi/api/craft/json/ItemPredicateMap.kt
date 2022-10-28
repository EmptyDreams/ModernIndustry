package top.kmar.mi.api.craft.json

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.common.Loader
import java.util.*
import java.util.function.Predicate
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 存储 key map
 * @author EmptyDreams
 */
class ItemPredicateMap {

    private val map = Char2ObjectOpenHashMap<LazyNode>()

    /** 获取指定`key`对应的`predicate` */
    fun getPredicate(key: Char): Predicate<ItemStack> {
        if (key == ' ') return Ingredient.EMPTY
        return map.get(key)?.predicate ?: throw IllegalArgumentException("指定的值[$key]没有找到对应的key")
    }

    /** 获取指定`key`对应的`stack` */
    fun getStack(key: Char): ItemStack {
        if (key == ' ') ItemStack.EMPTY
        return map.get(key)?.stack ?: throw IllegalArgumentException("指定的值[$key]没有找到对应的key")
    }

    operator fun set(key: Char, json: JsonObject) {
        map.put(key, LazyNode(json))
    }

    private class LazyNode(json: JsonObject) {

        val predicate by lazy(NONE) { parserPredicate(json) }
        val stack by lazy(NONE) { parserStack(json) }

    }

    companion object {

        private val contentMap = WeakHashMap<Any, JsonContext>()

        private fun getContent(): JsonContext {
            val mod = Loader.instance().activeModContainer()!!
            return contentMap.computeIfAbsent(mod) { JsonContext(mod.modId) }
        }

        /** 将一个JSON对象解析为[Predicate]对象 */
        fun parserPredicate(json: JsonObject): Predicate<ItemStack> =
            CraftingHelper.getIngredient(json, getContent())

        /** 将一个JSON对象解析为[ItemStack]对象 */
        fun parserStack(json: JsonObject): ItemStack =
            CraftingHelper.getItemStack(json, getContent())

    }

}