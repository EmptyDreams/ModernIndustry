package top.kmar.mi.api.pipes

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.expands.*
import top.kmar.mi.api.utils.interfaces.BreakConsumer
import java.util.*
import kotlin.math.min

/**
 * 流体管道通用实现
 * @author EmptyDreams
 */
abstract class FluidPipeEntity(val maxCapability: Int) : BaseTileEntity() {
    
    /** 当前管道中存储的流体 */
    @field:AutoSave
    protected var stack: FluidStack? = null
        get() = if (field != null && field!!.amount == 0) null else field

    val isEmpty: Boolean
        get() = stack == null
    val isNotEmpty: Boolean
        get() = stack != null
    val isFull: Boolean
        get() = stack?.amount == maxCapability
    val isNotFull: Boolean
        get() = !isFull
    /** 存储的流体量 */
    var amount: Int
        get() = stack.amount
        private set(value) {
            stack!!.amount = value
        }
    /**
     * 存储的流体
     * @throws NullPointerException 如果当前管道没有包含流体
     */
    val fluid: Fluid
        get() = stack!!.fluid
    /** 空闲空间 */
    val freeSpace: Int
        get() = maxCapability - amount
    /** 获取管道存储的流体（经过保护性拷贝） */
    val cpyStack: FluidStack?
        get() = stack?.copy()

    /** 判断指定方向是否有开口 */
    fun isOpening(facing: EnumFacing) = hasChannel(facing)

    /** 判断指定方向上是否含有通道 */
    abstract fun hasChannel(facing: EnumFacing): Boolean
    
    /** 判断指定方向上是否已经连接方块 */
    abstract fun isLink(facing: EnumFacing): Boolean
    
    /**
     * 将管道中的某个开口旋转到指定方向
     * @return 是否旋转成功
     */
    abstract fun linkFluidBlock(entity: TileEntity,  facing: EnumFacing): Boolean
    
    /** 切断管道与指定方向的连接 */
    abstract fun unlinkFluidBlock(facing: EnumFacing)
    
