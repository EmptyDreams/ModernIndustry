package top.kmar.mi.content.tileentity.user

import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import top.kmar.mi.api.capabilities.fluid.FluidCapability.TRANSFER
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.electricity.clock.OrdinaryCounter
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.fluid.data.FluidQueue
import top.kmar.mi.api.fluid.data.TransportReport
import top.kmar.mi.api.register.others.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.data.enums.IndexEnumMap
import top.kmar.mi.api.utils.data.io.Storage
import top.kmar.mi.content.blocks.machine.user.FluidPumpBlock
import top.kmar.mi.data.info.BiggerVoltage
import top.kmar.mi.data.info.EnumBiggerVoltage
import top.kmar.mi.data.info.EnumVoltage
import kotlin.math.min

/**
 * 水泵方块的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity(FluidPumpBlock.NAME)
open class EUFluidPump : FrontTileEntity(), IFluid, ITickable {

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
    /** 水泵输出方向 */
    private var front = EnumFacing.NORTH
    override fun getFront(): EnumFacing = front

    init {
        setReceiveRange(1, 100, EnumVoltage.C, EnumVoltage.D)
        val counter = OrdinaryCounter(100)
        counter.bigger = BiggerVoltage(2f, EnumBiggerVoltage.BOOM)
        setCounter(counter)
        isReceive = true
        maxEnergy = 100
    }

    override fun isReAllowable(facing: EnumFacing) = facing.axis !== front.axis

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

    override fun update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this)
            return
        }
        if (!shrinkEnergy(baseLoss)) return
        pumpFluidIn()
        pumpFluidOut()
    }

    /** 向外部泵水 */
    private fun pumpFluidOut() {
        val fluid = getFluidDirect(true) ?: return
        val simulate = TransportReport()
        val value = FluidQueue.from(data.copy(min(data.amount, maxPower)))
        if (fluid.insert(value, front, true, simulate) == 0) return
        if (simulate.priceTotal > nowEnergy) return
        val report = TransportReport()
        val pump = fluid.insert(value, front, false, report)
        shrinkEnergy(report.priceTotal)
        data.minusAmount(pump)
        TODO("暂时不支持向世界输出流体，也不支持运输一部分流体")
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
        fluid.extract(pump, front, true, simulate)
        if (simulate.priceTotal > nowEnergy) return
        val report = TransportReport()
        val queue = fluid.extract(pump, front, false, report)
        shrinkEnergy(report.priceTotal)
        while (!queue.isEmpty) {
            val value = queue.popHead(Int.MAX_VALUE)
            if (value.matchFluid(data)) data.plus(value)
            else if (data.amount >= value.amount) data.minusAmount(value.amount)
            else data = value.copy(value.amount - data.amount)
        }
        TODO("暂时不支持从世界泵入流体")
    }

    override fun canLink(facing: EnumFacing): Boolean {
        return super.canLink(facing) && facing.axis !== front.axis
    }

    override fun link(facing: EnumFacing): Boolean {
        if (!canLink(facing)) return false
        linked.set(facing, true)
        return true
    }

    override fun unlink(facing: EnumFacing) = linked.set(facing, false)

    override fun isLinked(facing: EnumFacing) = linked[facing]

    /**
     * 获取指定方向上连接的方块的IFluid
     * @return 是否为出水方向
     */
    fun getFluidDirect(positive: Boolean): IFluid? {
        val facing = if (positive) front else front.opposite
        if (!isLinked(facing)) return null
        val target = pos.offset(facing)
        val te = world.getTileEntity(target)
        return te!!.getCapability(TRANSFER, facing)
    }

}