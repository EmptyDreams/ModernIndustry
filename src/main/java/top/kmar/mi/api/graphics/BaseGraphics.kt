package top.kmar.mi.api.graphics

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ComplexCmptExp
import top.kmar.mi.api.graphics.components.interfaces.slots.IGraphicsSlot
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.copy
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 服务端GUI窗体对象
 * @author EmptyDreams
 */
open class BaseGraphics(root: DocumentCmpt?) : Container() {

    /** 容器对象 */
    @Suppress("LeakingThis")
    val document: DocumentCmpt = ((root?.copy() ?: DocumentCmpt(this)) as DocumentCmpt).apply {
        gui = this@BaseGraphics
    }
    /**
     * 客户端对象
     *
     * 保证在第一次尝试获取该对象时客户端GUI已经完成初始化
     */
    @get:SideOnly(Side.CLIENT)
    val client by lazy(NONE) { document.client as BaseGraphicsClient }
    private val graphicsSlots = ArrayList<IGraphicsSlot>(40)
    /** 打开该GUI的玩家 */
    var player: EntityPlayer =
            FakePlayerFactory.getMinecraft(FMLCommonHandler.instance().minecraftServerInstance.worlds[0])
        set(value) {
            if (field is FakePlayer) field = value
        }
        get() = if (field !is FakePlayer) field else throw AssertionError("player未初始化")

    /** 是否可以被指定玩家打开 */
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun detectAndSendChanges() {
        if (graphicsSlots.size != inventorySlots.size)
            throw AssertionError("存在Slot不在API的管理范围内")
        super.detectAndSendChanges()
        if (WorldUtil.isClient()) return
        val player = this.player
        fun task(cmpt: Cmpt) {
            cmpt.eachAllChildren {
                it.networkEvent(player)
                task(it)
            }
        }
        task(document)
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

    /**
     * 初始化GUI
     *
     * 调用该方法时，不保证客户端对象已经完成初始化
     *
     * @param player 打开GUI的玩家
     * @param pos 触发GUI的位置
     */
    open fun init(player: EntityPlayer, pos: BlockPos) {
        this.player = player
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
    fun queryCmptAll(exp: ComplexCmptExp) = document.queryCmptAll(exp)
    fun queryCmpt(exp: ComplexCmptExp) = document.queryCmpt(exp)

    class DocumentCmpt(
        var gui: BaseGraphics?,
        attributes: CmptAttributes
    ) : Cmpt(attributes) {

        constructor(gui: BaseGraphics?) :
                this(gui, CmptAttributes().apply {
                    id = "document"
                    this["level"] = "-1"
                })

        @SideOnly(Side.CLIENT)
        override fun initClientObj() = BaseGraphicsClient(gui!!)

        override fun installParent(parent: Cmpt) {
            val list = LinkedList<Cmpt>()
            list.add(gui!!.document)
            do {
                val node = list.pop()
                node.eachAllChildren {
                    if (!it.isInstallParent) {
                        it.isInstallParent = true
                        it.installParent(node)
                    }
                    list.add(it)
                }
            } while (list.isNotEmpty())
        }

        override fun installSlot(slot: IGraphicsSlot): Int {
            gui!!.addSlotToContainer(slot.slot)
            gui!!.graphicsSlots.add(slot)
            return gui!!.inventorySlots.size - 1
        }

        override fun uninstallSlot(slot: IGraphicsSlot) {
            with(gui!!) {
                val index = inventorySlots.indexOf(slot.slot)
                inventorySlots.removeAt(index)
                inventoryItemStacks.removeAt(index)
                graphicsSlots.removeAt(index)
            }
        }

        override fun buildNewObj() = DocumentCmpt(gui, attributes.copy())

    }

}