package top.kmar.mi.api.craft.shapes

import net.minecraft.item.ItemStack
import top.kmar.mi.api.craft.elements.ElementList

/**
 * 有序或无序的不可变元素列表
 * @author EmptyDreams
 */
interface IShape {

    /** 判断是否与指定列表相匹配 */
    fun match(input: ElementList): Boolean
    
    /**
     * 获取所有可能的输入
     *
     * @return 该函数返回的数组中 `Array<ItemStack>` 为一格中所有可以放置的物品，
     *  前面的二维数组要求宽度不能大于 [maxWidth] 且每一行的宽度必须相等，
     *  即必须满足下面的表达式：
     *
     *  ```kotlin
     *  val result = getAllInput(Int.MAX_VALUE)
     *  for (i in 1 until result.size) {
     *      if (result[i].size != result[i - 1].size) {
     *          throw AssertError()
     *      }
     *  }
     *  ```
     */
    fun getAllInput(maxWidth: Int): Array<Array<Array<ItemStack>>>
    
}