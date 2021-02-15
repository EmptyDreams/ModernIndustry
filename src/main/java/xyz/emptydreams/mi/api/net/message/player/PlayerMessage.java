package xyz.emptydreams.mi.api.net.message.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;

import javax.annotation.Nonnull;

/**
 * <p>以玩家为凭借的服务端-客户端双向通讯
 * <p>处理端：客户端、服务端
 * @author EmptyDreams
 */
public class PlayerMessage implements IMessageHandle<PlayerAddition> {
	
	public static PlayerMessage instance() {
		return INSTANCE;
	}
	
	private static final PlayerMessage INSTANCE = new PlayerMessage();
	
	private PlayerMessage() { }
	
	@Override
	public boolean parseOnClient(@Nonnull NBTTagCompound message) {
		return parse(message);
	}
	
	@Override
	public boolean parseOnServer(@Nonnull NBTTagCompound message) {
		return parse(message);
	}
	
	/** 解析消息 */
	private boolean parse(NBTTagCompound message) {
		NBTTagCompound data = message.getCompoundTag("data");
		PlayerAddition addition = PlayerAddition.instance(message);
		boolean result = PlayerHandleRegistry.apply(addition.getKey(), addition.getPlayer(), data);
		if (result) return true;
		message.setBoolean("_non", true);
		return false;
	}
	
	@Override
	public boolean match(@Nonnull NBTTagCompound message) {
		return message.getBoolean("_player_message");
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
	@Nonnull
	@Override
	public NBTTagCompound packaging(@Nonnull NBTTagCompound data, PlayerAddition addition) {
		NBTTagCompound message = new NBTTagCompound();
		message.setBoolean("_player_message", true);
		message.setTag("data", data);
		addition.writeTo(message);
		return message;
	}
	
	@Nonnull
	@Override
	public String getInfo(NBTTagCompound message) {
		if (message.getBoolean("_non")) return "没有找到可以处理该信息的Handle";
		return "未知故障，可能是发生了异常";
	}
	
}