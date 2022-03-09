package top.kmar.mi.content.tileentity.user

import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.capabilities.fluid.FluidCapability.TRANSFER
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.electricity.clock.OrdinaryCounter
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.fluid.data.FluidQueue
import top.kmar.mi.api.fluid.data.TransportReport
import top.kmar.mi.api.gui.component.ButtonComponent
import top.kmar.mi.api.gui.component.CommonProgress
import top.kmar.mi.api.gui.component.CommonProgress.Front.RIGHT
import top.kmar.mi.api.gui.component.CommonProgress.ProgressStyle.DOWN
import top.kmar.mi.api.gui.component.CommonProgress.Style.STRIPE
import top.kmar.mi.api.gui.component.StringComponent
import top.kmar.mi.api.net.IAutoNetwork
import top.kmar.mi.api.register.others.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.IOUtil
import top.kmar.mi.api.utils.TickClock
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.data.io.Storage
import top.kmar.mi.content.blocks.machine.user.FluidPumpBlock
import top.kmar.mi.data.info.BiggerVoltage
import top.kmar.mi.data.info.EnumBiggerVoltage
import top.kmar.mi.data.info.EnumVoltage
import top.kmar.mi.data.info.RelativeDirectionEnum
import java.util.*
import kotlin.math.min

/**
 * 水泵方块的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity(FluidPumpBlock.NAME)
open class EUFluidPump : FrontTileEntity(), IFluid, ITickable, IAutoNetwork {

    companion object {

        private val MAY_SIDE_X = listOf(EnumFacing.NORTH, EnumFacing.SOUTH)
        private val MAY_SIDE_Y = listOf(*EnumFacing.HORIZONTALS)
        private val MAY_SIDE_Z = listOf(EnumFacing.WEST, EnumFacing.EAST)

        private val MAY_PANEL_X =
            listOf(EnumFacing.NORTH, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.DOWN)
        private val MAY_PANEL_Z =
            listOf(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.UP)

        /** 判断指定的面板方向与管道方向是否匹配 */
        fun match(panel: EnumFacing, side: EnumFacing) = maySide(panel).contains(side)

        /**
         * 获取指定面板朝向下出水口管道可能在哪些方向
         * @return 一个不可修改的列表
         */
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
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
            else -> throw AssertionError()
        }

    }

    /** 最大存储容量 */
    var maxCapacity = 10000
    /** 一次性最多运送的流体量 */
    var maxPower = 2000
    /** 基础电能消耗 */
    var baseLoss = 5
    /** 内部存储的流体 */
    @Storage private var data = FluidData.empty()
    /** 出口入口是否连接 */
    @Storage private val linked = IndexEnumMap<EnumFacing>()
    /** 水泵面板方向 */
    @Storage var panelFacing = EnumFacing.WEST
        set(value) {
            field = value
            val list = maySide(value)
            if (!list.contains(side)) side = list[0]
        }
    /** 水泵输出方向 */
    @Storage var side = EnumFacing.NORTH
        set(value) {
            field = value
            val list = mayPanel(value)
            if (!list.contains(panelFacing)) panelFacing = list[0]
        }
    /** 是否工作 */
    @Storage var start = false
    /** 是否正在工作 */
    var working = false
        get() = field && start

    val guiEnergyText = StringComponent("mi.gui.fluid_pump.energy")
    val guiEnergy = CommonProgress(STRIPE, RIGHT)
    val guiConsumeText = StringComponent("mi.gui.fluid_pump.consume")
    val guiConsume = CommonProgress(STRIPE, RIGHT)
    val guiText = StringComponent()
    val guiFluid = CommonProgress(STRIPE, RIGHT)
    val guiButton = ButtonComponent(19, 30)

    init {
        setReceiveRange(1, 100, EnumVoltage.C, EnumVoltage.D)
        val counter = OrdinaryCounter(100)
        counter.bigger = BiggerVoltage(2f, EnumBiggerVoltage.BOOM)
        setCounter(counter)
        isReceive = true
        maxEnergy = 100
        guiEnergy.stringShower = DOWN
        guiConsume.stringShower = DOWN
        guiFluid.stringShower = DOWN
        guiButton.setAction { _, _ ->
            if (!world.isRemote) {
                start = !start
                send(true)
            }
        }
    }

    override fun isReAllowable(facing: EnumFacing) = facing.axis !== side.axis

    override fun isExAllowable(facing: EnumFacing?) = false

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?) =
        if (capability == TRANSFER) TRANSFER.cast(this) else super.getCapability(capability, facing)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
        if (super.hasCapability(capability, facing)) true else capability == TRANSFER

    override fun isEmpty() = data.isEmpty

    override fun isFull() = data.amount == maxCapacity

    /** 水泵不支持外部主动送水 */
    override fun insert(
        queue: FluidQueue,
        facing: EnumFacing,
        simulate: Boolean,
        report: TransportReport
    ): Int = 0

    override fun extract(
        amount: Int,
        facing: EnumFacing?,
        simulate: Boolean,
        report: TransportReport
    ): FluidQueue = FluidQueue.empty()

    private var oldState = working
    private val clock = TickClock(20)

    override fun update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this)
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
        updateGUI(old - nowEnergy)
        send(marked || working != oldState)
        oldState = working
    }

    /** 向外部泵水 */
    private fun pumpFluidOut() {
        val fluid = getFluidDirect(true) ?: return
        val simulate = TransportReport()
        val value = FluidQueue.from(data.copy(min(data.amount, maxPower)))
        if (fluid.insert(value, side, true, simulate) == 0) return
        if (simulate.priceTotal > nowEnergy) return
        val report = TransportReport()
        val pump = fluid.insert(value, side, false, report)
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

    private fun updateGUI(consume: Int) {
        guiEnergy.max = maxEnergy
        guiConsume.max = maxEnergy
        guiFluid.max = maxCapacity

        guiEnergy.now = nowEnergy
        guiConsume.now = consume
        guiFluid.now = data.amount

        if (world.isRemote) guiText.string = I18n.format(data.fluid!!.unlocalizedName)
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
        (facing.axis !== panelFacing.axis && super.canLinkEle(facing)) || linked.isInit

    override fun getFront() = panelFacing

    override fun linkFluid(facing: EnumFacing): Boolean {
        if (!canLinkFluid(facing)) return false
        linked.set(facing, true)
        if (facing.axis === panelFacing.axis) {
            for (value in EnumFacing.HORIZONTALS) {
                if (value.axis !== facing.axis) {
                    panelFacing = value
                    break
                }
            }
        }
        return true
    }

    override fun linkEle(pos: BlockPos): Boolean {
        val facing = WorldUtil.whatFacing(this.pos, pos)
        if (!canLinkEle(facing)) return false
        linked.set(facing, true)
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
            val operator = ByteDataOperator()
            operator.writeByte(side.ordinal.toByte())
            operator.writeByte(panelFacing.ordinal.toByte())
            operator.writeBoolean(working)
            operator.writeBoolean(start)
            operator
        }
    }

    @SideOnly(Side.CLIENT)
    override fun receive(reader: IDataReader) {
        side = EnumFacing.values()[reader.readByte().toInt()]
        panelFacing = EnumFacing.values()[reader.readByte().toInt()]
        working = reader.readBoolean()
        start = reader.readBoolean()
        world.markBlockRangeForRenderUpdate(pos, pos)
        val value = data.fluid?.unlocalizedName ?: "mi.gui.fluid_pump.null"
        guiText.string = I18n.format("mi.gui.fluid_pump.fluid", I18n.format(value))
        guiButton.text = I18n.format(if (start) "mi.gui.open" else "mi.gui.close")
    }

}