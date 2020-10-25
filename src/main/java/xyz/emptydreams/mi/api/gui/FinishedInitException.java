package xyz.emptydreams.mi.api.gui;

/**
 * @author EmptyDreams
 */
public class FinishedInitException extends RuntimeException {
	
	public FinishedInitException(String text) {
		super(text);
	}
	
	public FinishedInitException() {
		super("初始化完毕后不允许进行修改");
	}
	
}
