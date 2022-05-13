package top.kmar.mi.api.graph.utils.managers

import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import top.kmar.mi.api.graph.interfaces.IPanelClient
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.utils.GeneralPanel
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.utils.copy
import top.kmar.mi.api.utils.data.math.Point2D
import top.kmar.mi.api.utils.data.math.Size2D
import top.kmar.mi.api.utils.mergeStack
import kotlin.math.min

/**
 * 矩形[Slot]管理控件
 * @author EmptyDreams
 */
open class RectSlotPanelManager(
    /** ID分配起始位置 */
    val start: Int,
    /** X轴方向上Slot的数量 */
    val xAmount: Int,
    /** Y轴方向上Slot的数量 */
    val yAmount: Int,
    /** 左上角的Slot的X轴坐标 */
    val x: Int,
    /** 左上角Slot的Y轴坐标 */
    val y: Int,
    /** Slot的边长 */
    val length: Int,
    /**
     * Slot构造器
     *
     * 参数列表：
     * 1. [Point2D] - 当前要构造的Slot在组中的坐标
     * 2. [Int] - 为当前Slot分配的ID
     */
    private val slotCreater: (Point2D, Int) -> Slot
) : GeneralPanel() {

    private val slotList = Array<Array<Slot?>>(yAmount) { Array(xAmount) { null } }
    val end = start + xAmount * yAmount

    override fun onAdd2Container(father: IPanelContainer) {
        if (slotList[0][0] != null) throw IllegalArgumentException("[${this::class.simpleName}]不支持重复初始化")
        var index = start
        for (y in slotList.indices) {
            for (x in slotList[y].indices) {
                slotList[y][x] = father.addSlot { slotCreater(Point2D(x, y), index++) }
            }
        }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        throw UnsupportedOperationException("[${this::class.simpleName}]不支持移除")
    }

    operator fun get(x: Int, y: Int): Slot = slotList[y][x]!!

    /** 判断指定下标是否在当前管理器的范围内 */
    operator fun contains(index: Int): Boolean = index in start until end

    /** 通过坐标获取下标 */
    fun getIndex(x: Int, y: Int) = this[x, y].slotIndex

    /** 通过下标获取坐标 */
    fun getLocation(index: Int): Point2D {
        if (index !in this)
            throw IllegalArgumentException("输入的index[$index]不在当前管理器范围[$start, $end)内")
        val innerIndex = index - start
        val x = innerIndex % xAmount
        val y = innerIndex / xAmount
        return Point2D(x, y)
    }

    /**
     * 尝试放置指定的物品
     * @param stack 要合并的物品（函数内部不会修改该值）
     * @return 没有成功放入的物品
     */
    fun putStack(stack: ItemStack): ItemStack {
        val cpy = stack.copy()
        o@ for (list in slotList) {
            for (slot in list) {
                if (cpy.isEmpty) break@o
                cpy.count -= slot!!.mergeStack(cpy)
            }
        }
        return cpy
    }

    /**
     * 尝试取出指定位置的物品
     * @return 经过保护性拷贝的结果
     */
    fun fetchStack(x: Int, y: Int, maxCount: Int = Int.MAX_VALUE): ItemStack {
        if (x !in 0 until xAmount)
            throw IndexOutOfBoundsException("x[$x]超出了指定范围：[0, $xAmount)")
        if (y !in 0 until yAmount)
            throw IndexOutOfBoundsException("y[$y]超出了指定范围：[0, $yAmount)")
        val slot = this[x, y]
        val stack = slot.stack
        val count = min(stack.count, maxCount)
        if (count != 0)
            slot.putStack(stack.copy(stack.count - count))
        return stack.copy(count)
    }

}

/**
 * 多行[Slot]组的客户端实现
 * @author EmptyDreams
 */
class RectSlotPanelManagerClient(
    start: Int, xAmount: Int, yAmount: Int,
    x: Int, y: Int, length: Int,
    slotCreater: (Point2D, Int) -> Slot
) : RectSlotPanelManager(start, xAmount, yAmount, x, y, length, slotCreater), IPanelClient {

    override val height = length * yAmount
    override val width = length * xAmount

    override fun paint(painter: GuiPainter) {
        val texture = cacheManager[size, length].bindTexture()
        painter.drawTexture(0, 0, width, height, texture)
    }

    companion object {

        val cacheManager = ExpandTextureCacheManager<Int> { size, length, graphics ->
            val yAmount = size.height / length
            val image = LineSlotPanelManagerClient.cacheManager[Size2D(size.width, length)]
            for (y in 0 until yAmount) {
                image.drawToGraphics(graphics, 0, y * length, 0, 0, size.width, length)
            }
        }

    }

}