package xyz.emptydreams.mi.api.exception;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class RepeatSettingException extends RuntimeException {
	
	public RepeatSettingException(String text) {
		super("参数重复设置：" + text);
	}
	
}
