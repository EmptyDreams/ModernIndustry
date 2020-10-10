package xyz.emptydreams.mi.api.utils;

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
import xyz.emptydreams.mi.api.utils.data.Point3D;
import xyz.emptydreams.mi.api.utils.data.Range3D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 关于世界的操作
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class WorldUtil {
	
	/**
	 * 获取所有世界中指定名称的玩家的对象
	 * @param name 名称
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
		if (isServer(null)) {
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
	 * @return 若客户端当前世界的dimension不为输入值则返回null
	 * @see #getClientWorld()
	 */
	@Nonnull
	public static World getWorld(int dimension) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
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
		world.setBlockState(pos, newState);
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/**
	 * 优化客户端/服务端判断<br>
	 * <b>注意：该方法对单机游戏无效</b>
	 */
	public static void optimizeSideCalculate(@Nullable World world) {
		if (!isServer && isServer(world)) {
			try {
				Class.forName("xyz.emptydreams.mi.api.utils.WorldUtil$ServerTest");
				isServer = true;
			} catch (Throwable e) {
				MISysInfo.print("系统检测到当前环境不为服务端，跳过服务端优化");
				MISysInfo.print("如果你在服务端看到这条消息，请发送报告给作者！");
			}
		}
	}
	
	private static boolean isServer = false;
	
	/**
	 * 判断是否为服务端.
	 * 因为判断方法不必须依赖世界对象，所以world也可以为null。
	 * 使用null时将启动与使用world不同的算法。
	 * 在非ServerThread或ClientThread时可能会出现误判，
	 * 但是该方法误判代表FMLCommonHandler.instance().getSize()一定会误判
	 * @param world 世界对象（可为null）
	 */
	public static boolean isServer(@Nullable World world) {
		if (isServer) return true;
		if (world == null) {
			if (FMLCommonHandler.instance().getSide().isServer()) return true;
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
	private static final Map<World, List<TileEntity>> CLIENT_REMOVES = new LinkedHashMap<>();
	/** 服务端移除列表 */
	private static final Map<World, List<TileEntity>> SERVER_REMOVES = new LinkedHashMap<>();

	@SubscribeEvent
	public static void atServerTickEnd(TickEvent.ServerTickEvent event) {
		SERVER_REMOVES.forEach((key, value) -> key.tickableTileEntities.removeAll(value));
		SERVER_REMOVES.clear();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void atClientTickEnd(TickEvent.ClientTickEvent event) {
		CLIENT_REMOVES.forEach((key, value) -> key.tickableTileEntities.removeAll(value));
		CLIENT_REMOVES.clear();
	}
	
	@SideOnly(Side.SERVER)
	private static final class ServerTest {
	}
	
}
