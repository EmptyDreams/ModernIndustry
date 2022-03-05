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
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.data.enums.IndexEnumMap
import top.kmar.mi.api.utils.data.io.Storage
import top.kmar.mi.data.info.BiggerVoltage
import top.kmar.mi.data.info.EnumBiggerVoltage
import top.kmar.mi.data.info.EnumVoltage
import kotlin.math.min

/**
 * 水泵方块的TileEntity
 * @author EmptyDreams
 */
open class EUFluidPump : FrontTileEntity(), IFluid, ITickable {

    /** 最大存储容量 */
    var maxCapacity = 10000
    /** 基础电能消耗 */
    var baseLoss = 5
    /** 内部存储的流体 */
    @Storage private val data = FluidData.empty()
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

    override fun isReAllowable(facing: EnumFacing?) = true

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
    ): FluidQueue {
        val result = FluidQueue.empty()
        if (facing !== front) return result
        val value = data.copy(min(amount, data.amount))
        report.insert(facing, value)
        result.pushHead(value)
        if (!simulate) data.minusAmount(value.amount)
        return result
    }

    override fun update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this)
            return
        }
        val ingore = TransportReport()
        val report = TransportReport()
        TODO()
    }

    override fun link(facing: EnumFacing?): Boolean {
        linked.set(facing, true)
        return true
    }

    override fun unlink(facing: EnumFacing?) = linked.set(facing, false)

    override fun isLinked(facing: EnumFacing?) = linked[facing]

}