package xyz.emptydreams.mi.api.net.message.player;

import net.minecraftforge.fml.relauncher.Side;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.IDataReader;
import xyz.emptydreams.mi.api.net.ParseResultEnum;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.api.net.ParseResultEnum.EXCEPTION;
import static xyz.emptydreams.mi.api.net.ParseResultEnum.SUCCESS;

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
	public ParseResultEnum parseOnClient(@Nonnull IDataReader message) {
		return parse(message);
	}
	
	@Override
	public ParseResultEnum parseOnServer(@Nonnull IDataReader message) {
		return parse(message);
	}
	
	/** 解析消息 */
	private ParseResultEnum parse(IDataReader message) {
		PlayerAddition addition = PlayerAddition.instance(message);
		boolean result = PlayerHandleRegistry.apply(
							addition.getKey(), addition.getPlayer(), message.readData());
		if (result) return SUCCESS;
		MISysInfo.err("没有找到可以处理该信息的Handle");
		return EXCEPTION;
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