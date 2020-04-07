package minedreams.mi.api.gui.info;

/**
 * @author EmptyDreams
 * @version V1.0
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
