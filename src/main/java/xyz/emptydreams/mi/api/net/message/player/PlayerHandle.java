package xyz.emptydreams.mi.api.net.message.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * 用于在服务端/客户端接收到玩家信息时处理相关内容
 * @author EmptyDreams
 */
public interface PlayerHandle {
	
	/**
	 * 处理信息
	 * @param player 发送（接收）消息的玩家
	 * @param data 数据内容
	 */
	void apply(EntityPlayer player, NBTTagCompound data);
	
}