package top.kmar.mi.api.graphics

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.slots.IGraphicsSlot
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import top.kmar.mi.api.graphics.utils.exps.ComplexCmptExp
import top.kmar.mi.api.utils.expands.copy
import top.kmar.mi.api.utils.expands.isClient
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 服务端GUI窗体对象
 * @author EmptyDreams
 */
open class BaseGraphics(
    /** 打开GUI的玩家 */
    val player: EntityPlayer,
    /** GUI对应的方块坐标 */
    val pos: BlockPos,
    val key: ResourceLocation,
    root: DocumentCmpt?
) : Container() {

    /** 容器对象 */
    val document: DocumentCmpt = ((root?.copy() ?: DocumentCmpt()) as DocumentCmpt)
    /**
     * 客户端对象
     *
     * 保证在第一次尝试获取该对象时客户端GUI已经完成初始化
     */
    @get:SideOnly(Side.CLIENT)
    val client by lazy(NONE) { document.client as BaseGraphicsClient }
    private val graphicsSlots = ArrayList<IGraphicsSlot>(40)

    val tileEntity: TileEntity
        get() = player.world.getTileEntity(pos)!!

    /** 是否可以被指定玩家打开 */
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun detectAndSendChanges() {
        if (graphicsSlots.size != inventorySlots.size)
            throw AssertionError("存在Slot不在API的管理范围内")
        super.detectAndSendChanges()
        if (isClient()) return
        val player = this.player
        fun task(cmpt: Cmpt) {
            cmpt.eachAllChildren {
                it.networkEvent(player)
                task(it)
            }
        }
        task(document)
        GuiLoader.invokeLoopTask(this)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        val slot = graphicsSlots[index]
        val stack = slot.stack
        val oldCout = stack.count
        if (stack.isEmpty || !slot.canTakeStack(playerIn)) return stack
        // 尝试将物品放入Slot中
        val tryPutStack = { init: (IGraphicsSlot) -> Boolean ->
            graphicsSlots.stream()
                .filter(init)
                .sorted { o1, o2 ->
                    if (o1.hasStack == o2.hasStack) o1.compareTo(o2)
                    else if (o1.hasStack) -1
                    else 1
                }
                .forEachOrdered { stack.count = it.putStack(stack) }
        }
        tryPutStack { it.belong != slot.belong }
        if (!stack.isEmpty) tryPutStack { it.belong == slot.belong && it != slot }
        return stack.copy(oldCout - stack.count)
    }

    override fun onContainerClosed(playerIn: EntityPlayer) {
        super.onContainerClosed(playerIn)
        graphicsSlots.stream()
            .filter { it.drop }
            .forEach { playerIn.dropItem(it.stack, false) }
    }

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
    /** @see Cmpt.queryCmptLimit */
    fun queryCmptLimit(exp: ComplexCmptExp, limit: Int) = document.queryCmptLimit(exp, limit)
    /** @see Cmpt.queryCmptAll */
    fun queryCmptAll(exp: String) = document.queryCmptAll(exp)
    /** @see Cmpt.queryCmpt */
    fun queryCmpt(exp: String) = document.queryCmpt(exp)

    /** 添加一个slot，并返回其序列号 */
    fun installSlot(slot: IGraphicsSlot): Int {
        addSlotToContainer(slot.slot)
        graphicsSlots.add(slot)
        return inventorySlots.size - 1
    }

    /** 移除一个slot */
    fun uninstallSlot(slot: IGraphicsSlot) {
        val index = inventorySlots.indexOf(slot.slot)
        inventorySlots.removeAt(index)
        inventoryItemStacks.removeAt(index)
        graphicsSlots.removeAt(index)
        for (i in index until inventorySlots.size) {
            inventorySlots[i].slotNumber = i
        }
    }

    /** 删除指定下标范围内的 */
    fun uninstallSlots(range: IntRange) {
        inventorySlots.removeIf { it.slotNumber in range }
        graphicsSlots.removeIf { it.slot.slotNumber in range }
        for (i in range.reversed()) inventoryItemStacks.removeAt(i)
        for ((index, slot) in inventorySlots.withIndex()) {
            slot.slotNumber = index
        }
    }

    /** 为所有需要设置handler的控件设置handler */
    fun initItemStackHandler(handler: ItemStackHandler) {
        fun task(cmpt: Cmpt) {
            cmpt.initHandler(handler)
            cmpt.eachAllChildren { task(it) }
        }
        task(document)
    }

    fun installParent() {
        document.installParent(Cmpt.EMPTY_CMPT, this)
    }

    class DocumentCmpt(
        attributes: CmptAttributes = CmptAttributes().apply {
            id = "document"
            this["level"] = "-1"
        }
    ) : Cmpt(attributes) {

        @SideOnly(Side.CLIENT)
        override fun initClientObj() = BaseGraphicsClient(gui!!)

        override fun installParent(parent: Cmpt, gui: BaseGraphics) {
            super.installParent(parent, gui)
            val list = LinkedList<Cmpt>()
            list.add(gui.document)
            do {
                val node = list.pop()
                node.eachAllChildren {
                    if (!it.isInstallParent) {
                        it.isInstallParent = true
                        it.installParent(node, gui)
                    }
                    list.add(it)
                }
            } while (list.isNotEmpty())
            isInstallParent = true
        }

        override fun buildNewObj() = DocumentCmpt(attributes.copy())

    }

}