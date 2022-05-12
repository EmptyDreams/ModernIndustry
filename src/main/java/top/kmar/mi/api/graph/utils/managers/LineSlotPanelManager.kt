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
import top.kmar.mi.api.utils.checkMerge
import top.kmar.mi.api.utils.copy
import top.kmar.mi.api.utils.data.math.Size2D
import kotlin.math.min

/**
 * 单行[Slot]组
 * @author EmptyDreams
 */
open class LineSlotPanelManager(
    /** 起始ID（包含） */
    private val start: Int,
    /** [Slot]的数量 */
    val amount: Int,
    /**
     * [Slot]构造器
     *
     * 其中第二个参数是为[Slot]分配的ID
     */
    private val slotCreater: (LineSlotPanelManager, Int) -> Slot
) : GeneralPanel() {

    private val slotList = Array<Slot?>(amount) { null }
    /** 终止ID（不包含） */
    val end = start + amount

    override fun onAdd2Container(father: IPanelContainer) {
        if (slotList[0] != null) throw IllegalArgumentException("[SlotPanelManager]不支持重复初始化")
        var index = start
        slotList.forEachIndexed { i, _ ->
            slotList[i] = father.addSlot { slotCreater(this, index++) }
        }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        throw UnsupportedOperationException("[SlotPanel]不支持移除")
    }

    /**
     * 尝试放置指定的物品
     * @param stack 要合并的物品（函数内部不会修改该值）
     * @return 没有成功放入的物品
     */
    fun putStack(stack: ItemStack): ItemStack {
        var count = stack.count
        for (slot in slotList) {
            if (count == 0) break
            val slotStack = slot!!.stack
            if (!slotStack.checkMerge(stack)) continue
            val maxCount = min(slotStack.maxStackSize, slot.slotStackLimit) - slotStack.count
            val extract = min(maxCount, count)
            if (extract == 0) continue
            count -= extract
            slot.putStack(stack.copy(extract))
        }
        return stack.copy(count)
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

    /**
     * 获取指定位置的slot
     * @param index slot在总列表中的下标（[start] <= index < [end]）
     * @return 没有经过保护性拷贝的内部值
     */
    operator fun get(index: Int): Slot = slotList[index + start]!!

}

/**
 * 单行[Slot]组的客户端实现
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class LineSlotPanelManagerClient(
    start: Int, amount: Int, slotCreater: (LineSlotPanelManager, Int) -> Slot,
    override val x: Int,
    override val y: Int,
    length: Int
) : LineSlotPanelManager(start, amount, slotCreater), IPanelClient {

    override val height = length
    override val width = length * amount

    override fun paint(painter: GuiPainter) {
        val texture = createTexture(size).bindTexture()
        painter.drawTexture(0, 0, width, height, texture)
    }

    companion object {

        private val cacheManager = TextureCacheManager { size, graphics ->
            val amount = size.width / size.height
            val subSize = Size2D(size.height, size.height)
            for (i in 0 until amount) {
                val x = i * size.height
                val painter = graphics.create(x, 0, size.height, size.height)
                SlotPanelClient.drawTexture(subSize, painter)
                painter.dispose()
            }
        }

        fun createTexture(size: Size2D) = cacheManager[size]

    }

}