package xyz.emptydreams.mi.api.gui.component.group;

import xyz.emptydreams.mi.content.gui.ControlPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * 带选择框的Group
 * @author EmptyDreams
 */
public class SelectGroup extends Group {
	
	protected final List<Group> containers = new ArrayList<>();
	protected final ControlPanel control;
	protected final Style style;
	protected int index = 0;
	
	public SelectGroup(Style style, int width, int height) {
		super(0, 0, width, height, Panels::non);
		this.style = style;
		control = style.createControlPanel(this);
	}
	
	/** 构建一个新的页面 */
	public Group createNewPage() {
		Group result = style.createContainerPanel(this);
		if (containers.isEmpty()) add(result);
		containers.add(result);
		return result;
	}
	
	/** 设置标题 */
	public void setTitle(String title) {
		control.setTitle(title);
	}
	
	/** 下一页，如果当前页为最后一页，则不做反应 */
	public void next() {
		if (index == containers.size() - 1) return;
		super.components.remove(getActivatePage());
		++index;
		super.components.add(getActivatePage());
	}

	/** 上一页，如果当前页为第一页，则不做反应 */
	public void pre() {
		if (index == 0) return;
		super.components.remove(getActivatePage());
		--index;
		super.components.add(getActivatePage());
	}
	
	/** 获取当前显示的页面 */
	public Group getActivatePage() {
		return containers.get(index);
	}
	
	public enum Style {
		
		REC_UP {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.REC_UP_AND_DOWN, group.getWidth());
				result.setLocation(0, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public Group createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 18, group.getWidth(), group.getHeight() - 18);
			}
		},
		REC_DOWN {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.REC_RIGHT_AND_LEFT, group.getWidth());
				result.setLocation(0, group.getHeight() - 15);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public Group createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 0, group.getWidth(), group.getHeight() - 18);
			}
		}
		
		;
		/** 创建一个控制面板 */
		abstract public ControlPanel createControlPanel(SelectGroup group);
		/** 创建一个容纳控件的控件组 */
		abstract public Group createContainerPanel(SelectGroup group);
	
	}
	
	private static class InnerGroup extends Group {
		
		InnerGroup(int x, int y, int width, int height) {
			super(x, y, width, height, Panels::non);
		}
		
		@Override
		public void setSize(int width1, int height1) { }
		
		@Override
		public void setLocation(int x1, int y1) { }
		
	}
	
}