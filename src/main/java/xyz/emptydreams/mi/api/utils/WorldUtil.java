package xyz.emptydreams.mi.api.utils;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * 关于世界的操作
 * @author EmptyDreams
 * @version V1.0
 */
public final class WorldUtil {
	
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
	
}