    /** 判断指定流体能否和管道内存储的流体合并 */
    fun matchFluid(stack: FluidStack?): Boolean {
        if (isEmpty || stack == null) return true
        return stack.isFluidEqual(this.stack)
    }
    
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === FLUID_HANDLER_CAPABILITY) return true
        return super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === FLUID_HANDLER_CAPABILITY) {
            if (facing == null) {
                eachInsertOpening { it, _ -> return FLUID_HANDLER_CAPABILITY.cast(getFluidCap(it)) }
                return null
            }
            return FLUID_HANDLER_CAPABILITY.cast(getFluidCap(facing))
        }
        return super.getCapability(capability, facing)
    }
    
    /**
     * 尝试取出一定量的流体
     * @param amount 要取出的量
     * @param from 取出的方向
     * @param power 动力值，水平方向吸一格取流体需要 1 动力，
     *   垂直方向吸取一格流体需要 10 动力（注：此处一格为 [maxCapability]），最大值为 640
     * @param doEdit 是否修改内部数据
     */
    open fun export(amount: Int, from: EnumFacing, power: Int, doEdit: Boolean): FluidStack? {
        if (power > maxPower) {
            // TODO 损坏管道的代码
            world.setBlockToAir(pos)
            return null
        }
        var result: FluidStack? = stack?.copy(0)
        var nextPower = power
        var count = amount
        // 从周边的方块中提取流体
        eachExportOpening { it, stop ->
            if (it === from) return@eachExportOpening
            if (it.consume > nextPower) return@eachExportOpening stop()
            val that = pos.offset(it)
            if (!world.isBlockLoaded(that)) return@eachExportOpening
            val entity = world.getTileEntity(that)
            nextPower -= it.consume
            // 如果方块不包含 TE 则从该方块开始在世界中搜索第一个可以提取液体的流体方块
            val drainStack = if (entity == null) {
                val tmpCount = count    // 拷贝一份避免闭包时影响性能
                val exportPoint = world.bfsSearch(that, true, 8192) {
                    val handler = FluidUtil.getFluidHandler(world, it, null) ?: return@bfsSearch null
                    val tmp = handler.drain(tmpCount, false) ?: return@bfsSearch false
                    tmp.amount != 0 && (result == null || tmp.isFluidEqual(result))
                }
                if (exportPoint == null) null
                else {
                    val handler = world.getFluidCapability(exportPoint, null)!!
                    if (result == null) handler.drain(count, doEdit)
                    else handler.drain(result!!.copy(count), doEdit)
                }
            } else entity.exportFluid(result, count, it.opposite, nextPower, doEdit)
            if (drainStack.isEmpty) {   // 如果没有提取到液体则恢复修改的数据
                nextPower += it.consume
                return@eachExportOpening
            }
            // 如果是从世界中提取的液体且需要进行实际修改，则播放音效
            if (entity == null && doEdit) world.playSound(
                null, that,
                drainStack!!.emptySound, SoundCategory.BLOCKS,
                0.4F, 0.4F
            )
            if (result == null) result = drainStack
            else result!!.amount += drainStack.amount
            count -= drainStack.amount
            if (count == 0) stop()
        }
        if (result == null) return null
        // 修改当前管道的数据
        if (count == 0) {   // 如果流体已经提取足够的量，则将一部分补充到当前管道中
            if (isNotFull) {
                count = min(result!!.amount, freeSpace)
                if (doEdit) {
                    if (isEmpty) stack = result!!.copy(count)
                    else this.amount += count
                    markDirty()
                }
                result!!.amount -= count
                if (result!!.amount == 0) return null
            }
        } else if (nextPower >= from.opposite.consume && isNotEmpty) {
            // 如果流体未提取足够的量且能够继续提取流体，则将当前管道中的一部分流体补充到结果中
            count = min(count, this.amount)
            if (doEdit) {
                this.amount -= count
                markDirty()
            }
            result!!.amount += count
        }
        return result
    }
    
    /**
     * 尝试插入指定量的流体
     * @param stack 要插入的流体
     * @param from 从管道的哪个方向进行的插入操作
     * @param doEdit 是否修改管道数据
     * @return 成功插入的流体量
     */
    open fun insert(stack: FluidStack, from: EnumFacing, doEdit: Boolean): Int {
        val nextFacings = LinkedList<EnumFacing>().apply {
            eachOpening { if (it !== from) add(it) }
        }
        if (nextFacings.size == 1) return chainInsert(stack, from, doEdit)
        var amount = stack.amount - inputFluid(stack, stack.amount, from, doEdit)
        if (amount != 0)
            amount -= recursionInsert(stack.copy(amount), from, doEdit)
        return stack.amount - amount
    }

    /** 向当前管道插入流体，当且仅当当前管道内的流体与插入的流体不同时才会向周围方块输送流体 */
    private fun inputFluid(stack: FluidStack, count: Int, from: EnumFacing, doEdit: Boolean): Int = when {
        isEmpty -> {
            val amount = min(count, freeSpace)
            if (doEdit) {
                this.stack = stack.copy(amount)
                markDirty()
            }
            amount
        }

        stack.isFluidEqual(this.stack) -> {
            val amount = min(count, freeSpace)
            if (doEdit && amount != 0) {
                this.stack!!.amount += amount
                markDirty()
            }
            amount
        }

        else -> {
            var move = count
            eachInsertOpening { it, stop ->
                if (it == from) return@eachInsertOpening
                val that = pos.offset(it)
                if (!world.isBlockLoaded(that)) return@eachInsertOpening
                val entity = world.getTileEntity(that) as? FluidPipeEntity
                val cap = entity?.getCapability(FLUID_HANDLER_CAPABILITY, it.opposite)
                    ?: return@eachInsertOpening
                val amount = cap.fill(stack.copy(move), doEdit)
                if (doEdit) this.stack!!.amount -= amount
                move -= amount
                if (move == 0) stop()
            }
            var result = 0
            if (move == 0) result = inputFluid(stack, count, from, doEdit)
            else if (doEdit) markDirty()
            result
        }
    }

    /** 使用递归的方式插入流体 */
    private fun recursionInsert(stack: FluidStack, from: EnumFacing, doEdit: Boolean): Int {
        var amount = stack.amount
        eachInsertOpening { it, _ ->
            if (it === from) return@eachInsertOpening
            val that = pos.offset(it)
            if (!world.isBlockLoaded(that)) return@eachInsertOpening
            val entity = world.getTileEntity(pos.offset(it))
            // 如果 TileEntity 不为空则尝试将流体放入其中
            if (entity != null) amount -= entity.insertFluid(stack.copy(amount), it.opposite, doEdit)
            else {  // 否则在世界中搜索第一个可以放置流体的方块
                if (amount < Fluid.BUCKET_VOLUME) return@eachInsertOpening
                val insertPoint = world.bfsSearch(that, false, 8192) {
                    val state = world.getBlockState(it)
                    val block = state.block
                    if (!block.isReplaceable(world, it)) return@bfsSearch null
                    val handler = FluidUtil.getFluidHandler(world, it, null) ?: return@bfsSearch true
                    handler.drain(Int.MAX_VALUE, false).amount == 0
                } ?: return@eachInsertOpening
                val handler = world.getFluidBlockHandler(stack.fluid, insertPoint)
                val dif = handler.fill(stack.copy(amount), doEdit)
                amount -= dif
                // 如果放置成功且需要修改实际数据则播放音效
                if (doEdit && dif != 0) world.playSound(
                    null, that,
                    stack.fillSound, SoundCategory.BLOCKS,
                    0.5F, 0.5F
                )
            }
            if (amount == 0) return stack.amount
        }
        return stack.amount - amount
    }
    
    /**
     * 使用循环代替递归
     *
     * 在遇到不断切换流体类型的情况时，该函数会退化为递归运算
     */
    private fun chainInsert(stack: FluidStack, from: EnumFacing, doEdit: Boolean): Int {
        var prevFacing = from
        var pipe = this
        var spare = stack.amount
        while (true) {
            spare -= pipe.inputFluid(stack, spare, prevFacing, doEdit)
            val pair = pipe.nextOnly(prevFacing)
            if (pair.second == null) break
            prevFacing = pair.first
            pipe = pair.second!!
        }
        if (spare == 0) return stack.amount
        spare -= pipe.recursionInsert(stack.copy(spare), prevFacing, doEdit)
        return stack.amount - spare
    }

    /** 获取下一个管道，如果管道仅有一个出口或包含两个以上的出口则返回 `null` */
    private fun nextOnly(from: EnumFacing): Pair<EnumFacing, FluidPipeEntity?> {
        var result: FluidPipeEntity? = null
        var prev: EnumFacing = from
        eachOpening {
            if (it !== from) {
                val next = pos.offset(it)
                if (!world.isBlockLoaded(next)) return Pair(it.opposite, null)
                val entity = world.getTileEntity(next) as? FluidPipeEntity
                if (result != null) return Pair(it.opposite, null)
                result = entity
                prev = it.opposite
            }
        }
        return Pair(prev, result)
    }

    private inline fun eachOpening(consumer: (EnumFacing) -> Unit) {
        values().asSequence()
            .filter { isOpening(it) }
            .forEach(consumer)
    }
    
    private inline fun eachExportOpening(consumer: BreakConsumer<EnumFacing>) {
        var flag = false
        val stop = { flag = true }
        if (isOpening(UP)) {
            consumer(UP, stop)
            if (flag) return
        }
        randomHorizontals().forEach {
            if (isOpening(it)) {
                consumer(it, stop)
                if (flag) return
            }
        }
        if (isOpening(DOWN)) consumer(DOWN) {}
    }
    
    /** 遍历有开口的方向 */
    private inline fun eachInsertOpening(consumer: BreakConsumer<EnumFacing>) {
        var flag = false
        val stop = { flag = true }
        if (isOpening(DOWN)) {
            consumer(DOWN, stop)
            if (flag) return
        }
        randomHorizontals().forEach {
            if (isOpening(it)) {
                consumer(it, stop)
                if (flag) return
            }
        }
        if (isOpening(UP)) consumer(UP) {}
    }

    private val capArray = Array<IFluidHandler?>(6) { null }

    protected fun clearCapCache() {
        capArray.fill(null)
    }
    
    fun getFluidCap(facing: EnumFacing): IFluidHandler? {
        if (!isOpening(facing)) return null
        return capArray.computeIfAbsent(facing.index) {
            object : IFluidHandler {

                private val properties = FluidTankProperties(stack, maxCapability, true, false)

                override fun getTankProperties(): Array<IFluidTankProperties> {
                    return Array(1) { properties }
                }

                override fun fill(resource: FluidStack?, doFill: Boolean): Int {
                    if (resource == null) return 0
                    return insert(resource, facing, doFill)
                }

                override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? = null

                override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? = null

            }
        }
    }

    companion object {
        
        /** 传输流体需要消耗的动力 */
        private val EnumFacing.consume: Int
            get() = when (this) {
                DOWN -> 10
                UP -> 0
                else -> 1
            }
        
        const val maxPower = 40
        
        @JvmStatic
        protected fun FluidPipeEntity.tryLink(
            data: IndexEnumMap<EnumFacing>, that: TileEntity, facing: EnumFacing
        ): Boolean {
            if (that !is FluidPipeEntity) return true
            val opposite = facing.opposite
            data[facing] = true
            if (!that.isLink(opposite)) {
                if (!that.linkFluidBlock(this, opposite)) {
                    data[facing] = false
                    return false
                }
            }
            return true
        }
        
    }
    
}