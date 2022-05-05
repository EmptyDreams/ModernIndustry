package top.kmar.mi.api.gui.component.group;

import net.minecraft.client.resources.I18n;
import top.kmar.mi.api.utils.TickHelper;
import top.kmar.mi.content.gui.ControlPanel;
import top.kmar.mi.api.graph.client.GuiPainter;

import java.util.ArrayList;
import java.util.List;

/**
 * 带选择框的Group
 * @author EmptyDreams
 */
public class SelectGroup extends Group {
	
	protected final List<InnerGroup> containers = new ArrayList<>();
	protected final ControlPanel control;
	protected final Style style;
	protected int index = 0;
	
	public SelectGroup(Style style, int width, int height) {
		super(0, 0, width, height, Panels::non);
		this.style = style;
		control = style.createControlPanel(this);
	}
	
	/** 用指定标题构建一个页面 */
	public Group createNewPage(String title) {
		InnerGroup result = style.createContainerPanel(this);
		result.setTitle(title);
		if (containers.isEmpty()) add(result);
		containers.add(result);
		return result;
	}
	
	/** 构建一个新的页面，标题为页码 */
	public Group createNewPage() {
		int size = containers.size() + 1;
		Group result = createNewPage(I18n.format("book.pageIndicator", size, size));
		for (int i = 0; i < containers.size(); i++) {
			containers.get(i).setTitle(I18n.format("book.pageIndicator", i + 1, size));
		}
		return result;
	}
	
	/** 下一页，如果当前页为最后一页，则不做反应 */
	public void next() {
		if (index == containers.size() - 1) return;
		Group remove = getActivatePage();
		++index;
		TickHelper.addAutoTask(() -> {
			super.components.remove(remove);
			super.add(getActivatePage());
			return true;
		});
	}

	/** 上一页，如果当前页为第一页，则不做反应 */
	public void pre() {
		if (index == 0) return;
		Group remove = getActivatePage();
		--index;
		TickHelper.addAutoTask(() -> {
			super.components.remove(remove);
			super.add(getActivatePage());
			return true;
		});
	}
	
	/** 获取当前显示的页面 */
	public InnerGroup getActivatePage() {
		return containers.get(index);
	}
	
	@Override
	public void paint(GuiPainter painter) {
		control.setTitle(getActivatePage().getTitle());
		super.paint(painter);
	}
	
	public enum Style {
		
		/** 矩形按钮（上方） */
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
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 18, group.getWidth(), group.getHeight() - 18);
			}
		},
		/** 矩形按钮（下方） */
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
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 0, group.getWidth(), group.getHeight() - 18);
			}
		},
		REC_LEFT {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.REC_RIGHT_AND_LEFT, group.getHeight());
				result.setLocation(0, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(18, 0, group.getWidth() - 18, group.getHeight());
			}
		},
		REC_RIGHT {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.REC_RIGHT_AND_LEFT, group.getHeight());
				result.setLocation(group.getWidth() - 15, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 0, group.getWidth() - 18, group.getHeight());
			}
		},
		/** 弧形按钮（上方） */
		ARC_UP {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.ARC_UP_AND_DOWN, group.getWidth());
				result.setLocation(0, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 18, group.getWidth(), group.getHeight() - 18);
			}
		},
		/** 弧形按钮（下方） */
		ARC_DOWN {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.ARC_UP_AND_DOWN, group.getWidth());
				result.setLocation(0, 18);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 0, group.getWidth(), group.getHeight() - 18);
			}
		},
		/** 正方形按钮（上方） */
		SQUARE_UP {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.SQUARE_UP_AND_DOWN, group.getWidth());
				result.setLocation(0, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 18, group.getWidth(), group.getHeight() - 18);
			}
		},
		/** 正方形按钮（下方） */
		SQUARE_DOWN {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.SQUARE_UP_AND_DOWN, group.getWidth());
				result.setLocation(0, group.getHeight() - 15);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 0, group.getWidth(), group.getHeight() - 18);
			}
		},
		/** 正方向按钮（左侧） */
		SQUARE_LEFT {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.SQUARE_LEFT_AND_RIGHT, group.getHeight());
				result.setLocation(0, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(18, 0, group.getWidth() - 18, group.getHeight());
			}
		},
		/** 正方形按钮（右侧） */
		SQUARE_RIGHT {
			@Override
			public ControlPanel createControlPanel(SelectGroup group) {
				ControlPanel result = new ControlPanel(
						ControlPanel.Style.SQUARE_LEFT_AND_RIGHT, group.getHeight());
				result.setLocation(group.getWidth() - 15, 0);
				result.setNextOperator(it -> group.next());
				result.setPreOperator(it -> group.pre());
				group.add(result);
				return result;
			}
			
			@Override
			public InnerGroup createContainerPanel(SelectGroup group) {
				return new InnerGroup(0, 0, group.getWidth() - 18, group.getHeight());
			}
		}
		
		;
		/** 创建一个控制面板 */
		abstract public ControlPanel createControlPanel(SelectGroup group);
		/** 创建一个容纳控件的控件组 */
		abstract public InnerGroup createContainerPanel(SelectGroup group);
	
	}
	
	private static class InnerGroup extends Group {
		
		private String title;
		
		InnerGroup(int x, int y, int width, int height) {
			super(x, y, width, height, Panels::non);
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
		
		@Override
		public void setSize(int width1, int height1) { }
		
		@Override
		public void setLocation(int x1, int y1) { }
		
	}
	
}