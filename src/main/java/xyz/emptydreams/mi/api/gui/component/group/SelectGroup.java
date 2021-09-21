package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.ButtonComponent;
import xyz.emptydreams.mi.api.gui.component.MComponent;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponentManager;
import xyz.emptydreams.mi.api.utils.data.math.Point2D;
import xyz.emptydreams.mi.api.utils.data.math.Size2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static xyz.emptydreams.mi.api.gui.component.ButtonComponent.Style.*;

/**
 * 带选择框的Group
 * @author EmptyDreams
 */
public class SelectGroup extends MComponent {

	/** 翻页键位置 */
	private final Style style;
	/** 页面列表 */
	protected final List<Group> innerList = new ArrayList<>();
	/** 是否显示页码 */
	private boolean showPageNum = true;
	/** 是否锁定 */
	private boolean isLock = false;
	/** 内部管理器 */
	private final Group components;
	/** 当前页面 */
	protected int index = 0;
	
	public SelectGroup(Style style, int width, int height) {
		this(style, width, height, Panels::non);
	}
	
	public SelectGroup(Style style, int width, int height, Consumer<Group> panel) {
		this.style = style;
		super.setSize(width, height);
		components = new Group(0, 0, width, height, Panels::non);
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
		return result;
	}
	
	@Override
	public void setSize(int width, int height) { }
	
	@Override
	protected void init(IComponentManager manager, EntityPlayer player) {
		super.init(manager, player);
		components.init(manager, player);
		innerList.forEach(it -> it.init(manager, player));
	}
	
	@Override
	public void onAdd2Manager(IComponentManager manager, EntityPlayer player) {
		super.onAdd2Manager(manager, player);
		components.onAdd2Manager(manager, player);
		innerList.forEach(it -> it.onAdd2Manager(components, player));
	}
	
	@Override
	public void onAdd2ClientFrame(StaticFrameClient frame, EntityPlayer player) {
		super.onAdd2ClientFrame(frame, player);
		components.onAdd2ClientFrame(frame, player);
		innerList.forEach(it -> it.onAdd2ClientFrame(frame, player));
	}
	
	@Override
	public void send(Container con, IContainerListener listener) {
		super.send(con, listener);
		components.send(con, listener);
		innerList.forEach(it -> it.send(con, listener));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean update(int codeID, int data) {
		if (super.update(codeID, data)) return true;
		if (components.update(codeID, data)) return true;
		return innerList.stream().anyMatch(it -> it.update(codeID, data));
	}
	
	@Override
	public IComponent containCode(int code) {
		IComponent result = super.containCode(code);
		if (result != null) return result;
		result = components.containCode(code);
		if (result != null) return result;
		for (Group group : innerList) {
			result = group.containCode(code);
			if (result != null) return result;
		}
		return null;
	}
	
	@Nullable
	@Override
	public IComponent getMouseTarget(float mouseX, float mouseY) {
		mouseX -= getX();   mouseY -= getY();
		IComponent result = components.getMouseTarget(mouseX, mouseY);
		if (result != null) return result;
		for (Group group : innerList) {
			result = group.getMouseTarget(mouseX, mouseY);
			if (result != null) return result;
		}
		return null;
	}
	
	@Override
	public void paint(GuiPainter painter) {
		//暂时扔在这
	}
	
	/**
	 * <p>锁定该类
	 * <p>锁定后不允许修改该类及子页面
	 */
	public void lock() {
		isLock = true;
	}
	
	public void showPageNum(boolean is) {
		showPageNum = is;
	}
	
	/** 获取子页面 */
	public Group getInnerGroup(int index) {
		return innerList.get(index);
	}
	
	public boolean isLock() {
		return isLock;
	}
	
	public boolean isShowPageNum() {
		return showPageNum;
	}
	
	public Style getStyle() {
		return style;
	}
	
	private StringComponent pageNumShower;
	
	public enum Style {
		
		UP {
			@Override
			public Size2D getInnerSize(SelectGroup select) {
				return new Size2D(select.getWidth() - 6, select.getHeight() - 24);
			}
			
			@Override
			public Point2D getInnerLocation(SelectGroup select) {
				return new Point2D(0, 19);
			}
			
			@Override
			public void initComponents(SelectGroup select) {
				Style.initHelper(select, 0, 0, PAGE_LEFT, PAGE_RIGHT, true, Panels::horizontalCenter);
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
		};
		
		abstract public Size2D getInnerSize(SelectGroup select);
		abstract public Point2D getInnerLocation(SelectGroup select);
		abstract public void initComponents(SelectGroup select);
		
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
		}
		
	}
	
}