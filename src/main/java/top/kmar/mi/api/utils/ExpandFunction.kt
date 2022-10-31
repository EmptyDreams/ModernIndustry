@file:Suppress("NOTHING_TO_INLINE")

package top.kmar.mi.api.utils

import io.netty.buffer.ByteBuf
import net.minecraft.block.BlockLiquid
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.araw.AutoDataRW
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.utils.container.PairIntInt
import top.kmar.mi.api.utils.data.math.Point3D
import top.kmar.mi.api.utils.data.math.Range3D
import top.kmar.mi.api.utils.iterators.ArrayFlipIterator
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.sqrt

/** 深度复制列表 */
fun List<ItemStack>.deepClone(): MutableList<ItemStack> {
    val list = ArrayList<ItemStack>(size)
    forEach { list.add(it.copy()) }
    return list
}

inline fun <T> Array<T>.stream(): Stream<T> = Arrays.stream(this)

/** 判断两个stack能否合并 */
fun ItemStack.match(stack: ItemStack) =
    isEmpty || stack.isEmpty || (
                    (!hasSubtypes || metadata == stack.metadata) &&
                    isItemEqual(stack) &&
                    ItemStack.areItemStackTagsEqual(this, stack)
            )

/** 移除所有空格 */
fun String.removeAllSpace(): String {
    return replace(Regex("""\s"""), "")
}

/** 比较两个列表 */
fun <T : Comparable<T>> Collection<T>.compareTo(other: Collection<T>): Int {
    if (size != other.size) return size.compareTo(other.size)
    val itor0 = iterator()
    val itor1 = other.iterator()
    while (itor0.hasNext()) {
        val res = itor0.next().compareTo(itor1.next())
        if (res != 0) return res
    }
    return 0
}

/**
 * 计算一个字符串的开头有多少个空格，一个制表符当作4个空格
 * @return `first` - 第一个非空白符字符的下标，`second` - 空白符长度
 */
fun String.countStartSpace(): PairIntInt {
    var count = 0
    var index = 0
    for (c in this) {
        when (c) {
            ' ' -> ++count
            '\t' -> count += 4
            else -> break
        }
        ++index
    }
    return PairIntInt(index, count)
}

@field:SideOnly(Side.CLIENT)
private var oldGui: GuiScreen? = null
@field:SideOnly(Side.CLIENT)
private var isOpenClientGui = false

/** 打开一个客户端GUI */
@Suppress("ControlFlowWithEmptyBody")
fun EntityPlayer.openClientGui(key: ResourceLocation, x: Int, y: Int, z: Int) {
    if (world.isServer()) return
    val id = GuiLoader.getID(key)
    if (id > 0) throw IllegalArgumentException("指定GUI[$key]不是客户端GUI")
    // 清除键盘和鼠标输入
    KeyBinding.unPressAllKeys()
    while (Mouse.next()) { }
    while (Keyboard.next()) { }
    // 打开GUI
    val mc = Minecraft.getMinecraft()
    if (isOpenClientGui()) mc.currentScreen!!.onGuiClosed()
    else oldGui = mc.currentScreen
    isOpenClientGui = true
    val newGui = GuiLoader.getClientGuiElement(id, this, world, x, y, z)
    val scaled = ScaledResolution(mc)
    val i = scaled.scaledWidth
    val j = scaled.scaledHeight
    newGui.setWorldAndResolution(mc, i, j)
    mc.currentScreen = newGui
    mc.setIngameNotInFocus()
}

/**
 * 关闭客户端GUI
 * @return 是否成功关闭客户端GUI
 */
fun EntityPlayer.closeClientGui(): Boolean {
    if (world.isServer()) return false
    if (isOpenClientGui) {
        isOpenClientGui = false
        val mc = Minecraft.getMinecraft()
        mc.currentScreen!!.onGuiClosed()
        mc.currentScreen = oldGui
        return true
    }
    return false
}

/** 判断指定玩家是否打开了一个客户端GUI */
@Suppress("UnusedReceiverParameter")
fun EntityPlayer.isOpenClientGui(): Boolean = isOpenClientGui

fun EntityPlayer.openGui(key: ResourceLocation, x: Int, y: Int, z: Int) {
    openGui(ModernIndustry.instance, GuiLoader.getID(key), world, x, y, z)
}

/** 判断是否为偶数 */
inline fun Int.isEven() = this and 1 == 0

/** 如果满足指定条件则进行减法，否则返回其本身 */
inline fun Int.minusIf(num: Int, check: Int.() -> Boolean) = minusIf(num, check(this))

/** 如果满足指定条件则进行减法，否则返回其本身 */
inline fun Int.minusIf(num: Int, bool: Boolean) = if (bool) this - num else this

/** 如果满足指定条件则交换两个数值的位置 */
inline fun Int.swapIf(other: Int, bool: Boolean): PairIntInt {
    return if (bool) PairIntInt(other, this) else PairIntInt(this, other)
}

/** 除2，向下取整 */
inline fun Int.floorDiv2() = this shr 1

/** 向上取整的整除2 */
inline fun Int.ceilDiv2(): Int {
    val result = this shr 1
    return if (this and 1 == 0) result else result + 1
}

/** 向上取整的整除 */
inline infix fun Int.ceilDiv(other: Int): Int {
    val result = this / other
    return if (this % other == 0) result else result + 1
}

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

/** 如果表达式为真则倒序遍历，否则正序遍历 */
infix fun <T> Array<T>.flipIf(isFlip: Boolean) =
    if (isFlip) flip() else this.asIterable()

/** 指定起始遍历位置的倒序迭代器 */
infix fun <T> Array<T>.flip(startIndex: Int) = Iterable { ArrayFlipIterator(this, startIndex) }

/** 获取倒序遍历的迭代器 */
fun <T> Array<T>.flip() = Iterable { ArrayFlipIterator(this, this.size - 1) }

/** 拷贝Stack并将拷贝后的Stack的count修改为指定值 */
fun ItemStack.copy(count: Int): ItemStack {
    val result = copy()
    result.count = count
    return result
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
 * 从[NBTTagCompound]中读取数据到类中
 *
 * @param obj 要处理的类的对象
 * @param key 数据总体在[NBTTagCompound]中的`key`
 *
 * @receiver [NBTTagCompound]
 */
fun NBTTagCompound.readObject(obj: Any, key: String = ".") {
    AutoDataRW.read2ObjAll(getCompoundTag(key), obj)
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
    val nbt = AutoDataRW.writeAll(obj)
    setTag(key, nbt)
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