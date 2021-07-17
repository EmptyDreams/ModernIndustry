package xyz.emptydreams.mi.api.net.message;

import net.minecraft.entity.player.EntityPlayerMP;
import xyz.emptydreams.mi.api.net.ParseResultEnum;

/**
 * <p>用于存储网络通信信息处理中的额外数据
 * <p>用户可以继承该类以添加自己的数据
 * @author EmptyDreams
 */
public class ParseAddition {
	
	/** 本次处理结果 */
	private ParseResultEnum parseResult = ParseResultEnum.THROW;
	/** 处理次数（包含本次） */
	private int amount = 0;
	/** 发包的玩家 */
	private final EntityPlayerMP player;
	
	public ParseAddition(EntityPlayerMP player) {
		this.player = player;
	}
	
	public ParseAddition() {
		this(null);
	}
	
	/** 设置本次处理结果 */
	public ParseAddition setParseResult(ParseResultEnum result) {
		this.parseResult = result;
		return this;
	}
	
	/** 增加一次处理次数（一次等于1tick） */
	public ParseAddition plusAmount() {
		++amount;
		return this;
	}
	
	/** 获取本次处理结果 */
	public ParseResultEnum getParseResult() {
		return parseResult;
	}
	
	/** 获取当前处理次数（包含本次） */
	public int getAmount() {
		return amount;
	}
	
	public EntityPlayerMP getServicePlayer() {
		return player;
	}
	
	/** @see ParseResultEnum#isRetry()  */
	public boolean isRetry() {
		return getParseResult().isRetry();
	}
	
	/** @see ParseResultEnum#isThrow()  */
	public boolean isThrow() {
		return getParseResult().isThrow();
	}
	
	/** @see ParseResultEnum#isException()  */
	public boolean isException() {
		return getParseResult().isException();
	}
	
	/** @see ParseResultEnum#isSuccess()  */
	public boolean isSuccess() {
		return getParseResult().isSuccess();
	}
	
}