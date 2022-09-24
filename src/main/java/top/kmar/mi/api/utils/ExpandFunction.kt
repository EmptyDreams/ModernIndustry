package top.kmar.mi.api.utils

import io.netty.buffer.ByteBuf
import net.minecraft.block.BlockLiquid
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.ITextureObject
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other
import top.kmar.mi.api.araw.AutoDataRW
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.utils.data.math.Point3D
import top.kmar.mi.api.utils.data.math.Range3D
import top.kmar.mi.api.utils.iterators.ArrayFlipIterator
import java.awt.Color
import java.lang.Integer.max
import java.nio.charset.StandardCharsets
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/** 比较字符串是否相等（忽略大小写） */
fun String.equalsIgnoreCase(that: String): Boolean {
    if (length != that.length) return false
    for (i in indices) {
        if (this[i].lowercaseChar() != that[i].lowercaseChar())
            return false
    }
    return true
}

/** 在客户端执行一段代码 */
inline fun <T> T.applyClient(block: T.() -> Unit): T {
    if (WorldUtil.isClient()) block()
    return this
}

/**
 * 将颜色转为Int表达
 *
 * 格式：(Alpha Red Green Blue) 每个值占8bit
 */
fun Color.toInt(): Int {
    val red = this.red shl 16
    val green = this.green shl 8
    val alpha = this.alpha shl 24
    return red or green or blue or alpha
}

/** 如果表达式为真则倒序遍历，否则正序遍历 */
infix fun <T> Array<T>.flipIf(isFlip: Boolean) =
    if (isFlip) flip() else this.asIterable()

/** 指定起始遍历位置的倒序迭代器 */
infix fun <T> Array<T>.flip(startIndex: Int) = Iterable { ArrayFlipIterator(this, startIndex) }

/** 获取倒序遍历的迭代器 */
fun <T> Array<T>.flip() = Iterable { ArrayFlipIterator(this, this.size - 1) }

/**
 * 尝试将输入的[ItemStack]放入[Slot]中
 *
 * 函数不会修改传入的stack
 *
 * @return 成功放入的物品数量
 */
fun Slot.mergeStack(stack: ItemStack): Int {
    val slotStack = this.stack
    if (!slotStack.checkMerge(stack)) return 0
    val maxCount = min(slotStack.maxStackSize, slotStackLimit) - slotStack.count
    val extract = max(min(maxCount, stack.count), 0)
    if (extract != 0) putStack(stack.copy(extract))
    return extract
}

/** 拷贝Stack并将拷贝后的Stack的count修改为指定值 */
fun ItemStack.copy(count: Int): ItemStack {
    val result = copy()
    result.count = count
    return result
}

/** 判断当前Stack能否和指定的Stack合并 */
fun ItemStack.checkMerge(that: ItemStack): Boolean {
    if (isEmpty || that.isEmpty) return true
    if (!isItemEqualIgnoreDurability(that)) return false
    if (hasSubtypes && metadata != that.metadata) return false
    return ItemStack.areItemStackTagsEqual(this, that)
}

/**
 * 装载材质
 * @receiver [ITextureObject]
 */
fun ITextureObject.bindTexture(): ITextureObject {
    GlStateManager.bindTexture(glTextureId)
    return this
}

/** 检查指定位置是否可以放置指定流体方块 */
fun World.pushFluid(pos: BlockPos, data: FluidData, simulate: Boolean): Int {
    if (data.isEmpty) return 0
    val fluid = data.fluid!!
    if (!data.fluid!!.canBePlacedInWorld()) return 0
    val stack = data.toStack()
    val fluidSource = FluidUtil.getFluidHandler(FluidUtil.getFilledBucket(stack)) ?: return 0
    if (fluidSource.drain(stack, false) == null) return 0
    val state = getBlockState(pos)
    if (!(state.material.isSolid || state.block.isReplaceable(this, pos))) return 0
    if (provider.doesWaterVaporize() && fluid.doesVaporize(stack)) {
        val result = fluidSource.drain(stack, !simulate) ?: return 0
        result.fluid.vaporize(null, this, pos, result)
        return result.amount
    } else {
        val handler = getFluidBlockHandler(fluid, pos)
        val result = FluidUtil.tryFluidTransfer(handler, fluidSource, stack, !simulate) ?: return 0
        playSound(null, pos, fluid.getFillSound(stack), SoundCategory.BLOCKS, 1F, 1F)
        return result.amount
    }
}

fun World.getFluidBlockHandler(fluid: Fluid, pos: BlockPos): IFluidHandler =
    when (val block = fluid.block) {
        is IFluidBlock -> FluidBlockWrapper(block, this, pos)
        is BlockLiquid -> BlockLiquidWrapper(block, this, pos)
        else -> BlockWrapper(block, this, pos)
    }

