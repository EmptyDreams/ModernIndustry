package xyz.emptydreams.mi.api.net.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.NetworkLoader;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.Range3D;

import java.util.function.Predicate;

/**
 * 发送消息的工具类
 * @author EmptyDreams
 */
public final class MessageSender {
	
	/**
	 * 发送信息到服务端
	 * @param message 消息
	 */
	@SideOnly(Side.CLIENT)
	public static void sendToServer(IMessage message) {
		NetworkLoader.instance().sendToServer(message);
	}
	
	/**
	 * 发送消息到在指定世界指定范围内的所有玩家
	 * @param message 消息
	 * @param world 世界
	 * @param range 范围
	 */
	public static void sendToClientAround(IMessage message, World world, Range3D range) {
		WorldUtil.forEachPlayers(world, range, player -> sendToClient((EntityPlayerMP) player, message));
	}
	
	/**
	 * 发送信息到指定世界中满足条件的所有玩家
	 * @param message 消息
	 * @param world 世界对象
	 * @param test 条件表达式
	 * @throws ClassCastException 如果world中存储的用户对象不是{@link EntityPlayerMP}
	 */
	public static void sendToClientIf(IMessage message, World world, Predicate<EntityPlayer> test) {
		WorldUtil.forEachPlayers(world, player -> {
			if (test.test(player)) sendToClient((EntityPlayerMP) player, message);
		});
	}
	
	/**
	 * 发送信息到指定世界中的所有玩家
	 * @param message 消息
	 * @param world 世界对象
	 * @throws ClassCastException 如果world中存储的用户对象不是{@link EntityPlayerMP}
	 */
	public static void sendToClient(IMessage message, World world) {
		WorldUtil.forEachPlayers(world, player -> sendToClient((EntityPlayerMP) player, message));
	}
	
	/**
	 * 遍历世界中所有玩家，如果玩家满足指定要求则发送消息给玩家
	 * @param message 消息
	 * @param test 条件表达式
	 * @throws ClassCastException 如果world中存储的用户对象不是{@link EntityPlayerMP}
	 */
	public static void sendToClientIf(IMessage message, Predicate<EntityPlayer> test) {
		WorldUtil.forEachPlayers(player -> {
			if (test.test(player)) sendToClient((EntityPlayerMP) player, message);
		});
	}
	
	/**
	 * 将消息发送到指定玩家
	 * @param message 信息
	 */
	public static void sendToClientAll(IMessage message) {
		NetworkLoader.instance().sendToAll(message);
	}
	
	/**
	 * 将消息发送到指定玩家
	 * @param player 玩家对象
	 * @param message 信息
	 */
	public static void sendToClient(EntityPlayerMP player, IMessage message) {
		NetworkLoader.instance().sendTo(message, player);
	}

}
