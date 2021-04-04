package xyz.emptydreams.mi.api.net;

/**
 * @author EmptyDreams
 */
public enum ParseResultEnum {
	
	/** 重试 */
	RETRY,
	/** 丢弃信息 */
	THROW,
	/** 成功处理 */
	SUCCESS,
	/** 出现异常 */
	EXCEPTION;
	
	/** 是否丢弃信息 */
	public boolean isThrow() {
		return this == THROW || this == EXCEPTION;
	}
	
	/** 是否发生异常 */
	public boolean isException() {
		return this == EXCEPTION;
	}
	
	/** 是否成功处理 */
	public boolean isSuccess() {
		return this == SUCCESS;
	}
	
	/** 是否重新处理 */
	public boolean isRetry() {
		return this == RETRY;
	}
	
}