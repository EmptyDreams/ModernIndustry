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
import top.kmar.mi.api.electricity.EleEnergy
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.GuiLoader.MIGuiRegistryEvent
import top.kmar.mi.api.graphics.components.ProgressBarCmpt
import top.kmar.mi.api.graphics.components.SlotMatrixCmpt
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.*
import top.kmar.mi.api.utils.expands.*
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
    private var workProgress = 0
    /** 下一次的输出 */
    private var output: CraftOutput? = null
    /** 获取一个部件加工完成所需的能量 */
    val needEnergy: Int
        get() = output!!.getInt("energy", 20000)

    override fun update() {
        if (world.isClient()) return removeTickable()
        if (output == null) {
            this.output = calculateOutput()
            if (!putOutput(true)) {
                removeTickable()
                return clear(false)
            }
        }
        val need = needEnergy
        val energy = requestEnergy(min(100, need))
        if (energy.isEmpty) return updateBlockState(false)
        if (energy.voltage < minVoltage) return updateBlockState(true)
        if (energy.voltage > maxVoltage) return explode(3.5F, true)
        workProgress += energy.capacity
        if (workProgress >= need) {
            !putOutput(false)
            clear(false)
        } else updateBlockState(true)
        markDirty()
    }

    /** 计算输出，返回`null`表示无输出 */
    fun calculateOutput(): CraftOutput? {
        val existing = getOutputStacks()
        if (existing.all { it.count >= it.maxStackSize }) return null
        val input = calculateProduction()
        val output = CraftGuide.findOutput(CraftList.synthesizer, input)
        if (output == null) clear(true)
        else {
            val surplus = items.insertItems(25 until 29, output.stacks, true)
            if (surplus.isNotEmpty()) return null
        }
        return output
    }

    /**
     * 将输出添加到物品框中
     * @param simulation 是否为模拟
     * @return 是否成功放入
     */
    fun putOutput(simulation: Boolean): Boolean {
        val output = this.output?.stacks ?: return false
        val itor = output.iterator()
        var value: ItemStack = ItemStack.EMPTY
        for (i in 25 until 29) {
            if (value.isEmpty) {
                if (!itor.hasNext()) break
                value = itor.next()
            }
            val insert = items.insertItem(i, value.copy(), simulation)
            value.count = insert.count
        }
        if (itor.hasNext()) return false
        if (!simulation) {
            for (i in 0 until 25) {
                val stack = items.getStackInSlot(i)
                if (!stack.isEmpty) stack.shrink(1)
            }
        }
        return true
    }

    /** 根据原料列表计算产物  */
    fun calculateProduction(): ElementList {
        val input = ElementList(5, 5)
        for (y in 0..4) {
            for (x in 0..4) {
                input[x, y] = items.getStackInSlot(5 * y + x).copy()
            }
        }
        return input
    }

    /** 清楚工作状态和进度 */
    fun clear(updateState: Boolean) {
        workProgress = 0
        output = null
        if (updateState) updateBlockState(false)
    }

    /** 更新方块显示状态 */
    fun updateBlockState(isWorking: Boolean) {
        val old = world.getBlockState(pos)
        if (old.getValue(WORKING) == isWorking) return
        val newState = old.withProperty(WORKING, isWorking)
        world.setBlockWithMark(pos, newState)
    }

    override fun getFront() = EnumFacing.UP

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
        const val maxVoltage = EleEnergy.COMMON + 50
        /** 电器可承受的最小电压 */
        const val minVoltage = EleEnergy.COMMON - 50

        @SubscribeEvent
        @JvmStatic
        fun initGui(event: MIGuiRegistryEvent) {
            event.registryInitTask(synthesizer) { gui: BaseGraphics ->
                val synthesizer =
                    gui.tileEntity as EUElectronSynthesizer
                gui.initItemStackHandler(synthesizer.items)
                val input = gui.getElementByID("input") as SlotMatrixCmpt
                input.slotAttributes.onSlotChanged = {
                    with(synthesizer) {
                        output = calculateOutput()
                        addTickable()
                    }
                }
                val output = gui.getElementByID("output") as SlotMatrixCmpt
                output.slotAttributes.onSlotChanged = { synthesizer.addTickable() }
            }
            event.registryLoopTask(synthesizer) { gui: BaseGraphics ->
                val synthesizer = gui.tileEntity as EUElectronSynthesizer
                val work = gui.getElementByID("work") as ProgressBarCmpt
                if (synthesizer.output == null) {
                    work.value = 0
                    work.max = 0
                } else {
                    work.max = synthesizer.needEnergy
                    work.value = synthesizer.workProgress
                }
            }
        }

    }

}