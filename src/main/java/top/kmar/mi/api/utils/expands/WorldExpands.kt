/** 与世界相关的操作的封装 */
package top.kmar.mi.api.utils.expands

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import net.minecraft.block.BlockLiquid
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.utils.data.math.Point3D
import top.kmar.mi.api.utils.data.math.Range3D
import java.util.*
import java.util.function.BiConsumer

/** 获取一个方块的的 TE，当区块未加载时不会加载区块而是返回 `null` */
fun World.getBlockEntity(pos: BlockPos): TileEntity? =
    if (isBlockLoaded(pos)) getTileEntity(pos) else null

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

/** 获取指定方块的 [IFluidHandler] */
fun World.getFluidCapability(pos: BlockPos, side: EnumFacing?): IFluidHandler? {
    return FluidUtil.getFluidHandler(this, pos, side)
}

/** 向指定位置放置流体方块 */
fun World.setFluid(pos: BlockPos, data: FluidStack?, doEdit: Boolean): Int {
    if (data.isEmpty) return 0
    val fluid = data!!.fluid
    if (!data.fluid!!.canBePlacedInWorld()) return 0
    val fluidSource = FluidUtil.getFluidHandler(FluidUtil.getFilledBucket(data)) ?: return 0
    if (fluidSource.drain(data, false) == null) return 0
    val state = getBlockState(pos)
    if (!(state.material.isSolid || state.block.isReplaceable(this, pos))) return 0
    if (provider.doesWaterVaporize() && fluid.doesVaporize(data)) {
        val result = fluidSource.drain(data, doEdit) ?: return 0
        result.fluid.vaporize(null, this, pos, result)
        return result.amount
    } else {
        val handler = getFluidBlockHandler(fluid, pos)
        val result = FluidUtil.tryFluidTransfer(handler, fluidSource, data, doEdit) ?: return 0
        if (doEdit)
            playSound(null, pos, data.emptySound, SoundCategory.BLOCKS, 1F, 1F)
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
    //markBlockRangeForRenderUpdate(pos, pos)
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

/**
 * 以广度优先搜索在世界中搜索第一个满足条件的方块
 * @param startPos 起始的搜索位点
 * @param priorityUp 是否优先向上搜索，为 `false` 时优先向下搜索
 * @param deep 扫描深度限制
 * @param check 检查指定方块是否满足要求，返回 `null` 表示该方块不再作为新的节点继续拓展
 * @return 搜索到的方块坐标，未搜索到则返回 `null`
 */
fun World.bfsSearch(
    startPos: BlockPos, priorityUp: Boolean, deep: Int,
    check: (BlockPos) -> Boolean?
): BlockPos? {
    val map = Int2ObjectRBTreeMap<LinkedList<BlockPos>> { left, right ->
        if (priorityUp) right.compareTo(left)
        else left.compareTo(right)
    }.apply {
        put(startPos.y, LinkedList<BlockPos>().apply { addLast(startPos) })
    }
    val record = ObjectRBTreeSet<BlockPos>().apply { add(startPos) }
    do {
        val list = map.get(map.firstIntKey())
        val pos = list.removeFirst()
        val result = check(pos)
        if (result != null) {
            if (result) return pos
            // 添加下一次要扫描的坐标
            EnumFacing.values().forEach {
                val next = pos.offset(it)
                if (isBlockLoaded(next) && next !in record) {
                    val target: LinkedList<BlockPos>
                    if (map.containsKey(next.y)) {
                        target = map.get(next.y)
                    } else {
                        target = LinkedList()
                        map.put(next.y, target)
                    }
                    target.addLast(next)
                    record.add(next)
                }
            }
        }
        if (list.isEmpty()) map.remove(pos.y)
    } while (record.size < deep && map.isNotEmpty())
    return null
}

//---------------------私有内容---------------------//

/** 移除列表 */
private val tickableRemoves = Object2ObjectRBTreeMap<World, MutableSet<TileEntity>> { left, right ->
    left.provider.dimension.compareTo(right.provider.dimension)
}
/** 添加列表 */
private val tickableAdds = Object2ObjectRBTreeMap<World, MutableSet<TileEntity>> { left, right ->
    left.provider.dimension.compareTo(right.provider.dimension)
}

/** 从世界中移除一个 Tick 任务 */
fun World.removeTickable(entity: TileEntity) {
    tickableRemoves.computeIfAbsent(this) {
        ObjectRBTreeSet { left, right -> left.pos.compareTo(right.pos) }
    }.add(entity)
    tickableAdds[this]?.remove(entity)
}

/**
 * 向世界添加一个 Tick 任务
 *
 * **注意：该方法不能在世界中已包含当前任务的情况下调用，否则会导致世界中重复包含指定任务**
 */
fun World.addTickable(entity: TileEntity) {
    tickableAdds.computeIfAbsent(this) {
        ObjectRBTreeSet { left, right -> left.pos.compareTo(right.pos) }
    }.add(entity)
    tickableRemoves[this]?.remove(entity)
}

/** 执行 tick 任务列表更新任务 */
@Suppress("UnusedReceiverParameter")
fun World?.callTickableListUpdateTask() {
    if (isClient() && Minecraft.getMinecraft().world == null) {
        tickableRemoves.clear()
        tickableAdds.clear()
        return
    }
    clearTickableRemoves()
    clearTickableAdds()
}

/** 清空移除任务列表 */
private fun clearTickableRemoves() {
    tickableRemoves.forEach { (world, list) ->
        val itor = world.tickableTileEntities.listIterator(world.tickableTileEntities.size)
        while (itor.hasPrevious()) {
            val value = itor.previous()
            if (list.remove(value)) itor.remove()
        }
        if (list.isNotEmpty()) throw AssertionError("列表没有正确清零：$list")
    }
    tickableRemoves.clear()
}

/** 清空添加任务列表 */
private fun clearTickableAdds() {
    tickableAdds.forEach { (world, list) ->
        world.tickableTileEntities.addAll(list)
    }
    tickableAdds.clear()
}