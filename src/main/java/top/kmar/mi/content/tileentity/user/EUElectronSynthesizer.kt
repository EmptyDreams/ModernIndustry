package top.kmar.mi.content.tileentity.user

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.craft.CraftGuide
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.electricity.clock.OrdinaryCounter
import top.kmar.mi.api.electricity.info.BiggerVoltage
import top.kmar.mi.api.electricity.info.EleEnergy
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.GuiLoader.MIGuiRegistryEvent
import top.kmar.mi.api.graphics.components.ProgressBarCmpt
import top.kmar.mi.api.graphics.components.SlotMatrixCmpt
import top.kmar.mi.api.register.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.isClient
import top.kmar.mi.api.utils.match
import top.kmar.mi.api.utils.removeTickable
import top.kmar.mi.content.blocks.BlockGuiList.synthesizer
import top.kmar.mi.data.CraftList
import top.kmar.mi.data.properties.MIProperty.Companion.WORKING
import kotlin.math.min

/**
 * 电子工作台
 * @author EmptyDreams
 */
@AutoTileEntity("electron_synthesizer")
@EventBusSubscriber
class EUElectronSynthesizer : FrontTileEntity(), ITickable {

    /** 物品列表 */
    @field:AutoSave
    private val items = ItemStackHandler(5 * 5 + 4)
    /** 工作时间 */
    @field:AutoSave
    private var workingTime = 0
    /** 下一次的输出 */
    private var output: CraftOutput? = null
    /** 获取工作需要的时间 */
    val maxTime: Int
        get() = output!!.getInt("time")

    init {
        val counter = OrdinaryCounter(100)
        counter.bigger = BiggerVoltage(2F, EnumBiggerVoltage.BOOM)
        this.counter = counter
        maxEnergy = 20
    }

    override fun update() {
        if (world.isClient()) return removeTickable()
        if (output == null) {
            val input = calculateProduction()
            val output = CraftGuide.findOutput(CraftList.synthesizer, input) ?: return clear(true)
            this.output = output
            if (!putOutput(true)) return clear(true)
        }
        if (!shrinkEnergy(1)) return
        if (++workingTime >= maxTime) {
            if (!putOutput(false)) throw AssertionError()
            clear(false)
        } else updateBlockState()
    }

    /**
     * 将输出添加到物品框中
     * @param simulation 是否为模拟
     * @return 是否成功放入
     */
    fun putOutput(simulation: Boolean): Boolean {
        val output = this.output?.stacks ?: return false
        val slots = getOutputStacks()
        val itor = output.iterator()
        var value: ItemStack = ItemStack.EMPTY
        for (stack in slots) {
            if (value.isEmpty) {
                if (!itor.hasNext()) break
                value = itor.next()
            }
            if (stack.count < stack.maxStackSize && stack.match(value)) {
                val count = min(value.count, stack.maxStackSize - stack.count)
                value.shrink(count)
                if (!simulation) stack.grow(count)
            }
        }
        if (itor.hasNext()) return false
        for (i in 0 until 25) {
            val stack = items.getStackInSlot(i)
            if (!stack.isEmpty) stack.shrink(1)
        }
        return true
    }

    /** 根据原料列表计算产物  */
    fun calculateProduction(): ElementList {
        val input = ElementList(5, 5)
        for (y in 0..4) {
            for (x in 0..4) {
                input[x, y] = items.getStackInSlot(5 * y + x)
            }
        }
        return input
    }

    /** 清楚工作状态和进度 */
    fun clear(updateState: Boolean) {
        output = null
        workingTime = 0
        if (updateState) updateBlockState()
    }

    /** 更新方块显示状态 */
    fun updateBlockState() {
        val newState = world.getBlockState(getPos()).withProperty(WORKING, workingTime > 0)
        WorldUtil.setBlockState(world, pos, newState)
    }

    override fun isReceiveAllowable(facing: EnumFacing?) = true

    override fun isExtractAllowable(facing: EnumFacing?) = false

    override fun onReceive(energy: EleEnergy): Boolean {
        if (energy.voltage > maxVoltage) counter.plus()
        return true
    }

    override fun getFront() = EnumFacing.UP

    override fun getExVoltage(): Int = maxVoltage

    /** 获取所有输出框的stack */
    fun getOutputStacks() = Array(4) { items.getStackInSlot(25 + it) }

    /** 获取所有物品框的stack */
    fun getAllStacks(): List<ItemStack> {
        val result = ArrayList<ItemStack>(items.slots)
        for (i in 0 until items.slots) {
            result.add(items.getStackInSlot(i))
        }
        return result
    }

    companion object {

        /** 电器可以承受的最大电压 */
        const val maxVoltage = EleEnergy.COMMON

        @SubscribeEvent
        @JvmStatic
        fun initGui(event: MIGuiRegistryEvent) {
            event.registryInitTask(synthesizer) { gui: BaseGraphics ->
                val synthesizer =
                    gui.tileEntity as EUElectronSynthesizer
                gui.initItemStackHandler(synthesizer.items)
                val input = gui.getElementByID("input") as SlotMatrixCmpt
                input.slotAttributes.onSlotChanged = { synthesizer.output = null }
            }
            event.registryLoopTask(synthesizer) { gui: BaseGraphics ->
                val synthesizer =
                    gui.tileEntity as EUElectronSynthesizer
                val work = gui.getElementByID("work") as ProgressBarCmpt?
                work!!.max = synthesizer.maxTime
                work.value = synthesizer.workingTime
            }
        }

    }

}