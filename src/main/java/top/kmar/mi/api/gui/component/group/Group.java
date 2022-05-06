package top.kmar.mi.api.gui.component.group;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.gui.component.interfaces.IComponent;
import top.kmar.mi.api.gui.component.interfaces.IComponentManager;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.graph.utils.GuiPainter;
import top.kmar.mi.api.gui.client.StaticFrameClient;
import top.kmar.mi.api.gui.component.MComponent;
import top.kmar.mi.api.graph.listeners.mouse.IMouseLocationListener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 控件包，支持自动排版
 * @author EmptyDreams
 */
public class Group extends MComponent implements Iterable<IComponent>, IComponentManager {

	/** 包含的控件 */
	protected final List<IComponent> components = new LinkedList<>();
	/** 对齐模式 */
	protected Consumer<Group> mode;
	/** 两个控件间的最短距离(像素) */
	protected int minDistance = 3;
	/** 两个控件间的最远距离(像素) */
	protected int maxDistance = 10;
	/** 存储上一层管理类 */
	private IComponentManager superManager;

	public Group() {
		this(0, 0, 0, 0, Panels::non);
	}
	
	public Group(Consumer<Group> panel) {
		this(0, 0, 0, 0, panel);
	}
	
	/**
	 * 创建一格group
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @param width 宽度
	 * @param height 高度
	 * @param panel 对其模式
	 * @see Panels
	 */
	public Group(int x, int y, int width, int height, Consumer<Group> panel) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		mode = StringUtil.checkNull(panel, "panel");
	}
	
	public Group add(IComponent component) {
		components.add(StringUtil.checkNull(component, "component"));
		if (superManager != null) component.onAdd2Manager(this);
		return this;
	}

	/**
	 * 添加多个组件
	 * @throws NullPointerException 如果components == null或其中包含null
	 */
	public void adds(IComponent... components) {
		for (IComponent iComponent : components) {
			add(iComponent);
		}
	}
	
	/** 遍历所有组件 */
	@Override
	public void forEachComponent(Predicate<? super IComponent> predicate) {
		for (IComponent component : components) {
			if (!predicate.test(component)) break;
		}
	}
	
	/** 获取复制的组件列表 */
	@Override
	public ArrayList<IComponent> cloneComponent() {
		return new ArrayList<>(components);
	}
	
	/** 获取组件数量 */
	@Override
	public int componentSize() {
		return components.size();
	}
	
	@Nonnull
	@Override
	public IComponentManager getSuperManager() {
		if (superManager == null) throw new NullPointerException("不存在上级管理类");
		return superManager;
	}
	
	@Override
	public boolean isFrame() {
		return false;
	}
	
	@Override
	public IComponent containCode(int code) {
		IComponent component = super.containCode(code);
		if (component != null) return component;
		for (IComponent c : components) {
			component = c.containCode(code);
			if (component != null) return component;
		}
		return null;
	}
	
	/** 获取两个控件间的最短距离 */
	public int getMinDistance() { return minDistance; }
	/** 设置两个控件间的最短距离 */
	public void setMinDistance(int minDistance) { this.minDistance = minDistance; }
	/** 获取两个控件间的最远距离 */
	public int getMaxDistance() { return maxDistance; }
	/** 设置两个控件间的最远距离 */
	public void setMaxDistance(int maxDistance) { this.maxDistance = maxDistance; }
	/** 获取排列模式 */
	public Consumer<Group> getControlMode() { return mode; }
	/** 设置排列模式 */
	public void setControlPanel(Consumer<Group> mode) {
		this.mode = StringUtil.checkNull(mode, "mode");
	}
	/** 遍历控件 */
	@Override
	public Iterator<IComponent> iterator() { return components.iterator(); }
	
	@Override
	protected void initForManager(IComponentManager manager) {
		super.initForManager(manager);
		registryListener((IMouseLocationListener) (mouseX, mouseY) ->
				components.forEach(it -> it.activateListener(manager.getFrame(),
															 IMouseLocationListener.class,
															 event -> event.mouseLocation(mouseX, mouseY))));
	}
	
	protected boolean isSort = true;
	
	protected void sort() {
		if (isSort) {
			isSort = false;
			for (IComponent component : components) {
				if (component instanceof Group) ((Group) component).sort();
			}
			mode.accept(this);
		}
	}
	
	@Override
	public void onAdd2Manager(IComponentManager manager) {
		super.onAdd2Manager(manager);
		sort();
		if (superManager == null) {
			superManager = manager;
			components.forEach(it -> {
				it.onAdd2Manager(Group.this);
				allocID(it);
			});
		} else {
			components.forEach(manager::allocID);
		}
	}

	@Override
	public void onAdd2ClientFrame(StaticFrameClient frame) {
		for (IComponent component : components) {
			if (component instanceof Group) ((Group) component).sort();
		}
		mode.accept(this);
		components.forEach(it -> it.onAdd2ClientFrame(frame));
	}

	@Override
	public void send(Container con, IContainerListener listener) {
		components.forEach(it -> it.send(con, listener));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean update(int codeID, int data) {
		for (IComponent component : components)
			if (component.update(codeID, data)) return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		for (IComponent it : components) {
			painter.paintComponent(it);
		}
	}
	
	@Override
	public String toString() {
		return "控件数量：" + componentSize() +
				"；坐标：(" + getX() + "," + getY() + ")；" +
				"大小：(" + getWidth() + "," + getHeight() + ")";
	}

}