/**
 * 创建一个新的ItemStack
 * @receiver [Item]
 */
fun Item.newStack(amount: Int = 1) = ItemStack(this, amount)

/**
 * 读取坐标
 * @receiver [NBTTagCompound]
 */
fun NBTTagCompound.getBlockPos(key: String): BlockPos {
    val value = getIntArray(key)
    return BlockPos(value[0], value[1], value[2])
}

/**
 * 从[NBTTagCompound]中读取数据到类中
 *
 * @param obj 要处理的类的对象
 * @param key 数据总体在[NBTTagCompound]中的`key`
 *
 * @receiver [NBTTagCompound]
 */
fun NBTTagCompound.readObject(obj: Any, key: String = ".") {
    val operator = ByteDataOperator()
    operator.writeFromNBT(this, key)
    AutoDataRW.read2ObjAll(operator, obj)
}

/**
 * 将类中的所有被`AutoSave`注释的属性写入到[NBTTagCompound]中
 *
 * @param obj 要处理的类的对象
 * @param key 数据总体在[NBTTagCompound]中的`key`
 *
 * @receiver [NBTTagCompound]
 */
fun NBTTagCompound.writeObject(obj: Any, key: String = ".") {
    val operator = ByteDataOperator()
    AutoDataRW.write2LocalAll(operator, obj)
    if (operator.size() != 0) operator.readToNBT(this, key)
}

/**
 * 读取字符串
 * @receiver [ByteBuf]
 */
fun ByteBuf.readString(): String {
    val size: Int = readInt()
    val result = ByteArray(size)
    for (i in 0 until size) {
        result[i] = readByte()
    }
    return String(result)
}

/**
 * 写入字符串
 * @receiver [ByteBuf]
 */
fun ByteBuf.writeString(data: String) {
    val bytes = data.toByteArray(StandardCharsets.UTF_8)
    writeInt(bytes.size)
    for (b in bytes) {
        writeByte(b.toInt())
    }
}

/**
 * 遍历当前世界中在指定范围内得所有玩家
 * @receiver [World]
 */
fun World.forEachPlayersAround(range: Range3D, consumer: (EntityPlayer) -> Unit) =
    forEachPlayers { if (range.isIn(Point3D(it))) consumer(it) }

/**
 * 遍历当前世界中的所有玩家
 * @receiver [World]
 */
fun World.forEachPlayers(consumer: (EntityPlayer) -> Unit) = playerEntities.forEach(consumer)

/**
 * @see WorldUtil.removeTickable
 * @receiver [TileEntity]
 */
fun TileEntity.removeTickable() = WorldUtil.removeTickable(this)

/**
 * @see WorldUtil.addTickable
 * @receiver [TileEntity]
 */
fun TileEntity.addTickable() = WorldUtil.addTickable(this)

/**
 * 判断当前世界是否为客户端
 * @receiver [World]
 */
fun World.isClient() = isRemote

/**
 * 判断当前世界是否为服务端
 * @receiver [World]
 */
fun World.isServer() = !isRemote

/**
 * 判断指定坐标在当前坐标的哪一个相邻方向
 * @receiver [BlockPos]
 * @throws IllegalArgumentException 如果传入的参数和当前坐标不相邻
 */
fun BlockPos.whatFacing(pos: BlockPos): EnumFacing {
    for (facing in EnumFacing.values()) {
        if (offset(facing) == pos) return facing
    }
    throw IllegalArgumentException("now[" + this + "]和other" + other + "不相邻！")
}

/**
 * 当玩家放置方块时判断方块的朝向
 * @param pos 放置的方块的坐标
 * @receiver [EntityPlayer]
 */
fun EntityPlayer.getPlacingDirection(pos: BlockPos): EnumFacing {
    val x: Double = posX - pos.x
    val y: Double = posZ - pos.z
    if (pos.y < posY || posY + height < pos.y) {
        return if (sqrt(x * x + y * y) <= 1.8) {
            //如果玩家和方块间的水平距离小于1.8
            if (pos.y < posY) EnumFacing.DOWN else EnumFacing.UP
        } else {
            //如果玩家和方块间的水平距离大于1.8
            horizontalFacing
        }
    } else if (pos.y == posY.toInt() || pos.y == posY.toInt() + 1) {
        //如果玩家和方块在同一水平面上
        if (sqrt(x * x + y * y) > 1.8 || abs(rotationPitch) < 40) {
            return horizontalFacing
        }
        if (rotationPitch < -8.3) return EnumFacing.UP
        if (rotationPitch > 8.3) return EnumFacing.DOWN
    }
    //如果玩家和方块大致处于同一平面
    return horizontalFacing
}