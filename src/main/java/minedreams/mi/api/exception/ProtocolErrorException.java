package minedreams.mi.api.exception;

/**
 * 当代码运行没有遵循协议时抛出
 *
 * @author EmptyDreams
 * @version V1.0
 */
public class ProtocolErrorException extends RuntimeException {
	
	/** 继承结构异常 */
	public static final RuntimeException INHERITANCE_STRUCTURE =
			new ProtocolErrorException("类的继承结构没有遵顼协议");
	
	public ProtocolErrorException(String text) {
		super(text);
	}
	
}
