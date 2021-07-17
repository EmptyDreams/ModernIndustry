package xyz.emptydreams.mi.api.net.message.player;

import net.minecraftforge.fml.relauncher.Side;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.net.message.ParseAddition;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.api.net.ParseResultEnum.EXCEPTION;
import static xyz.emptydreams.mi.api.net.ParseResultEnum.SUCCESS;

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
	public ParseAddition parseOnClient(@Nonnull IDataReader message, ParseAddition result) {
		PlayerAddition addition = PlayerAddition.instance(message);
		boolean applyResult = PlayerHandleRegistry.apply(
				addition.getKey(), null, message.readData());
		if (applyResult) return result.setParseResult(SUCCESS);
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return result.setParseResult(EXCEPTION);
	}
	
	@Override
	public ParseAddition parseOnServer(@Nonnull IDataReader message, ParseAddition result) {
		PlayerAddition addition = PlayerAddition.instance(message);
		boolean applyResult = PlayerHandleRegistry.apply(
				addition.getKey(), result.getServicePlayer(), message.readData());
		if (applyResult) return result.setParseResult(SUCCESS);
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return result.setParseResult(EXCEPTION);
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
	@Nonnull
	@Override
	public IDataReader packaging(@Nonnull IDataReader data, PlayerAddition addition) {
		ByteDataOperator operator = new ByteDataOperator(data.size() + 50);
		addition.writeTo(operator);
		operator.writeData(data);
		return operator;
	}
	
}