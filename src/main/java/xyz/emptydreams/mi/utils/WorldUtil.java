package xyz.emptydreams.mi.utils;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * 关于世界的操作
 * @author EmptyDreams
 * @version V1.0
 */
public final class WorldUtil {
	
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
