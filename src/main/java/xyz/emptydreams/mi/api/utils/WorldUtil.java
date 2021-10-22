package xyz.emptydreams.mi.api.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;
import xyz.emptydreams.mi.content.blocks.base.EleTransferBlock;
import xyz.emptydreams.mi.api.fluid.data.FluidData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 关于世界的操作
 * @author EmptyDreams
 */
public final class WorldUtil {
	
	/** 获取本地玩家对象 */
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getPlayerAtClient() {
		return Minecraft.getMinecraft().player;
	}
	
	/**
	 * 获取所有世界中指定UUID的玩家的对象
	 * @return 若玩家不存在则返回null
	 */
	public static EntityPlayer getPlayer(UUID uuid) {
		if (isClient()) return getPlayerAtClient();
		return getPlayerAtService(uuid);
	}
	
	/**
	 * 获取所有世界中指定名称的玩家的对象
	 * @return 若玩家不存在则返回null
	 */
	public static EntityPlayer getPlayerAtService(String name) {
		for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
			EntityPlayer player = world.getPlayerEntityByName(name);
			if (player != null) return player;
		}
		return null;
	}
	
	/**
	 * 获取所有世界中指定UUID的玩家的对象
	 * @return 若玩家不存在则返回null
	 */
	public static EntityPlayer getPlayerAtService(UUID uuid) {
		for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
			EntityPlayer player = world.getPlayerEntityByUUID(uuid);
			if (player != null) return player;
		}
		return null;
	}
	
	/**
	 * 获取指定区域内的玩家列表
	 * @param world 世界对象
	 * @param range 范围
	 */
	@Nonnull
	public static List<EntityPlayer> getPlayersInRange(World world, Range3D range) {
		List<EntityPlayer> result = new LinkedList<>();
		forEachPlayers(world, range, result::add);
		return result;
	}
	
	/**
	 * 遍历指定世界中在指定范围内的所有玩家
	 * @param world 指定世界
	 * @param range 范围
	 * @param consumer 操作
	 */
	public static void forEachPlayers(World world, Range3D range, Consumer<EntityPlayer> consumer) {
		world.playerEntities.forEach(player -> {
			if (range.isIn(new Point3D(player))) consumer.accept(player);
		});
	}
	
	/**
	 * 遍历所有世界中的所有玩家
	 * @param consumer 操作
	 */
	public static void forEachPlayers(Consumer<EntityPlayer> consumer) {
		if (isServer()) {
			for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
				world.playerEntities.forEach(consumer);
			}
		} else {
			getClientWorld().playerEntities.forEach(consumer);
		}
	}
	
	/**
	 * 遍历指定世界中的所有玩家
	 * @param world 指定世界
	 * @param consumer 操作
	 */
	public static void forEachPlayers(World world, Consumer<EntityPlayer> consumer) {
		world.playerEntities.forEach(consumer);
	}
	
	/**
	 * 根据dimension获取世界对象
	 * @return 若客户端当前世界的dimension不为输入值则抛出异常
	 * @see #getClientWorld()
	 */
	@Nonnull
	public static World getWorld(int dimension) {
		if (WorldUtil.isServer()) {
			return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
		} else {
			int dim = Minecraft.getMinecraft().world.provider.getDimension();
			if (dim != dimension) {
				throw new IllegalArgumentException("世界对象[" + dim +"]与目标世界对象[" + dimension + "]不同");
			}
			return Minecraft.getMinecraft().world;
		}
	}
	
	/**
	 * 获取客户端世界对象
	 */
	@SideOnly(Side.CLIENT)
	@Nonnull
	public static World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}
	
	/**
	 * 将指定任务从每Tick的循环中移除
	 * @param tickable 要移除的任务
	 */
	public static void removeTickable(TileEntity tickable) {
		StringUtil.checkNull(tickable, "tickable");
		if (tickable.getWorld().isRemote) {
			CLIENT_REMOVES.computeIfAbsent(tickable.getWorld(), key -> new LinkedList<>()).add(tickable);
			TickHelper.addClientTask(WorldUtil::clearClientTickableList);
		} else {
			SERVER_REMOVES.computeIfAbsent(tickable.getWorld(), key -> new LinkedList<>()).add(tickable);
			TickHelper.addServerTask(WorldUtil::clearServerTickableList);
		}
	}
	
	/**
	 * 将指定任务添加到每Tick的循环中
	 * @param tickable 要添加的任务
	 */
	public static void addTickable(TileEntity tickable) {
		StringUtil.checkNull(tickable, "tickable");
		if (!(tickable instanceof ITickable))
			throw new IllegalArgumentException("输入的参数应当实现ITickable接口：" + tickable.getClass());
		if (tickable.getWorld().isRemote) {
			CLIENT_ADDS.computeIfAbsent(tickable.getWorld(), key -> new LinkedList<>()).add(tickable);
			TickHelper.addClientTask(WorldUtil::clearClientTickableList);
		} else {
			SERVER_ADDS.computeIfAbsent(tickable.getWorld(), key -> new LinkedList<>()).add(tickable);
			TickHelper.addServerTask(WorldUtil::clearServerTickableList);
		}
	}
	
	/**
	 * <p>将流体释放到世界中。
	 * <p>与{@link #putFluid2World(World, BlockPos, Fluid)}不同的是，该方法会将流体以垂直的形式放置到世界中
	 * @param world 世界对象
	 *
	 * @param target 目标坐标
	 * @param out 要释放的流体
	 * @return 释放出的流体总量
	 */
	public static int bleedFluid2World(World world, BlockPos target, FluidData out, boolean simulate) {
		Fluid fluid = out.getFluid();
		if (out.getAmount() < 1000 || fluid == null || !fluid.canBePlacedInWorld()) return 0;
		Block targetBlock = world.getBlockState(target).getBlock();
		if (targetBlock != fluid.getBlock()) {
			if (!targetBlock.isReplaceable(world, target)) return 0;
			FluidStack stack = out.toStack();
			List<EntityPlayer> players = WorldUtil.getPlayersInRange(world, new Range3D(target, 16));
			if (world.provider.doesWaterVaporize() && fluid.doesVaporize(stack)) {
				if (!simulate) players.forEach(it -> fluid.vaporize(it, world, target, stack));
			} else {
				if (!simulate) {
					WorldUtil.putFluid2World(world, target, fluid);
					SoundEvent soundevent = fluid.getEmptySound(stack);
					players.forEach(it -> world.playSound(it,
							target, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F));
				}
			}
			int result = 1000;
			if (out.getAmount() >= 2000) {
				result += bleedFluid2World(world, target, out.copy(out.getAmount() - 1000), simulate);
			}
			return result;
		} else {
			return bleedFluid2World(world, target.offset(EnumFacing.DOWN), out, simulate);
		}
	}
	
	/**
	 * 放置流体到世界
	 * @param world 世界
	 * @param pos 坐标
	 * @param fluid 流体
	 */
	public static void putFluid2World(World world, BlockPos pos, Fluid fluid) {
		setBlockState(world, pos, fluid.getBlock().getDefaultState());
		fluid.getBlock().onNeighborChange(world, pos, pos.offset(EnumFacing.NORTH));
	}
	
	/**
	 * 设置BlockState，当新旧state一致时不进行替换
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param newState 新的state
	 */
	public static void setBlockState(World world, BlockPos pos, IBlockState newState) {
		world.setBlockState(pos, newState, 11);
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/**
	 * 判断指定方块是否为完整方块或接近于完整方块
	 * @param access 所在世界
	 * @param pos 方块坐标
	 */
	public static boolean isFullBlock(IBlockAccess access, BlockPos pos) {
		IBlockState state = access.getBlockState(pos);
		if (state.isFullBlock() && state.isFullCube()) return true;
		AxisAlignedBB box = state.getBoundingBox(access, pos);
		double width = box.maxX - box.minX;
		double height = box.maxY - box.minY;
		double length = box.maxZ - box.minZ;
		return (width * height * length) >= 0.75;
	}
	
	/**
	 * 使指定位置着火
	 * @param world 所在世界
	 * @param pos 坐标
	 */
	public static void setFire(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isReplaceable(world, pos) ||
				state.getBlock() instanceof EleTransferBlock ||
				state.getBlock() == Blocks.AIR) {
			world.setBlockState(pos, Blocks.FIRE.getDefaultState());
		}
	}
	
	/**
	 * 遍历指定方块周围的所有TE，不包含TE的不会进行遍历
	 * @param world 所在世界
	 * @param pos 中心方块
	 * @param run 要运行的代码，其中TE只遍历到的TE，EnumFacing指TE相对于中心方块的方向
	 */
	public static void forEachAroundTE(World world, BlockPos pos, BiConsumer<TileEntity, EnumFacing> run) {
		TileEntity te;
		for (EnumFacing facing : EnumFacing.values()) {
			te = world.getTileEntity(pos.offset(facing));
			if (te != null) run.accept(te, facing);
		}
	}
	
	/**
	 * 判断other在now的哪个方向
	 */
	public static EnumFacing whatFacing(BlockPos now, BlockPos other) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (now.offset(facing).equals(other)) return facing;
		}
		throw new IllegalArgumentException("now[" + now + "]和other" + other + "不相邻！");
	}
	
	/** 判断是否为服务端 */
	public static boolean isServer() {
		return isServer(null);
	}
	
	/** 判断是否为客户端 */
	public static boolean isClient() {
		return !isServer(null);
	}
	
	/**
	 * 判断是否为服务端.
	 * 因为判断方法不必须依赖世界对象，所以world也可以为null。
	 * 使用null时将启动与使用world不同的算法。
	 * @param world 世界对象（可为null）
	 */
	public static boolean isServer(@Nullable World world) {
		if (world == null) {
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()) return true;
			return Thread.currentThread().getName().toLowerCase().contains("server");
		} else {
			return !world.isRemote;
		}
	}
	
	/** @see #isServer(World) */
	public static boolean isClient(@Nullable World world) {
		return !isServer(world);
	}
	
	//---------------------私有内容---------------------//
	
	/** 客户端移除列表 */
	private static final Map<World, List<TileEntity>> CLIENT_REMOVES = new Object2ObjectArrayMap<>(3);
	/** 服务端移除列表 */
	private static final Map<World, List<TileEntity>> SERVER_REMOVES = new Object2ObjectArrayMap<>(3);

	/** 客户端添加列表 */
	private static final Map<World, List<TileEntity>> CLIENT_ADDS = new Object2ObjectArrayMap<>(3);
	/** 服务端添加列表 */
	private static final Map<World, List<TileEntity>> SERVER_ADDS = new Object2ObjectArrayMap<>(3);
	
	private static boolean clearServerTickableList() {
		SERVER_REMOVES.forEach((key, value) -> key.tickableTileEntities.removeAll(value));
		SERVER_REMOVES.clear();
		SERVER_ADDS.forEach((key, value) -> key.tickableTileEntities.addAll(value));
		SERVER_ADDS.clear();
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	private static boolean clearClientTickableList() {
		CLIENT_REMOVES.forEach((key, value) -> key.tickableTileEntities.removeAll(value));
		CLIENT_REMOVES.clear();
		CLIENT_ADDS.forEach((key, value) -> key.tickableTileEntities.addAll(value));
		CLIENT_ADDS.clear();
		return true;
	}
	
}