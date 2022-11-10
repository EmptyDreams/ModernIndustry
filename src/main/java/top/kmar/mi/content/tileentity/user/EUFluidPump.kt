package top.kmar.mi.content.tileentity.user

import net.minecraft.client.resources.I18n
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.capabilities.fluid.FluidCapability.TRANSFER
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.electricity.EleEnergy
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.fluid.data.FluidQueue
import top.kmar.mi.api.fluid.data.TransportReport
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.ButtonCmpt
import top.kmar.mi.api.graphics.components.ProgressBarCmpt
import top.kmar.mi.api.graphics.components.TextCmpt
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.net.messages.block.cap.BlockNetworkCapability
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.*
import top.kmar.mi.api.utils.expands.isClient
import top.kmar.mi.api.utils.expands.removeTickable
import top.kmar.mi.content.blocks.BlockGuiList
import top.kmar.mi.content.blocks.machine.user.FluidPumpBlock
import top.kmar.mi.data.properties.RelativeDirectionEnum
import java.util.*
import kotlin.math.min

/**
 * 水泵方块的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity(FluidPumpBlock.NAME)
@EventBusSubscriber
open class EUFluidPump : FrontTileEntity(), IFluid, ITickable {

    /** 最大存储容量 */
    var maxCapacity = 10000
    /** 一次性最多运送的流体量 */
    var maxPower = 2000
    /** 基础电能消耗 */
    var baseLoss = 30
    /** 内部存储的流体 */
    @field:AutoSave private var data = FluidData.empty()
    /** 水泵面板方向 */
    @field:AutoSave var panelFacing = EnumFacing.WEST
        set(value) {
            field = value
            val list = maySide(value)
            if (!list.contains(side)) side = list[0]
        }
    /** 水泵输出方向 */
    @field:AutoSave var side = EnumFacing.NORTH
        set(value) {
            field = value
            val list = mayPanel(value)
            if (!list.contains(panelFacing)) panelFacing = list[0]
        }
    /** 是否工作 */
    @field:AutoSave
    var start = false
    /** 是否正在工作 */
    var working = false
        get() = field && start
    private var consume = 0

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === BlockNetworkCapability.capObj) {
            return BlockNetworkCapability.capObj.cast { data, _ ->
                data as NBTTagCompound
                side = EnumFacing.values()[data.getByte("side").toInt()]
                panelFacing = EnumFacing.values()[data.getByte("fac").toInt()]
                working = data.getBoolean("work")
                start = data.getBoolean("start")
                world.markBlockRangeForRenderUpdate(pos, pos)
            }
        }
        if (capability === TRANSFER)
            return if (facing == null || facing.axis === side.axis) TRANSFER.cast(this) else null
        if (facing?.axis === side.axis) return null
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === TRANSFER)
            return facing == null || facing.axis === side.axis
        return facing?.axis === side.axis && super.hasCapability(capability, facing)
    }

    override fun isEmpty() = data.isEmpty

    override fun isFull() = data.amount == maxCapacity

    /** 水泵不支持外部主动送水 */
    override fun insert(
        queue: FluidQueue,
        facing: EnumFacing,
        simulate: Boolean,
        report: TransportReport
    ): Int = 0

    /** 水泵不支持外部主动抽水 */
    override fun extract(
        amount: Int,
        facing: EnumFacing?,
        simulate: Boolean,
        report: TransportReport
    ): FluidQueue = FluidQueue.empty()

    private var oldState = working
    private val clock = TickClock(20)

    override fun update() {
        if (world.isClient()) {
            removeTickable()
            return
        }
        if (clock.notContinue()) return
        val energy = requestEnergy(baseLoss)
        if (!checkEnergy(energy)) return
        consume = energy.capacity
        if (start) {
            consume += pumpFluidOut()
            consume += pumpFluidIn()
            working = true
            markDirty()
        } else working = false
        send(marked || working != oldState)
        oldState = working
    }

    /** 向外部泵水 */
    private fun pumpFluidOut(): Int {
        val fluid = getFluidDirect(true) ?: return 0
        val simulate = TransportReport()
        val testQueue = FluidQueue.from(data.copy(min(data.amount, maxPower)))
        fluid.insert(testQueue, side, true, simulate)
        if (simulate.priceTotal == 0) return 0
        @Suppress("DuplicatedCode")
        if (simulate.priceTotal > maxPower) return maxPower
        val energy = requestEnergy(simulate.priceTotal)
        if (!checkEnergy(energy)) return energy.capacity
        if (energy.capacity < simulate.priceTotal - 10) return energy.capacity
        val report = TransportReport()
        val queue = FluidQueue.from(data.copy(min(data.amount, maxPower)))
        val pump = fluid.insert(queue, side, false, report)
        data.minusAmount(pump)
        return energy.capacity
    }

    /**
     * 从外部泵入水
     *
     * 如果泵入的流体种类和已存储的不一样，则两者会相互抵消
     */
    private fun pumpFluidIn(): Int {
        val fluid = getFluidDirect(false) ?: return 0
        val simulate = TransportReport()
        val pump = min(maxPower, maxCapacity - data.amount)
        fluid.extract(pump, side, true, simulate)
        if (simulate.priceTotal > maxPower) return maxPower
        val energy = requestEnergy(simulate.priceTotal)
        if (!checkEnergy(energy)) return energy.capacity
        if (energy.capacity < simulate.priceTotal - 10) return energy.capacity
        val report = TransportReport()
        val queue = fluid.extract(pump, side, false, report)
        while (!queue.isEmpty) {
            val value = queue.popHead(Int.MAX_VALUE)
            if (value.matchFluid(data)) data.plus(value)
            else if (data.amount >= value.amount) data.minusAmount(value.amount)
            else data = value.copy(value.amount - data.amount)
        }
        return energy.capacity
    }

    private fun checkEnergy(energy: EleEnergy): Boolean {
        if (energy.voltage > maxVoltage) {
            explode(3F, false)
            return false
        }
        return energy.voltage >= minVoltage
    }

    /** 计算出水口在面板的哪一个方向 */
    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    fun calculateFront() = when (panelFacing.axis) {
        EnumFacing.Axis.X -> {
            val result = when (side) {
                EnumFacing.NORTH -> RelativeDirectionEnum.LEFT
                EnumFacing.SOUTH -> RelativeDirectionEnum.RIGHT
                else -> throw AssertionError("内部错误")
            }
            if (panelFacing == EnumFacing.WEST) result else result.opposite()
        }
        EnumFacing.Axis.Y -> {
            val result = when (side) {
                EnumFacing.SOUTH -> RelativeDirectionEnum.UP
                EnumFacing.NORTH -> RelativeDirectionEnum.DOWN
                EnumFacing.EAST -> RelativeDirectionEnum.LEFT
                EnumFacing.WEST -> RelativeDirectionEnum.RIGHT
                else -> throw AssertionError("内部错误")
            }
            if (panelFacing == EnumFacing.DOWN) result else result.opposite()
        }
        EnumFacing.Axis.Z -> {
            val result = when (side) {
                EnumFacing.WEST -> RelativeDirectionEnum.LEFT
                EnumFacing.EAST -> RelativeDirectionEnum.RIGHT
                else -> throw AssertionError("内部错误")
            }
            if (panelFacing == EnumFacing.SOUTH) result else result.opposite()
        }
    }

    override fun canLinkFluid(facing: EnumFacing) =
        facing.axis !== EnumFacing.Axis.Y &&
                (facing.axis !== panelFacing.axis || countLinks() == 0)

    override fun getFront() = panelFacing

    override fun linkFluid(facing: EnumFacing): Boolean {
        if (!canLinkFluid(facing)) return false
        linkData[facing] = true
        if (facing.axis !== side.axis) side = facing
        return true
    }

    /**
     * 获取指定方向上连接的方块的IFluid
     * @return 是否为出水方向
     */
    private fun getFluidDirect(positive: Boolean): IFluid? {
        val facing = if (positive) side else side.opposite
        if (!isLink(facing)) return null
        val target = pos.offset(facing)
        val te = world.getTileEntity(target)
        return te!!.getCapability(TRANSFER, facing)
    }

    override fun markDirty() {
        super.markDirty()
        marked = true
    }

    private val networkRecord = mutableListOf<UUID>()
    private var marked = false

    private fun send(refresh: Boolean = false) {
        if (refresh) networkRecord.clear()
        val message = NBTTagCompound().apply {
            setByte("side", side.ordinal.toByte())
            setByte("fac", panelFacing.ordinal.toByte())
            setBoolean("work", working)
            setBoolean("start", start)
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    companion object {

        const val maxVoltage = EleEnergy.COMMON + 100
        const val minVoltage = EleEnergy.COMMON - 30

        private val MAY_SIDE_X = listOf(EnumFacing.NORTH, EnumFacing.SOUTH)
        private val MAY_SIDE_Y = listOf(*EnumFacing.HORIZONTALS)
        private val MAY_SIDE_Z = listOf(EnumFacing.WEST, EnumFacing.EAST)

        private val MAY_PANEL_X =
            listOf(EnumFacing.NORTH, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.DOWN)
        private val MAY_PANEL_Y = listOf<EnumFacing>()
        private val MAY_PANEL_Z =
            listOf(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.UP)

        /** 判断指定的面板方向与管道方向是否匹配 */
        fun match(panel: EnumFacing, side: EnumFacing) = maySide(panel).contains(side)

        /**
         * 获取指定面板朝向下出水口管道可能在哪些方向
         * @return 一个不可修改的列表
         */
        fun maySide(panel: EnumFacing) = when (panel.axis) {
            EnumFacing.Axis.X -> MAY_SIDE_X
            EnumFacing.Axis.Y -> MAY_SIDE_Y
            EnumFacing.Axis.Z -> MAY_SIDE_Z
        }

        /**
         * 获取指定出/入水口方向下面板可能在哪些方向
         * @return 一个不可修改的列表
         */
        fun mayPanel(side: EnumFacing) = when (side.axis) {
            EnumFacing.Axis.X -> MAY_PANEL_X
            EnumFacing.Axis.Z -> MAY_PANEL_Z
            EnumFacing.Axis.Y -> MAY_PANEL_Y
        }

        @JvmStatic
        @SubscribeEvent
        fun registryGuiInfo(event: GuiLoader.MIGuiRegistryEvent) {
            event.registryInitTask(BlockGuiList.fluidPump) { gui ->
                val button = gui.queryCmpt("button") as ButtonCmpt
                val pump = gui.tileEntity as EUFluidPump
                button.addEventListener(IGraphicsListener.mouseClick) {
                    pump.start = !pump.start
                    pump.markDirty()
                    if (it != null) it.send2Service = true
                }
            }
            event.registryLoopTask(BlockGuiList.fluidPump) { gui ->
                val fluid = gui.getElementByID("fluid") as ProgressBarCmpt
                val consume = gui.getElementByID("consume") as ProgressBarCmpt
                val pump = gui.tileEntity as EUFluidPump
                // 更新容量进度条数据
                fluid.max = pump.maxCapacity
                fluid.value = pump.data.amount
                // 更新瞬时能量消耗数据
                consume.max = pump.maxPower
                consume.value = pump.consume
            }
            event.registryClientLoopTask(BlockGuiList.fluidPump) { gui ->
                val fluid = gui.getElementByID("fluid") as ProgressBarCmpt
                val consume = gui.getElementByID("consume") as ProgressBarCmpt
                val pump = gui.tileEntity as EUFluidPump
                val texts = gui.queryCmptAll("p")
                    .map { it.client as TextCmpt.TextCmptClient }
                val fluidText = I18n.format(
                    if (pump.data.isEmpty) "mi.gui.fluid_pump.null"
                    else pump.data.fluid!!.unlocalizedName
                )
                texts[0].text = I18n.format("mi.gui.fluid_pump.fluid",
                    "${fluid.value}/${fluid.max}", fluidText)
                texts[1].text = I18n.format("mi.gui.fluid_pump.consume",
                    "${consume.value}/${consume.max}")
                val button = gui.queryCmpt("button")
                button!!.attributes["value"] = I18n.format(if (pump.start) "mi.gui.open" else "mi.gui.close")
            }
        }

    }

}