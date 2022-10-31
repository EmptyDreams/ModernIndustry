package top.kmar.mi.api.fluid

import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.HORIZONTALS
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.capabilities.fluid.FluidCapability
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.fluid.data.FluidQueue
import top.kmar.mi.api.fluid.data.TransportReport
import top.kmar.mi.api.net.IAutoNetwork
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.*
import top.kmar.mi.api.utils.container.DoubleIndexEnumMap
import top.kmar.mi.api.utils.expands.*
import java.util.*
import javax.annotation.Nonnull

/**
 * 流体管道的TileEntity的父类
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

        @JvmStatic
        private val HOR_LIST = ArrayList<Array<EnumFacing>>(24)

        @JvmStatic
        fun randomHorizontals() = HOR_LIST[random.nextInt(HOR_LIST.size)]

        init {
            val record = BooleanArray(HORIZONTALS.size) {false}
            val array = Array(HORIZONTALS.size) {EnumFacing.UP}
            for (i in HORIZONTALS.indices) dfs(0, i, record, array)
        }

        @JvmStatic
        private fun dfs(deep: Int, index: Int, record: BooleanArray, array: Array<EnumFacing>) {
            array[deep] = HORIZONTALS[index]
            if (deep == 3) HOR_LIST.add(array.clone())
            else {
                record[index] = true
                for (i in HORIZONTALS.indices) {
                    if (!record[i]) dfs(deep + 1, i, record, array)
                }
                record[index] = false
            }
        }

    }

    /**
     * 六个方向的连接数据
     *
     * 规定：左值用来表示该方向上是否连接方块，右值表示该方块是否为管道
     */
    @AutoSave
    protected val linkData = DoubleIndexEnumMap(EnumFacing.values())
    /** 六个方向的管塞数据  */
    @AutoSave
    protected val plugData = EnumMap<EnumFacing, ItemStack>(EnumFacing::class.java)
    /** 管道内存储的流体量  */
    @AutoSave
    protected var fluidData = FluidData.empty()
    private var lineCode: BlockPos? = null

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
        val newData = queue.popTail(getMaxAmount())
        report.insert(facing, newData)
        queue.pushTail(fluidData)
        if (!simulate) fluidData = newData
        for (value in PUSH_EACH_PRIORITY) {
            if (queue.isEmpty) break
            if (value == facing.opposite || !isOpen(value)) continue
            val fluid = getFluidDirect(value)
            fluid?.insert(queue, value, simulate, report) ?: pump2World(queue, facing, simulate, report)
        }
        return newData.amount
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

    protected enum class JudgeResultEnum {EQUALS, COVER, FAILED}

    /** 将流体从管道放入世界 */
    protected fun pump2World(
        queue: FluidQueue, facing: EnumFacing,
        simulate: Boolean, report: TransportReport
    ): Int {
        var result = 0
        val stack = Stack<BlockPos>()
        val record = HashSet<BlockPos>()
        stack.push(pos.offset(facing))
        while (!(queue.isEmpty || stack.isEmpty())) {
            val dist = stack.pop()
            if (record.contains(dist)) continue
            record.add(dist)
            val value = queue.popTail(Int.MAX_VALUE)
            val block = value.fluid?.block
            var base = 1.0
            when (judge(block, dist)) {
                JudgeResultEnum.EQUALS -> {}
                JudgeResultEnum.COVER -> {
                    val amount = if (block is IFluidBlock) block.place(world, dist, value.toStack(), !simulate)
                                    else world.pushFluid(dist, value, simulate)
                    if (amount == 0) continue
                    report.insert(facing, value.copy((amount * base).toInt()))
                    value.minusAmount(amount)
                    result += amount
                }
                JudgeResultEnum.FAILED -> {
                    queue.pushTail(value)
                    continue
                }
            }
            base += 0.1
            queue.pushTail(value)
            stack.push(dist.offset(EnumFacing.UP))
            for (enumFacing in randomHorizontals()) stack.push(dist.offset(enumFacing))
            stack.push(dist.offset(EnumFacing.DOWN))
        }
        return result
    }

    protected open fun judge(block: Block?, pos: BlockPos): JudgeResultEnum {
        if (block == null) return JudgeResultEnum.FAILED
        val state = world.getBlockState(pos)
        if (state.material.isSolid || !state.block.isReplaceable(world, pos)) return JudgeResultEnum.FAILED
        if (state.block == block && state.getValue(BlockLiquid.LEVEL) == 0) return JudgeResultEnum.EQUALS
        return JudgeResultEnum.COVER
    }

    /** 将流体从世界抽入管道 */
    @Nonnull
    protected fun pumpFromWorld(
        amount: Int, facing: EnumFacing, simulate: Boolean, report: TransportReport
    ): FluidQueue {
        var amountCopy = amount
        val stack = Stack<BlockPos>()
        val record = HashSet<BlockPos>()
        stack.push(pos.offset(facing.opposite))
        val result = FluidQueue.empty()
        var base = 1.0
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
                result.pushTail(plus)
                plus.amount = (plus.amount * base).toInt()
                report.insert(facing, plus)
                world.markBlockRangeForRenderUpdate(value, value)
            }
            base += 0.1
            stack.push(value.offset(EnumFacing.DOWN))
            for (enumFacing in randomHorizontals()) stack.push(value.offset(enumFacing))
            stack.push(value.offset(EnumFacing.UP))
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
        val update = linkData[facing, false]
        linkData[facing] = false
        if (update) updateLinkCode(true)
        updateBlockState(false)
    }

    /**
     * 该函数符则修改[lineCode]数据，字类重写时务必调用
     *
     * 注：函数内部会调用[canLinkFluid]，字类重写时无需再次调用
     */
    override fun linkFluid(facing: EnumFacing): Boolean {
        if (!canLinkFluid(facing)) return false
        val thatTE = world.getTileEntity(pos.offset(facing))
        if (thatTE is FTTileEntity) {
            linkData[facing] = true
            if (!thatTE.isLinked(facing.opposite)) {
                thatTE.lineCode = lineCode
                thatTE.forEachLine(facing.opposite) {
                    it.lineCode = lineCode
                    true
                }
            }
        } else linkData[facing, true] = true
        return true
    }

    /**
     * 该函数会检查[lineCode]，字类重写时务必调用并检查返回值
     */
    override fun canLinkFluid(facing: EnumFacing): Boolean {
        val thatTE = world.getTileEntity(pos.offset(facing))
        if (thatTE !is FTTileEntity) return true
        return if (thatTE.isLinked(facing.opposite)) true else thatTE.lineCode != lineCode
    }

    override fun isLinked(facing: EnumFacing) = linkData[facing, true]

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
    open fun getLinkedAmount() = EnumFacing.values().stream().filter { facing: EnumFacing ->
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
        return te!!.getCapability(FluidCapability.TRANSFER, facing.opposite)
    }

    /** 判断指定方向是否含有开口  */
    abstract fun hasAperture(facing: EnumFacing): Boolean

    /** 判断指定方向上能否通过流体  */
    open fun isOpen(facing: EnumFacing) = hasAperture(facing) && !hasPlug(facing)

    /** 判断指定方向上是否可以设置管塞  */
    open fun canSetPlug(facing: EnumFacing) = !(hasPlug(facing) || isLinked(facing))

    override fun receive(@Nonnull reader: NBTBase) {
        val nbt = reader as NBTTagCompound
        linkData.setValue(nbt.getByte("data").toInt())
        syncClient(nbt.getTag("sync"))
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
    protected abstract fun sync(): NBTBase

    /** 用于客户端同步数据  */
    @SideOnly(Side.CLIENT)
    protected abstract fun syncClient(reader: NBTBase)

    /** 向客户端发送服务端存储的信息并更新显示  */
    fun send() {
        if (world.isClient()) return
        if (players.size == world.playerEntities.size) return
        sendBlockMessageIfNotUpdate(players, 128) {
            val data = NBTTagCompound()
            data.setByte("data", linkData.getValue().toByte())
            data.setTag("sync", sync())
            data
        }
    }

    /**
     * 遍历当前线路（不包含当前方块）
     * @param from 上一个方向，为null表示当前方块为起点
     * @param function 操作，返回false表示终止当前线路的遍历
     */
    protected open fun forEachLine(from: EnumFacing?, function: (FTTileEntity) -> Boolean) {
        if (getLinkedAmount() < 3) {
            var lastFT: FTTileEntity = this
            var lastKey = from
            do {
                val next = lastFT.nextOnly(lastKey)
                if (next == null) {
                    lastFT = this
                    break
                }
                lastFT = next.first
                lastKey = next.second.opposite
                if (lastFT.getLinkedAmount() > 2 || !function(lastFT)) break
            } while (true)
            if (lastFT !== this) lastFT.forEachLine(lastKey, function)
        } else {
            for ((key, _, right) in linkData) {
                if (key === from) continue
                val value = getFluidDirect(key) ?: continue
                if (right && function(value as FTTileEntity))
                    value.forEachLine(key.opposite, function)
            }
        }
    }

    protected open fun nextOnly(from: EnumFacing?): Pair<FTTileEntity, EnumFacing>? {
        for ((key, _, right) in linkData) {
            if (key === from) continue
            val value = getFluidDirect(key) ?: continue
            if (right) return Pair(value as FTTileEntity, key)
        }
        return null
    }
    
    /**
     * 更新IBlockState
     * @param isRunOnClient 是否在客户端运行
     */
    fun updateBlockState(isRunOnClient: Boolean) {
        markDirty()
        if (world.isClient()) {
            if (!isRunOnClient) return
        } else {
            players.clear()
            send()
        }
        val oldState = world.getBlockState(pos)
        val newState = oldState.getActualState(world, pos)
        world.setBlockWithMark(pos, newState)
    }

    /** 方法内包含管道正常运行的方法，重写时务必使用`super.update()`调用 */
    override fun update() {
        if (world.isClient()) removeTickable()
        else {
            updateLinkCode(false)
            send()
        }
    }

    override fun markDirty() {
        super.markDirty()
        players.clear()
    }

    /**
     * 更新线路信息
     * @param force 是否强制更新
     */
    protected fun updateLinkCode(force: Boolean) {
        if (lineCode !== null && !force) return
        lineCode = pos
        forEachLine(null) {
            it.lineCode = lineCode
            true
        }
    }

}