/** 与世界相关的操作的封装 */
package top.kmar.mi.api.utils.expands

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import net.minecraft.block.BlockLiquid
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
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
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.utils.TickHelper
import top.kmar.mi.api.utils.data.math.Point3D
import top.kmar.mi.api.utils.data.math.Range3D
import java.util.*
import java.util.function.BiConsumer

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

/** 是否为客户端 */
fun isClient() = FMLCommonHandler.instance().effectiveSide.isClient

/** 是否为服务端 */
fun isServer() = FMLCommonHandler.instance().effectiveSide.isServer

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
 * 将TE从tick任务列表中移除
 * @receiver [TileEntity]
 */
fun TileEntity.removeTickable() {
    if (world.isClient()) {
        CLIENT_REMOVES.computeIfAbsent(world) { LinkedList() }.add(this)
        TickHelper.addClientTask { clearClientTickableList() }
    } else {
        SERVER_REMOVES.computeIfAbsent(world) { LinkedList() }.add(this)
        TickHelper.addServerTask { clearServerTickableList() }
    }
}

/**
 * 将TE添加到世界的tick任务列表中
 * @receiver [TileEntity]
 */
fun TileEntity.addTickable()  {
    require(this is ITickable) { "输入的参数应当实现ITickable接口：$javaClass" }
    if (world.isRemote) {
        CLIENT_ADDS.computeIfAbsent(world) { LinkedList() }.add(this)
        TickHelper.addClientTask { clearClientTickableList() }
    } else {
        SERVER_ADDS.computeIfAbsent(world) { LinkedList() }.add(this)
        TickHelper.addServerTask { clearServerTickableList() }
    }
}

/** 客户端世界对象 */
@get:SideOnly(Side.CLIENT)
val clientWorld: WorldClient
    get() = Minecraft.getMinecraft().world

/** 客户端玩家对象 */
@get:SideOnly(Side.CLIENT)
val clientPlayer: EntityPlayerSP
    get() = Minecraft.getMinecraft().player

/** 根据dimension获取世界对象，客户端直接返回当前世界对象 */
fun getWorld(dimension: Int): World {
    return if (isServer()) {
        FMLCommonHandler.instance().minecraftServerInstance.getWorld(dimension)
    } else {
        Minecraft.getMinecraft().world
    }
}

/** 设置[IBlockState]并调用[World.markBlockRangeForRenderUpdate] */
fun World.setBlockWithMark(pos: BlockPos, state: IBlockState) {
    setBlockState(pos, state, 11)
    markBlockRangeForRenderUpdate(pos, pos)
}

/**
 * 遍历指定方块周围的所有TE，不包含TE的不会进行遍历
 * @param center 中心方块
 * @param consumer 要运行的代码，其中[TileEntity]是遍历到的方块的TE，[EnumFacing]指被遍历的方块相对于中心方块的方向
 */
fun World.forEachAroundTileEntity(center: BlockPos, consumer: BiConsumer<TileEntity, EnumFacing>) {
    for (facing in EnumFacing.values()) {
        val te = getTileEntity(center.offset(facing))
        if (te != null) consumer.accept(te, facing)
    }
}

/**
 * 判断指定坐标在当前坐标的哪一个相邻方向
 * @receiver [BlockPos]
 * @throws IllegalArgumentException 如果传入的参数和当前坐标不相邻
 */
fun BlockPos.whatFacing(pos: BlockPos): EnumFacing {
    for (facing in EnumFacing.values()) {
        if (offset(facing) == pos) return facing
    }
    throw IllegalArgumentException("now[$this]和other[$pos]不相邻！")
}

//---------------------私有内容---------------------//

/** 客户端移除列表  */
private val CLIENT_REMOVES: MutableMap<World, MutableList<TileEntity>> = Object2ObjectArrayMap(3)
/** 服务端移除列表  */
private val SERVER_REMOVES: MutableMap<World, MutableList<TileEntity>> = Object2ObjectArrayMap(3)

/** 客户端添加列表  */
private val CLIENT_ADDS: MutableMap<World, MutableList<TileEntity>> = Object2ObjectArrayMap(3)
/** 服务端添加列表  */
private val SERVER_ADDS: MutableMap<World, MutableList<TileEntity>> = Object2ObjectArrayMap(3)

private fun clearServerTickableList(): Boolean {
    SERVER_REMOVES.forEach { (key, value) ->
        key.tickableTileEntities.removeAll(value)
    }
    SERVER_REMOVES.clear()
    SERVER_ADDS.forEach { (key, value) ->
        value.stream()
            .distinct()
            .filter { !key.tickableTileEntities.contains(it) }
            .forEach { key.tickableTileEntities.add(it) }
    }
    SERVER_ADDS.clear()
    return true
}

@SideOnly(Side.CLIENT)
private fun clearClientTickableList(): Boolean {
    CLIENT_REMOVES.forEach { (key, value) ->
        key.tickableTileEntities.removeAll(value)
    }
    CLIENT_REMOVES.clear()
    CLIENT_ADDS.forEach { (key, value) ->
        value.stream()
            .distinct()
            .filter { !key.tickableTileEntities.contains(it) }
            .forEach { key.tickableTileEntities.add(it) }
    }
    CLIENT_ADDS.clear()
    return true
}