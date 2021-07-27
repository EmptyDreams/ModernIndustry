package xyz.emptydreams.mi.api.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 关于世界的操作
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
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
		if (tickable.getWorld().isRemote)
			CLIENT_REMOVES.computeIfAbsent(tickable.getWorld(), key -> new LinkedList<>()).add(tickable);
		else
			SERVER_REMOVES.computeIfAbsent(tickable.getWorld(), key -> new LinkedList<>()).add(tickable);
	}

	public static void addTickable(TileEntity tickable) {
		StringUtil.checkNull(tickable, "tickable");
		
	}
	
	/**
	 * 设置BlockState，当新旧state一致时不进行替换
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param newState 新的state
	 */
	public static void setBlockState(World world, BlockPos pos, IBlockState newState) {
		setBlockState(world, pos, world.getBlockState(pos), newState);
	}
	
	/**
	 * 设置BlockState，当新旧state一致时不进行替换
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param oldState 旧的state
	 * @param newState 新的state
	 */
	public static void setBlockState(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (oldState.equals(newState)) return;
		world.setBlockState(pos, newState, 11);
		world.markBlockRangeForRenderUpdate(pos, pos);
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
	
	@SubscribeEvent
	public static void atServerTickEnd(TickEvent.ServerTickEvent event) {
		SERVER_REMOVES.forEach((key, value) -> key.tickableTileEntities.removeAll(value));
		SERVER_REMOVES.clear();
		SERVER_ADDS.forEach((key, value) -> key.tickableTileEntities.addAll(value));
		SERVER_ADDS.clear();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void atClientTickEnd(TickEvent.ClientTickEvent event) {
		CLIENT_REMOVES.forEach((key, value) -> key.tickableTileEntities.removeAll(value));
		CLIENT_REMOVES.clear();
		CLIENT_ADDS.forEach((key, value) -> key.tickableTileEntities.addAll(value));
		CLIENT_ADDS.clear();
	}
	
}