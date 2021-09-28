package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.client.renderer.GlStateManager;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.component.ButtonComponent;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.utils.data.math.Point2D;
import xyz.emptydreams.mi.api.utils.data.math.Size2D;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static xyz.emptydreams.mi.api.gui.component.ButtonComponent.Style.*;

/**
 * 带选择框的Group
 * @author EmptyDreams
 */
public class SelectGroup extends Group {

	/** 翻页键位置 */
	private final Style style;
	/** 页面列表 */
	protected final List<Group> innerList = new ArrayList<>();
	/** 是否显示页码 */
	private boolean showPageNum = true;
	/** 是否锁定 */
	private boolean isLock = false;
	/** 当前页面 */
	protected int index = 0;
	/** 页码显示器 */
	private StringComponent pageNumShower;
	
	public SelectGroup(Style style, int width, int height) {
		this(style, width, height, Panels::non);
	}
	
	public SelectGroup(Style style, int width, int height, Consumer<Group> panel) {;
		this.style = style;
		super.setSize(width, height);
		style.initComponents(this);
	}
	
	/**
	 * 构建一个新的页面
	 * @return 返回构建出的新的页面，该页面不允许手动调整坐标及尺寸
	 */
	@Nonnull
	public Group createGroup() {
		Group result = new Group() {
			{
				Size2D size = style.getInnerSize(SelectGroup.this);
				Point2D location = style.getInnerLocation(SelectGroup.this);
				super.setSize(size.getWidth(), size.getHeight());
				super.setLocation(location.getX(), location.getY());
			}
			@Override
			public void setSize(int width, int height) { }
			@Override
			public void setLocation(int x, int y) { }
		};
		innerList.add(result);
		add(result);
		return result;
	}
	
	@Override
	public void setSize(int width, int height) { }
	
	@Override
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		painter.paintComponent(super.components.get(0));
		painter.paintComponent(getActiveGroup());
		if (isShowPageNum()) {
			if (pageNumShower == null) {
				pageNumShower = new StringComponent();
			}
			pageNumShower.setString(String.valueOf(getActiveIndex()));
			getStyle().initShowerLocation(this);
			painter.paintComponent(pageNumShower);
		}
	}
	
	/**
	 * <p>锁定该类
	 * <p>锁定后不允许修改该类及子页面
	 */
	public void lock() {
		isLock = true;
	}
	
	/** 设置是否显示页码 */
	public void showPageNum(boolean is) {
		showPageNum = is;
	}
	
	/** 获取子页面 */
	public Group getActiveGroup() {
		return innerList.get(getActiveIndex());
	}
	
	public boolean isLock() {
		return isLock;
	}
	
	/** 获取已激活的页码 */
	public int getActiveIndex() {
		return index;
	}
	
	/** 判断是否显示页码 */
	public boolean isShowPageNum() {
		return showPageNum;
	}
	
	/** 获取风格 */
	public Style getStyle() {
		return style;
	}
	
	public enum Style {
		
		UP {
			@Override
			public Size2D getInnerSize(SelectGroup select) {
				return new Size2D(select.getWidth() - 6, select.getHeight() - 24);
			}
			
			@Override
			public Point2D getInnerLocation(SelectGroup select) {
				return new Point2D(0, 0);
			}
			
			@Override
			public void initComponents(SelectGroup select) {
				Style.initHelper(select, 0, 0, PAGE_LEFT, PAGE_RIGHT, true, Panels::horizontalCenter);
			}
			
			@Override
			public void initShowerLocation(SelectGroup selectGroup) {
				selectGroup.pageNumShower.setSize(selectGroup.getWidth(), 9);
				selectGroup.pageNumShower.setLocation(0, 0);
			}
		},
		DOWN {
			@Override
			public Size2D getInnerSize(SelectGroup select) {
				return UP.getInnerSize(select);
			}
			
			@Override
			public Point2D getInnerLocation(SelectGroup select) {
				return new Point2D(0, select.getHeight() - 19);
			}
			
			@Override
			public void initComponents(SelectGroup select) {
				Style.initHelper(select, 0, select.getHeight() - 15,
						PAGE_LEFT, PAGE_RIGHT, true, Panels::horizontalCenter);
			}
			
			@Override
			public void initShowerLocation(SelectGroup selectGroup) {
				selectGroup.pageNumShower.setSize(selectGroup.getWidth(), 9);
				selectGroup.pageNumShower.setLocation(0, selectGroup.getHeight() - 10);
			}
		},
		LEFT {
			@Override
			public Size2D getInnerSize(SelectGroup select) {
				return new Size2D(select.getWidth() - 24, select.getHeight() - 6);
			}
			
			@Override
			public Point2D getInnerLocation(SelectGroup select) {
				return new Point2D(0, 0);
			}
			
			@Override
			public void initComponents(SelectGroup select) {
				Style.initHelper(select, 0, 0, PAGE_UP, PAGE_DOWN, false, Panels::verticalCenter);
			}
			
			@Override
			public void initShowerLocation(SelectGroup selectGroup) {
				selectGroup.pageNumShower.setSize(15, selectGroup.getHeight());
				selectGroup.pageNumShower.setLocation(0, 0);
			}
		},
		RIGHT {
			@Override
			public Size2D getInnerSize(SelectGroup select) {
				return LEFT.getInnerSize(select);
			}
			
			@Override
			public Point2D getInnerLocation(SelectGroup select) {
				return new Point2D(select.getWidth() - 19, 0);
			}
			
			@Override
			public void initComponents(SelectGroup select) {
				Style.initHelper(select, select.getWidth() - 15, 0,
						PAGE_UP, PAGE_DOWN, false, Panels::verticalCenter);
			}
			
			@Override
			public void initShowerLocation(SelectGroup selectGroup) {
				selectGroup.pageNumShower.setSize(15, selectGroup.getHeight());
				selectGroup.pageNumShower.setLocation(selectGroup.getWidth() - 15, 0);
			}
		};
		
		/** 获取内部Group的大小 */
		abstract public Size2D getInnerSize(SelectGroup select);
		/** 获取内部Group的坐标 */
		abstract public Point2D getInnerLocation(SelectGroup select);
		/** 初始化SelectGroup */
		abstract public void initComponents(SelectGroup select);
		/** 初始化页码显示器 */
		abstract public void initShowerLocation(SelectGroup selectGroup);
		
		/**
		 * @param select 选择框控件
		 * @param x 按钮坐标
		 * @param y 按钮坐标
		 * @param preStyle 上一页按钮的形状
		 * @param nextStyle 下一页按钮的形状
		 * @param isHor 按钮是否水平显示
		 * @param panels 排序板
		 */
		private static void initHelper(SelectGroup select, int x, int y,
		                               ButtonComponent.Style preStyle, ButtonComponent.Style nextStyle,
		                               boolean isHor, Consumer<Group> panels) {
			Group group = new Group(x, y,
					isHor ? select.getWidth() : 15, isHor ? 15 : select.getHeight(), panels);
			ButtonComponent left = new ButtonComponent(isHor ? 10 : 15, isHor ? 15 : 10, preStyle);
			ButtonComponent right = new ButtonComponent(isHor ? 10 : 15, isHor ? 15 : 10, nextStyle);
			select.pageNumShower = new StringComponent();
			select.pageNumShower.setSize(isHor ? 20 : 15, isHor ? 15 : 20);
			group.adds(left, select.pageNumShower, right);
			select.add(group);
		}
		
	}
	
}