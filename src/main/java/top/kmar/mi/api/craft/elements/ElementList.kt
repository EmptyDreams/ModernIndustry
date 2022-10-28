package top.kmar.mi.api.craft.elements

import net.minecraft.item.ItemStack
import top.kmar.mi.api.utils.match
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 可变物品列表
 * @author EmptyDreams
 */
class ElementList(
    val width: Int,
    val height: Int
) : Iterable<ItemStack> {

    private val values = Array(height) { Array(width) { ItemStack.EMPTY } }

    val size = width * height
    /** 最小化的列表 */
    val minimum by lazy(NONE) { clipSpace() }
    /** 无序化的列表 */
    val disorderly by lazy(NONE) { merge() }

    /** 通过下标获取值，仅在`height == 1`时可用 */
    operator fun get(index: Int): ItemStack {
        assert(height == 1)
        return values[0][index]
    }

    /** 通过坐标获取值 */
    operator fun get(x: Int, y: Int): ItemStack {
        return values[y][x]
    }

    /** 通过下标设置值，仅在`height == 1`时可用 */
    operator fun set(index: Int, value: ItemStack) {
        assert(height == 1)
        values[0][index] = value
    }

    /** 通过坐标设置值 */
    operator fun set(x: Int, y: Int, value: ItemStack) {
        values[y][x] = value
    }

    /** 裁剪掉四周的空白区域，并返回一个新的列表（不修改当前对象但两个列表共享[ItemStack]对象） */
    fun clipSpace(): ElementList {
        var left = 0
        var top = 0
        var right = width - 1
        var bottom = height - 1
        // 查找第一个包含非空元素的行
        o@for (y in 0 .. bottom) {
            for (x in 0 .. right) {
                if (!this[x, y].isEmpty) {
                    top = y
                    break@o
                }
            }
            return empty
        }
        // 查找最后一个包含非空元素的行
        o@for (y in bottom downTo top) {
            for (x in 0 .. right) {
                if (!this[x, y].isEmpty) {
                    bottom = y
                    break@o
                }
            }
        }
        // 查找第一个包含非空元素的列
        o@for (x in 0 .. right) {
            for (y in top .. bottom) {
                if (!this[x, y].isEmpty) {
                    left = x
                    break@o
                }
            }
        }
        // 查找最后一个包含非空元素的列
        o@for (x in right downTo left) {
            for (y in top .. bottom) {
                if (!this[x, y].isEmpty) {
                    right = x
                    break@o
                }
            }
        }
        val result = ElementList(right - left + 1, bottom - top + 1)
        for (y in top .. bottom) {
            for (x in left .. right)
                result[x - left, y - top] = this[x, y]
        }
        return result
    }

    /** 合并列表中所有可合并的物品，并返回一个新的列表（不修改当前对象但两个列表共享[ItemStack]对象） */
    fun merge(): MutableList<ItemStack> {
        val result = LinkedList<ItemStack>()
        asSequence()
            .filter { !it.isEmpty }
            .forEach {
                for (output in result) {
                    if (output.match(it)) {
                        output.grow(it.count)
                        return@forEach
                    }
                }
                result.add(it)
            }
        return result
    }

    override fun iterator(): Iterator<ItemStack> = ArrayIterator()

    private inner class ArrayIterator : Iterator<ItemStack> {

        private var y = 0
        private var x = 0

        override fun hasNext() = x != width || y != height

        override fun next(): ItemStack {
            if (x == width) {
                x = 0
                ++y
            }
            return get(x, y)
        }

    }

    companion object {

        @JvmStatic
        val empty: ElementList = ElementList(0, 0)

        @JvmStatic
        fun build(vararg stacks: ItemStack): ElementList {
            val result = ElementList(stacks.size, 1)
            for ((index, it) in stacks.withIndex()) {
                result[index] = it
            }
            return result
        }

        @JvmStatic
        fun build(length: Int): ElementList = ElementList(length, 1)

    }

}