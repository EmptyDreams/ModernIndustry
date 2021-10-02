package xyz.emptydreams.mi.api.gui.component.group;

/**
 * 带选择框的Group
 * @author EmptyDreams
 */
public class SelectGroup extends Group {
	
	
	
	public void next() {
	
	}

	public void pre() {
	
	}
	
	public enum Style {
		;
		/** 创建一个控制面板 */
		abstract public Group createContralPanel(SelectGroup group);
	
	}
	
}