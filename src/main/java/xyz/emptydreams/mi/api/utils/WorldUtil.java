package xyz.emptydreams.mi.api.utils;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.WaitList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 关于世界的操作
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public final class WorldUtil {

	/**
	 * 将指定任务从每Tick的循环中移除
	 * @param tickable 要移除的任务
	 */
	public static void removeTickable(TileEntity tickable) {
		WaitList.checkNull(tickable, "tickable");
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
				Class.forName(net.minecraft.client.Minecraft.class.getName());
			} catch (ClassNotFoundException e) {
				isServer = true;
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
			return Thread.currentThread().getName().contains("Server Thread");
		} else {
			return !world.isRemote;
		}
	}
	
	/** @see #isServer(World) */
	public static boolean isClient(@Nullable World world) {
		return !isServer(world);
	}

	/** 客户端移除列表 */
	@SideOnly(Side.CLIENT)
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

}