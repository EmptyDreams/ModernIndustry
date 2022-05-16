package top.kmar.mi.api.graph.utils.managers

import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPanelClient
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.modules.SlotPanelClient
import top.kmar.mi.api.graph.utils.GeneralPanel
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.utils.copy
import top.kmar.mi.api.utils.data.math.Point2D
import top.kmar.mi.api.utils.data.math.Size2D
import top.kmar.mi.api.utils.flipIf
import top.kmar.mi.api.utils.mergeStack
import kotlin.math.min

/**
 * 单行[Slot]组
 * @author EmptyDreams
 */
open class LineSlotPanelManager(
    /** 起始ID（包含） */
    private val start: Int,
    /** Slot的数量 */
    val amount: Int,
    /** 左上角的Slot的X轴坐标 */
    val x: Int,
    /** 左上角的Slot的Y轴坐标 */
    val y: Int,
    /** 每个Slot的大小 */
    val length: Int,
    /** Slot构造器，参数是为Slot分配的ID */
    private val slotCreater: (Point2D, Int) -> Slot
) : GeneralPanel() {

    private val slotList = Array<Slot?>(amount) { null }
    /** 终止ID（不包含） */
    val end = start + amount

    override fun onAdd2Container(father: IPanelContainer) {
        if (slotList[0] != null) throw IllegalArgumentException("[${this::class.simpleName}]不支持重复初始化")
        var index = start
        for (i in slotList.indices) {
            slotList[i] = father.addSlot { slotCreater(Point2D(x + i * length, y), index++) }
        }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        throw UnsupportedOperationException("[${this::class.simpleName}]不支持移除")
    }

    /**
     * 获取指定位置的slot
     * @param index slot在总列表中的下标（[start] <= index < [end]）
     * @return 没有经过保护性拷贝的内部值
     */
    operator fun get(index: Int): Slot = slotList[index + start]!!

    /**
     * 尝试放置指定的物品
     * @param stack 要合并的物品（函数内部不会修改该值）
     * @param flip 是否反向遍历
     * @return 没有成功放入的物品
     */
    fun putStack(stack: ItemStack, flip: Boolean): ItemStack {
        val cpy = stack.copy()
        for (slot in slotList flipIf flip) {
            if (cpy.isEmpty) break
            cpy.count -= slot!!.mergeStack(cpy)
        }
        return cpy
    }

    /**
     * 尝试取出指定位置的物品
     * @param index slot在总列表中的下标（[start] <= index < [end]）
     * @param maxCount 最多取出的数量
     * @return 经过保护性拷贝的结果
     */
    fun fetchStack(index: Int, maxCount: Int = Int.MAX_VALUE): ItemStack {
        val slot = this[index]
        val stack = slot.stack
        val count = min(stack.count, maxCount)
        if (count != 0)
            slot.putStack(stack.copy(stack.count - count))
        return stack.copy(count)
    }

}

/**
 * 单行[Slot]组的客户端实现
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class LineSlotPanelManagerClient(
    start: Int, amount: Int, slotCreater: (Point2D, Int) -> Slot,
    x: Int, y: Int, length: Int
) : LineSlotPanelManager(start, amount, x, y, length, slotCreater), IPanelClient {

    override val height = length
    override val width = length * amount

    override fun paint(painter: GuiPainter) {
        val texture = cacheManager[size].bindTexture()
        painter.drawTexture(0, 0, width, height, texture)
    }

    companion object {

        @JvmStatic
        val cacheManager = TextureCacheManager { size, graphics ->
            val amount = size.width / size.height
            val length = size.height
            val texture = SlotPanelClient.cacheManager[Size2D(length, length)]
            for (x in 0 until amount) {
                texture.drawToGraphics(graphics, x * length, 0, 0, 0, length, length)
            }
        }

    }

}