package top.kmar.mi.api.fluid

import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.capabilities.fluid.FluidCapability
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.fluid.data.FluidQueue
import top.kmar.mi.api.fluid.data.TransportReport
import top.kmar.mi.api.net.IAutoNetwork
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.IOUtil
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.data.io.Storage
import java.util.*
import javax.annotation.Nonnull

/**
 *
 * @author EmptyDreams
 */
abstract class FTTileEntity : BaseTileEntity(), IAutoNetwork, IFluid, ITickable {

    companion object {

        /** 运送流体时流体运送方向优先级列表  */
        val PUSH_EACH_PRIORITY = arrayOf(
            EnumFacing.DOWN, EnumFacing.NORTH,
            EnumFacing.WEST, EnumFacing.SOUTH,
            EnumFacing.EAST, EnumFacing.UP
        )
        /** 吸取流体时吸取方向优先级列表  */
        val POP_EACH_PRIORITY = arrayOf(
            EnumFacing.UP, EnumFacing.EAST,
            EnumFacing.SOUTH, EnumFacing.WEST,
            EnumFacing.NORTH, EnumFacing.DOWN
        )

    }

    /** 六个方向的连接数据  */
    @Storage
    protected val linkData = IndexEnumMap<EnumFacing>()
    /** 六个方向的管塞数据  */
    @Storage
    protected val plugData = EnumMap<EnumFacing, ItemStack>(EnumFacing::class.java)
    /** 管道内存储的流体量  */
    @Storage
    protected var fluidData = FluidData.empty()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
        if (super.hasCapability(capability, facing)) true
        else capability === FluidCapability.TRANSFER && (facing == null || hasAperture(facing))

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
        if (capability === FluidCapability.TRANSFER && (facing == null || hasAperture(facing)))
            FluidCapability.TRANSFER.cast(this)
        else super.getCapability(capability, facing)

    /**
     * 缺省算法因为自身缺陷，不会维护流体在管道中的顺序，所以有可能有一部分原本在流体管道中的流体逆流回传入的 queue 中。
     *
     * 该方法会优先将放置在队尾的流体送入管道，因为这样做可以尽可能地保证流体在管道中地顺序。
     */
    override fun insert(queue: FluidQueue, facing: EnumFacing, simulate: Boolean, report: TransportReport): Int {
        if (!isOpen(facing.opposite)) return 0
        var result = 0
        val newData = queue.popTail(getMaxAmount())
        report.insert(facing, newData)
        if (isEmpty) {
            result += newData.amount
            if (!simulate) fluidData.plus(newData)
        } else {
            queue.pushTail(fluidData)
            if (!simulate) fluidData = newData
            for (value in PUSH_EACH_PRIORITY) {
                if (queue.isEmpty) break
                if (value == facing.opposite) continue
                val fluid = getFluidDirect(value) ?: continue
                result += fluid.insert(queue, value, simulate, report)
            }
        }
        return result
    }

    /** 该方法保证返回的队列中头部为最先取出的流体  */
    override fun extract(
        amount: Int,
        facing: EnumFacing,
        simulate: Boolean,
        report: TransportReport
    ): FluidQueue {
        val result = FluidQueue.empty()
        if (!isOpen(facing)) return result
        var copy = amount
        for (value in POP_EACH_PRIORITY) {
            if (copy == 0) break
            if (value == facing) continue
            val fluid = getFluidDirect(value)
            val queue = fluid?.extract(copy, value.opposite, simulate, report) ?:
                            pumpFromWorld(amount, facing, simulate, report)
            copy -= result.pushTail(queue)
        }
        val value = fluidData.copy(amount.coerceAtMost(fluidData.amount))
        fluidData = result.popTail(value.amount)
        result.pushHead(value)
        return result
    }

    @Nonnull
    protected fun pumpFromWorld(
        amount: Int, facing: EnumFacing, simulate: Boolean, report: TransportReport
    ): FluidQueue {
        var amountCopy = amount
        val stack = Stack<BlockPos>()
        val record: MutableSet<BlockPos> = HashSet()
        stack.push(pos.offset(facing.opposite))
        val result = FluidQueue.empty()
        while (!stack.empty() && amountCopy != 0) {
            val value = stack.pop()
            if (record.contains(value)) continue
            record.add(value)
            val thatBlock = world.getBlockState(value).block
            var plus: FluidData? = null
            if (thatBlock is IFluidBlock) {
                val thatFluid = thatBlock as IFluidBlock
                val fluidStack = thatFluid.drain(world, value, false)
                if (fluidStack == null || fluidStack.amount == 0 || amountCopy < fluidStack.amount) continue
                if (!simulate) thatFluid.drain(world, value, true)
                amountCopy -= fluidStack.amount
                plus = FluidData(fluidStack)
            } else if (amountCopy >= 1000 && thatBlock is BlockLiquid) {
                if (world.getBlockState(value).getValue(BlockLiquid.LEVEL) == 0) {
                    amountCopy -= 1000
                    if (!simulate) world.setBlockToAir(value)
                    val fluidType = if (thatBlock === Blocks.LAVA) FluidRegistry.LAVA else FluidRegistry.WATER
                    plus = FluidData(fluidType, 1000)
                }
            } else continue
            if (plus != null) {
                report.insert(facing.opposite, plus)
                result.pushTail(plus)
                world.markBlockRangeForRenderUpdate(value, value)
            }
            for (enumFacing in PUSH_EACH_PRIORITY) stack.push(value.offset(enumFacing))
        }
        return result
    }

