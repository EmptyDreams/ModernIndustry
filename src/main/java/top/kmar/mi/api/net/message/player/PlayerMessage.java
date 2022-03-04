package top.kmar.mi.api.net.message.player;

import net.minecraftforge.fml.relauncher.Side;
import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.net.ParseResultEnum;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.api.net.message.IMessageHandle;
import top.kmar.mi.api.net.message.ParseAddition;

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
	public ParseAddition parseOnClient(@Nonnull IDataReader message, ParseAddition result) {
		PlayerAddition addition = PlayerAddition.instance(message);
		boolean applyResult = PlayerHandleRegistry.apply(
				addition.getKey(), null, message.readData());
		if (applyResult) return result.setParseResult(ParseResultEnum.SUCCESS);
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return result.setParseResult(ParseResultEnum.EXCEPTION);
	}
	
	@Override
	public ParseAddition parseOnServer(@Nonnull IDataReader message, ParseAddition result) {
		PlayerAddition addition = PlayerAddition.instance(message);
		boolean applyResult = PlayerHandleRegistry.apply(
				addition.getKey(), result.getServicePlayer(), message.readData());
		if (applyResult) return result.setParseResult(ParseResultEnum.SUCCESS);
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return result.setParseResult(ParseResultEnum.EXCEPTION);
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