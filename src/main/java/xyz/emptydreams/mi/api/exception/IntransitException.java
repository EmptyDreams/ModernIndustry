package xyz.emptydreams.mi.api.exception;

/**
 * 表示这是一个转发的异常
 * @author EmptyDreams
 */
public class IntransitException extends RuntimeException {
	
	public IntransitException(String text, Throwable throwable) {
		super(text, throwable);
	}
	
	public IntransitException(Throwable throwable) {
		super(throwable);
	}
	
}