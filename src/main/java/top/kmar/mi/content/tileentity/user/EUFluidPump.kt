package top.kmar.mi.content.tileentity.user

import net.minecraft.client.resources.I18n
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.capabilities.fluid.FluidCapability.TRANSFER
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.electricity.clock.OrdinaryCounter
import top.kmar.mi.api.electricity.info.BiggerVoltage
import top.kmar.mi.api.electricity.info.EleEnergy
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.fluid.data.FluidQueue
import top.kmar.mi.api.fluid.data.TransportReport
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.ButtonCmpt
import top.kmar.mi.api.graphics.components.ProgressBarCmpt
import top.kmar.mi.api.graphics.components.TextCmpt
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.net.IAutoNetwork
import top.kmar.mi.api.register.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.*
import top.kmar.mi.api.utils.container.IndexEnumMap
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
open class EUFluidPump : FrontTileEntity(), IFluid, ITickable, IAutoNetwork {

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    companion object {

        const val VOLTAGE = EleEnergy.COMMON

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
                val energy = gui.getElementByID("energy") as ProgressBarCmpt
                val consume = gui.getElementByID("consume") as ProgressBarCmpt
                val pump = gui.tileEntity as EUFluidPump
                // 更新容量进度条数据
                fluid.max = pump.maxCapacity
                fluid.value = pump.data.amount
                // 更新能量进度条数据
                energy.max = pump.maxEnergy
                energy.value = pump.nowEnergy
                // 更新瞬时能量消耗数据
                consume.max = pump.maxPower
                consume.value = pump.consume
            }
            event.registryClientLoopTask(BlockGuiList.fluidPump) { gui ->
                val fluid = gui.getElementByID("fluid") as ProgressBarCmpt
                val energy = gui.getElementByID("energy") as ProgressBarCmpt
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
                texts[1].text = I18n.format("mi.gui.fluid_pump.energy",
                    "${energy.value}/${energy.max}")
                texts[2].text = I18n.format("mi.gui.fluid_pump.consume",
                    "${consume.value}/${consume.max}")
                val button = gui.queryCmpt("button")
                button!!.attributes["value"] = I18n.format(if (pump.start) "mi.gui.open" else "mi.gui.close")
            }
        }

    }

    /** 最大存储容量 */
    var maxCapacity = 10000
    /** 一次性最多运送的流体量 */
    var maxPower = 2000
    /** 基础电能消耗 */
    var baseLoss = 5
    /** 内部存储的流体 */
    @field:AutoSave private var data = FluidData.empty()
    /** 出口入口是否连接 */
    @field:AutoSave private val linked = IndexEnumMap(EnumFacing.values())
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

    init {
        val counter = OrdinaryCounter(100)
        counter.bigger = BiggerVoltage(
            2f,
            EnumBiggerVoltage.BOOM
        )
        @Suppress("LeakingThis")
        setCounter(counter)
        maxEnergy = 100
    }

    override fun isReceiveAllowable(facing: EnumFacing) = facing.axis !== side.axis

    override fun isExtractAllowable(facing: EnumFacing?) = false

    override fun getExVoltage() = 0

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?) =
        if (capability == TRANSFER) TRANSFER.cast(this) else super.getCapability(capability, facing)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
        if (super.hasCapability(capability, facing)) true else capability == TRANSFER

    override fun isEmpty() = data.isEmpty

    override fun isFull() = data.amount == maxCapacity

    override fun onReceive(energy: EleEnergy): Boolean {
        if (energy.voltage > VOLTAGE) counter.plus()
        return true
    }

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
        val old = nowEnergy
        if (start && shrinkEnergy(baseLoss)) {
            pumpFluidOut()
            pumpFluidIn()
            working = true
            markDirty()
        } else working = false
        consume = old - nowEnergy
        send(marked || working != oldState)
        oldState = working
    }

    /** 向外部泵水 */
    private fun pumpFluidOut() {
        val fluid = getFluidDirect(true) ?: return
        val simulate = TransportReport()
        val testQueue = FluidQueue.from(data.copy(min(data.amount, maxPower)))
        fluid.insert(testQueue, side, true, simulate)
        if (simulate.priceTotal == 0) return
        if (simulate.priceTotal > nowEnergy) return
        val report = TransportReport()
        val queue = FluidQueue.from(data.copy(min(data.amount, maxPower)))
        val pump = fluid.insert(queue, side, false, report)
        shrinkEnergy(report.priceTotal)
        data.minusAmount(pump)
    }

    /**
     * 从外部泵入水
     *
     * 如果泵入的流体种类和已存储的不一样，则两者会相互抵消
     */
    private fun pumpFluidIn() {
        val fluid = getFluidDirect(false) ?: return
        val simulate = TransportReport()
        val pump = min(maxPower, maxCapacity - data.amount)
        fluid.extract(pump, side, true, simulate)
        if (simulate.priceTotal > nowEnergy) return
        val report = TransportReport()
        val queue = fluid.extract(pump, side, false, report)
        shrinkEnergy(report.priceTotal)
        while (!queue.isEmpty) {
            val value = queue.popHead(Int.MAX_VALUE)
            if (value.matchFluid(data)) data.plus(value)
            else if (data.amount >= value.amount) data.minusAmount(value.amount)
            else data = value.copy(value.amount - data.amount)
        }
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

    override fun canLinkEle(facing: EnumFacing) = facing.axis !== side.axis && facing !== panelFacing

    override fun canLinkFluid(facing: EnumFacing) =
        facing.axis !== EnumFacing.Axis.Y &&
                ((facing.axis !== panelFacing.axis && super.canLinkEle(facing)) || linked.isInit())

    override fun getFront() = panelFacing

    override fun linkFluid(facing: EnumFacing): Boolean {
        if (!canLinkFluid(facing)) return false
        linked[facing] = true
        if (facing.axis !== side.axis) side = facing
        return true
    }

    override fun linkEle(pos: BlockPos): Boolean {
        val facing = this.pos.whatFacing(pos)
        if (!canLinkEle(facing)) return false
        linked[facing] = true
        return true
    }

    override fun unlink(facing: EnumFacing) = linked.set(facing, false)

    override fun isLinked(facing: EnumFacing) = linked[facing]

    /**
     * 获取指定方向上连接的方块的IFluid
     * @return 是否为出水方向
     */
    private fun getFluidDirect(positive: Boolean): IFluid? {
        val facing = if (positive) side else side.opposite
        if (!isLinked(facing)) return null
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
        IOUtil.sendBlockMessageIfNotUpdate(this, networkRecord, 128) {
            NBTTagCompound().apply {
                setByte("side", side.ordinal.toByte())
                setByte("fac", panelFacing.ordinal.toByte())
                setBoolean("work", working)
                setBoolean("start", start)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun receive(reader: NBTBase) {
        val nbt = reader as NBTTagCompound
        side = EnumFacing.values()[nbt.getByte("side").toInt()]
        panelFacing = EnumFacing.values()[nbt.getByte("fac").toInt()]
        working = nbt.getBoolean("work")
        start = nbt.getBoolean("start")
        world.markBlockRangeForRenderUpdate(pos, pos)
    }

}