package top.kmar.mi.api.pipes

import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.DOWN
import net.minecraft.util.EnumFacing.UP
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.expands.computeIfAbsent
import top.kmar.mi.api.utils.expands.copy
import top.kmar.mi.api.utils.expands.randomHorizontals
import top.kmar.mi.api.utils.interfaces.BreakConsumer
import java.util.*
import kotlin.math.min

/**
 * 流体管道通用实现
 * @author EmptyDreams
 */
@AutoTileEntity("pipe")
open class FluidPipeEntity(val maxCapability: Int) : BaseTileEntity() {

    /** 存储指定方向上是否有开口 */
    @field:AutoSave
    protected val openData = IndexEnumMap(EnumFacing.values())
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
    val amount: Int
        get() = if (isEmpty) 0 else stack!!.amount
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
    fun isOpening(facing: EnumFacing) = openData[facing]

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return (capability === FLUID_HANDLER_CAPABILITY && facing != null && !isOpening(facing)) ||
                super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === FLUID_HANDLER_CAPABILITY) {
            if (facing == null) {
                eachOpening { it, _ -> return FLUID_HANDLER_CAPABILITY.cast(getFluidCap(it)) }
                return null
            }
            return FLUID_HANDLER_CAPABILITY.cast(getFluidCap(facing))
        }
        return super.getCapability(capability, facing)
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
            eachOpening { it, _ -> if (it !== from) add(it) }
        }
        if (nextFacings.size == 1) return chainInsert(stack, from, doEdit)
        var amount = stack.amount - inputFluid(stack, stack.amount, from, doEdit)
        if (amount != stack.amount) {
            eachOpening { it, _ ->
                if (it === from) return@eachOpening
                val entity = world.getTileEntity(pos.offset(it)) ?: return@eachOpening
                val cap = entity.getCapability(FLUID_HANDLER_CAPABILITY, it.opposite) ?: return@eachOpening
                amount += cap.fill(stack.copy(stack.amount - amount), doEdit)
                if (amount == stack.amount) return amount
            }
        }
        return amount
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
            eachOpening { it, stop ->
                if (it == from) return@eachOpening
                val entity = (world.getTileEntity(pos.offset(it)) as? FluidPipeEntity)
                    ?: return@eachOpening
                val cap = entity.getCapability(FLUID_HANDLER_CAPABILITY, it.opposite)
                    ?: return@eachOpening
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
        pipe.eachOpening { it, _ ->
            if (it === prevFacing) return@eachOpening
            val entity = world.getTileEntity(pipe.pos.offset(it)) ?: return@eachOpening
            val cap = entity.getCapability(FLUID_HANDLER_CAPABILITY, it.opposite) ?: return@eachOpening
            spare -= cap.fill(stack.copy(spare), doEdit)
            if (spare == 0) return stack.amount
        }
        return stack.amount - spare
    }

    /** 获取下一个管道，如果管道仅有一个出口或包含两个以上的出口则返回 `null` */
    private fun nextOnly(from: EnumFacing): Pair<EnumFacing, FluidPipeEntity?> {
        var result: FluidPipeEntity? = null
        var prev: EnumFacing = from
        eachOpening { it, _ ->
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

    /** 遍历有开口的方向 */
    private inline fun eachOpening(consumer: BreakConsumer<EnumFacing>) {
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
        if (isOpening(UP))
            consumer(UP) {}
    }

    private val capArray = Array<IFluidHandler?>(6) { null }

    fun getFluidCap(facing: EnumFacing): IFluidHandler? {
        if (!isOpening(facing)) return null
        return capArray.computeIfAbsent(facing.index) {
            object : IFluidHandler {

                private val properties = FluidTankProperties(stack, maxCapability, true, true)

                override fun getTankProperties(): Array<IFluidTankProperties> {
                    return Array(1) { properties }
                }

                override fun fill(resource: FluidStack?, doFill: Boolean): Int {
                    if (resource == null) return 0
                    return insert(resource, facing, doFill)
                }

                override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? {
                    TODO("Not yet implemented")
                }

                override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
                    TODO("Not yet implemented")
                }

            }
        }
    }

}