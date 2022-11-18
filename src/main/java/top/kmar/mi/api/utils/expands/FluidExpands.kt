package top.kmar.mi.api.utils.expands

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundEvent
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
import top.kmar.mi.api.pipes.FluidPipeEntity

val FluidStack?.isEmpty: Boolean
    get() = this == null || this.amount == 0

val FluidStack?.amount: Int
    get() = this?.amount ?: 0

val FluidStack.fillSound: SoundEvent
    get() = fluid.getFillSound(this)

val FluidStack.emptySound: SoundEvent
    get() = fluid.getEmptySound(this)

/**
 * 尝试将指定流体插入到方块中
 * @param stack 要插入的流体
 * @param from 当前方块被操作的面
 * @param doEdit 是否修改方块数据
 * @return 实际插入的量
 */
fun TileEntity.insertFluid(stack: FluidStack, from: EnumFacing, doEdit: Boolean): Int {
    val cap = getCapability(FLUID_HANDLER_CAPABILITY, from) ?: return 0
    return cap.fill(stack, doEdit)
}

/**
 * 尝试从一个方块中取出指定量和种类的流体
 * @param stack 要取出的流体，其 `amount` 将被忽略，值为 `null` 标识忽略流体种类
 * @param amount 要取出的最大流体量
 * @param from 当前方块被操作的面
 * @param power 动力大小，具体见：[FluidPipeEntity.export]
 * @param doEdit 是否修改方块的数据
 * @return 实际取出的流体数据，未取出时返回 `null`
 */
fun TileEntity.exportFluid(
    stack: FluidStack?, amount: Int, from: EnumFacing, power: Int, doEdit: Boolean
): FluidStack? {
    if (stack == null) return exportFluid(amount, from, power, doEdit)
    if (this is FluidPipeEntity) {
        if (!matchFluid(stack)) return null
        return export(amount, from, power, doEdit)
    } else {
        val cap = getCapability(FLUID_HANDLER_CAPABILITY, from) ?: return null
        val target = if (stack.amount == amount) stack else stack.copy(amount)
        return cap.drain(target, doEdit)
    }
}

/**
 * 尝试从一个方块中取出指定量的流体
 * @param amount 要取出的最大流体量
 * @param from 当前方块被操作的面
 * @param power 动力大小，具体见：[FluidPipeEntity.export]
 * @param doEdit 是否修改方块的数据
 * @return 实际取出的流体数据，未取出时返回 `null`
 */
fun TileEntity.exportFluid(amount: Int, from: EnumFacing, power: Int, doEdit: Boolean): FluidStack? {
    if (this is FluidPipeEntity) {
        return export(amount, from, power, doEdit)
    } else {
        val cap = getCapability(FLUID_HANDLER_CAPABILITY, from) ?: return null
        return cap.drain(amount, doEdit)
    }
}