    /**
     * 获取可存储的最大量
     *
     * 该方法可能会在对象构造函数中调用，必须保证构造过程中也可以返回正确的值
     */
    open fun getMaxAmount() = 1000

    override fun isEmpty() = fluidData.isEmpty

    override fun isFull() = fluidData.amount == getMaxAmount()

    override fun unlink(facing: EnumFacing) {
        linkData[facing] = false
        updateBlockState(false)
    }

    override fun isLinked(facing: EnumFacing) = linkData[facing]

    /**
     * 在指定方向上设置管塞
     * @param plug 管塞物品对象，为null表示去除管塞
     * @param facing 方向
     * @return 是否设置成功（若管塞已经被设置也返回true）
     */
    open fun setPlug(facing: EnumFacing, plug: ItemStack?): Boolean {
        if (!canSetPlug(facing)) return false
        if (plug == null) plugData[facing] = null else plugData[facing] = plug.copy()
        markDirty()
        return true
    }

    /**
     * 判定指定方向上是否有管塞
     * @param facing 指定方向
     */
    open fun hasPlug(facing: EnumFacing) = plugData[facing] != null

    /** 统计管道出口数量  */
    open fun getLinkedAmount() = Arrays.stream(EnumFacing.values()).filter { facing: EnumFacing ->
        isLinked(facing)
    }.count().toInt()

    /**
     * 获取指定方向上连接的方块的IFluid
     * @return 如果指定方向上没有连接方块则返回null
     */
    protected fun getFluidDirect(facing: EnumFacing): IFluid? {
        if (!isLinked(facing)) return null
        val target = pos.offset(facing)
        val te = world.getTileEntity(target)
        return te!!.getCapability(FluidCapability.TRANSFER, facing)
    }

    /** 判断指定方向是否含有开口  */
    abstract fun hasAperture(facing: EnumFacing): Boolean

    /** 判断指定方向上能否通过流体  */
    open fun isOpen(facing: EnumFacing) = hasAperture(facing) && !hasPlug(facing)

    /** 判断指定方向上是否可以设置管塞  */
    open fun canSetPlug(facing: EnumFacing) = !(hasPlug(facing) || isLinked(facing))

    override fun receive(@Nonnull reader: IDataReader) {
        linkData.value = reader.readByte().toInt()
        syncClient(reader)
        updateBlockState(true)
    }

    /**
     *
     * 存储已经更新过的玩家列表
     *
     * 不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
     * 方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
     */
    private val players: MutableList<UUID> = ArrayList()

    /** 用于服务端写入需要同步的数据，写入的数据会发送给客户端  */
    protected abstract fun sync(writer: IDataWriter)

    /** 用于客户端同步数据  */
    @SideOnly(Side.CLIENT)
    protected abstract fun syncClient(reader: IDataReader)

    /** 向客户端发送服务端存储的信息并更新显示  */
    fun send() {
        if (world.isRemote) return
        if (players.size == world.playerEntities.size) return
        IOUtil.sendBlockMessageIfNotUpdate(this, players, 128) {
            val operator = ByteDataOperator(1)
            operator.writeByte(linkData.value.toByte())
            sync(operator)
            operator
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        send()
        if (isRemove && players.size != world.playerEntities.size) {
            isRemove = false
            WorldUtil.addTickable(this)
        }
        return super.getUpdateTag()
    }

    /**
     * 更新IBlockState
     * @param isRunOnClient 是否在客户端运行
     */
    fun updateBlockState(isRunOnClient: Boolean) {
        markDirty()
        if (world.isRemote) {
            if (!isRunOnClient) return
        } else {
            players.clear()
            send()
        }
        val oldState = world.getBlockState(pos)
        val newState = oldState.getActualState(world, pos)
        WorldUtil.setBlockState(world, pos, newState)
    }

    /**
     * 方法内包含管道正常运行的方法，重写时务必使用`super.update()`调用
     */
    override fun update() {
        send()
        updateTickableState()
    }

    /** 存储该TE是否已经从tickable的列表中移除  */
    private var isRemove = false

    /**
     * 更新管道tickable的状态
     */
    fun updateTickableState() {
        if (isRemove) {
            isRemove = false
            WorldUtil.addTickable(this)
        } else {
            isRemove = true
            WorldUtil.removeTickable(this)
        }
    }

}