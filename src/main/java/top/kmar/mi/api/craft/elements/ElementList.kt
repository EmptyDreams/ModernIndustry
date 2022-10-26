package top.kmar.mi.api.craft.elements

import net.minecraft.item.ItemStack

/**
 * 可变物品列表
 * @author EmptyDreams
 */
class ElementList<T : Any>(
    val width: Int,
    val height: Int,
    val def: T,
    val type: Class<T>
) : Iterable<T> {

    private val values = Array(height) { Array<Any>(width) { def } }

    val size = width * height

    /** 通过下标获取值，仅在`height == 1`时可用 */
    operator fun get(index: Int): T {
        assert(height == 1)
        @Suppress("UNCHECKED_CAST")
        return values[0][index] as T
    }

    /** 通过坐标获取值 */
    operator fun get(x: Int, y: Int): T {
        @Suppress("UNCHECKED_CAST")
        return values[y][x] as T
    }

    /** 通过下标设置值，仅在`height == 1`时可用 */
    operator fun set(index: Int, value: T) {
        assert(height == 1)
        values[0][index] = value
    }

    /** 通过坐标设置值 */
    operator fun set(x: Int, y: Int, value: T) {
        values[y][x] = value
    }

    override fun iterator(): Iterator<T> = ArrayIterator()

    private inner class ArrayIterator : Iterator<T> {

        private var y = 0
        private var x = 0

        override fun hasNext() = x != width || y != height

        override fun next(): T {
            if (x == width) {
                x = 0
                ++y
            }
            return get(x, y)
        }

    }

    companion object {

        @JvmStatic
        val empty: ElementList<*> = buildInt(0)

        @JvmStatic
        inline fun <reified T : Any> build(width: Int, height: Int, def: T): ElementList<T> =
            ElementList(width, height, def, T::class.java)

        @JvmStatic
        fun buildStackList(vararg stacks: ItemStack): ElementList<ItemStack> {
            val result = build(stacks.size, 1, ItemStack.EMPTY)
            for ((index, it) in stacks.withIndex()) {
                result[index] = it
            }
            return result
        }

        @JvmStatic
        fun buildStackList(length: Int): ElementList<ItemStack> =
            build(length, 1, ItemStack.EMPTY)

        @JvmStatic
        fun buildStackRect(width: Int, height: Int): ElementList<ItemStack> =
            build(width, height, ItemStack.EMPTY)

        @JvmStatic
        fun buildInt(value: Int): ElementList<Int> = build(1, 1, value)

    }

}