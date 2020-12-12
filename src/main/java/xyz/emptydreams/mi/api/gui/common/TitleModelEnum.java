package xyz.emptydreams.mi.api.gui.common;

/**
 * 标题样式
 * @author EmptyDreams
 */
public enum TitleModelEnum {
	
	/** 左侧 */
	LEFT(0),
	/** 居中 */
	CENTRAL(1),
	/** 右侧 */
	RIGHT(2);
	
	private final int INDEX;
	
	TitleModelEnum(int index) {
		INDEX = index;
	}
	
	public int getIndex() {
		return INDEX;
	}
	
	public static TitleModelEnum getInstance(int index) {
		return values()[index];
	}
	
}
