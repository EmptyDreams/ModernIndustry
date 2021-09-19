package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.MComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponentManager;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseLocationListener;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
	/** */
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

	/**
	 * 添加一个组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(IComponent component) {
		components.add(StringUtil.checkNull(component, "component"));
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
	public void forEachComponent(Consumer<? super IComponent> consumer) {
		components.forEach(consumer);
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
	protected void init(IComponentManager manager, EntityPlayer player) {
		super.init(manager, player);
		registryListener((MouseLocationListener) (mouseX, mouseY) ->
				components.forEach(it -> it.activateListener(manager.getFrame(),
															 MouseLocationListener.class,
															 event -> event.mouseLocation(mouseX, mouseY))));
	}
	
	private boolean isSort = true;
	
	private void sort() {
		if (isSort) {
			isSort = false;
			for (IComponent component : components) {
				if (component instanceof Group) ((Group) component).sort();
			}
			mode.accept(this);
		}
	}
	
	@Override
	public void onAdd2Manager(IComponentManager manager, EntityPlayer player) {
		superManager = manager;
		super.onAdd2Manager(manager, player);
		sort();
		components.forEach(it -> {
			manager.allocID(it);
			it.onAdd2Manager(this, player);
		});
	}

	@Override
	public void onAdd2ClientFrame(StaticFrameClient frame, EntityPlayer player) {
		super.onAdd2ClientFrame(frame, player);
		for (IComponent component : components) {
			if (component instanceof Group) ((Group) component).sort();
		}
		mode.accept(this);
		components.forEach(it -> it.onAdd2ClientFrame(frame, player));
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
	public void realTimePaint(GuiPainter painter) {
		components.forEach(it -> it.realTimePaint(
				painter.createPainter(it.getX(), it.getY(), it.getWidth(), it.getHeight())));
	}
	
	@Nullable
	@Override
	public IComponent getMouseTarget(float mouseX, float mouseY) {
		for (IComponent component : components) {
			if (MathUtil.checkMouse2DRec(mouseX, mouseY, component)) {
				IComponent result = component.getMouseTarget(mouseX, mouseY);
				if (result != null)
					return result;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "控件数量：" + componentSize() +
				"；坐标：(" + getX() + "," + getY() + ")；" +
				"大小：(" + getWidth() + "," + getHeight() + ")";
	}

}