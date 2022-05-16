package top.kmar.mi.api.utils.iterators

/**
 * 倒序遍历数组的迭代器
 * @author EmptyDreams
 */
class ArrayFlipIterator<T>(
    private val array: Array<T>,
    private var index: Int
) : Iterator<T> {

    override fun hasNext() = index != 0

    override fun next() = array[index--]

}