package top.kmar.mi.api.net.message.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import top.kmar.mi.api.net.ParseResultEnum;
import top.kmar.mi.api.net.message.IMessageHandle;
import top.kmar.mi.api.net.message.ParseAddition;
import top.kmar.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;

/**
 * <p>以玩家为凭借的服务端-客户端双向通讯
 * <p>处理端：客户端、服务端
 * @author EmptyDreams
 */
public class PlayerMessage implements IMessageHandle<PlayerAddition, ParseAddition> {
	
	public static PlayerMessage instance() {
		return INSTANCE;
	}
	
	private static final PlayerMessage INSTANCE = new PlayerMessage();
	
	private PlayerMessage() { }
	
	@Override
	public ParseAddition parseOnClient(@Nonnull NBTTagCompound message, ParseAddition result) {
		PlayerAddition addition = PlayerAddition.instance(message.getTag("add"));
		boolean applyResult = PlayerHandleRegistry.apply(
				addition.getKey(), null, message.getTag("data"));
		if (applyResult) return result.setParseResult(ParseResultEnum.SUCCESS);
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return result.setParseResult(ParseResultEnum.EXCEPTION);
	}
	
	@Override
	public ParseAddition parseOnServer(@Nonnull NBTTagCompound message, ParseAddition result) {
		PlayerAddition addition = PlayerAddition.instance(message.getTag("add"));
		boolean applyResult = PlayerHandleRegistry.apply(
				addition.getKey(), result.getServicePlayer(), message.getTag("data"));
		if (applyResult) return result.setParseResult(ParseResultEnum.SUCCESS);
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return result.setParseResult(ParseResultEnum.EXCEPTION);
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
}