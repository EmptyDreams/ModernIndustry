package top.kmar.mi.api.graphics

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.GraphicsSlot
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import top.kmar.mi.api.utils.copy
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.min

/**
 * 服务端GUI窗体对象
 * @author EmptyDreams
 */
abstract class BaseGraphics : Container() {

    /** 容器对象 */
    val document = DocumentCmpt()
    /**
     * 客户端对象
     *
     * 保证在第一次尝试获取该对象时客户端GUI已经完成初始化
     */
    @get:SideOnly(Side.CLIENT)
    val client by lazy(NONE) { document.client as BaseGraphicsClient }

    /** 是否可以被指定玩家打开 */
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        inventorySlots[0].xPos = 0
    }

    private var _slotCache: List<GraphicsSlot>? = null

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        val cache = _slotCache ?: run {
            val list = ArrayList<GraphicsSlot>(inventorySlots.size)
            inventorySlots.forEach { list.add(it as GraphicsSlot) }
            list.sort()
            _slotCache = list
            list
        }
        val slot = inventorySlots[index] as GraphicsSlot
        val stack = slot.stack
        val oldCout = stack.count
        if (stack.isEmpty || !slot.canTakeStack(playerIn)) return stack
        // 尝试将物品放入Slot中
        val tryPutStack = { init: (GraphicsSlot) -> Boolean ->
            cache.stream()
                .filter(init)
                .filter { it.isEnabled && it.isItemValid(stack) }
                .filter { stack.item == it.stack.item }
                .filter { !stack.hasSubtypes || stack.metadata == it.stack.metadata }
                .filter { ItemStack.areItemStackTagsEqual(stack, it.stack) }
                .forEachOrdered {
                    val itStack = it.stack
                    val maxCout = min(itStack.maxStackSize, it.slotStackLimit)
                    val cout = min(stack.count, maxCout - itStack.count)
                    if (cout == 0) return@forEachOrdered
                    stack.shrink(cout)
                    itStack.grow(cout)
                    it.onSlotChanged()
                }
        }
        tryPutStack { it.belong != slot.belong }
        if (!stack.isEmpty) tryPutStack { it.belong == slot.belong }
        return stack.copy(oldCout - stack.count)
    }

    override fun addSlotToContainer(slotIn: Slot): Slot {
        if (slotIn !is GraphicsSlot) throw IllegalArgumentException(
                "${javaClass.simpleName} 仅支持传入 ${GraphicsSlot::class.java.simpleName} 作为Slot")
        return super.addSlotToContainer(slotIn)
    }

    /**
     * 初始化GUI
     *
     * 调用该方法时，不保证客户端对象已经完成初始化
     *
     * @param player 打开GUI的玩家
     * @param pos 触发GUI的位置
     */
    abstract fun init(player: EntityPlayer, pos: BlockPos)

    /** 添加一个控件 */
    fun addChild(cmpt: Cmpt) = document.addChild(cmpt)
    /** 移除一个控件 */
    fun removeChild(cmpt: Cmpt) = document.removeChild(cmpt)
    /** 注册一个事件 */
    fun addEventListener(
        name: String,
        listener: IGraphicsListener<*>
    ) = document.addEventListener(name, listener)
    /** 移除一个事件 */
    fun removeEventListener(
        name: String,
        listener: IGraphicsListener<*>
    ) = document.removeEventListener(name, listener)
    /** 发布一个事件 */
    fun dispatchEvent(name: String, message: ListenerData) = document.dispatchEvent(name, message)
    /** 通过ID获取元素 */
    fun getElementByID(id: String) = document.getElementByID(id)

    inner class DocumentCmpt : Cmpt("document") {

        @SideOnly(Side.CLIENT)
        override fun initClientObj(): CmptClient =
            BaseGraphicsClient(this@BaseGraphics)

        override fun installSlot(slot: GraphicsSlot): Int {
            _slotCache = null
            addSlotToContainer(slot)
            return inventorySlots.size - 1
        }

        override fun uninstallSlot(slot: GraphicsSlot) {
            _slotCache = null
            val index = inventorySlots.indexOf(slot)
            inventorySlots.removeAt(index)
            inventoryItemStacks.removeAt(index)
        }

    }

}