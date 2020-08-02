package xyz.emptydreams.mi.api.gui.group;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.IFrame;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.component.IComponent;
import xyz.emptydreams.mi.api.gui.component.MComponent;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 控件包，支持自动排版
 * @author EmptyDreams
 */
public class Group extends MComponent implements Iterable<IComponent> {

	/** 包含的控件 */
	private final List<IComponent> components = new LinkedList<>();
	/** 对齐模式 */
	private Consumer<Group> mode;
	/** 两个控件间的最短距离(像素) */
	private int minDistance = 3;
	/** 两个控件间的最远距离(像素) */
	private int maxDistance = 10;

	public Group() {
		this(0, 0, 0, 0, null);
	}

	public Group(int x, int y, int width, int height, Consumer<Group> panel) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		mode = panel;
	}

	/**
	 * 移除一个组件
	 * @return 是否移除成功
	 */
	public boolean remove(IComponent component) {
		if (component == null) return false;
		return components.remove(component);
	}

	/**
	 * 添加一个组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(IComponent component) {
		WaitList.checkNull(component, "component");
		components.add(component);
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

	/** 获取两个控件间的最短距离 */
	public int getMinDistance() { return minDistance; }
	/** 设置两个控件间的最短距离 */
	public void setMinDistance(int minDistance) { this.minDistance = minDistance; }
	/** 获取两个控件间的最远距离 */
	public int getMaxDistance() { return maxDistance; }
	/** 设置两个控件间的最远距离 */
	public void setMaxDistance(int maxDistance) { this.maxDistance = maxDistance; }
	/** 获取排列模式 */
	public Consumer<Group> getArrangeMode() { return mode; }
	/** 设置排列模式，传入为空表示不自动进行排列 */
	public void setControlPanel(Consumer<Group> mode) { this.mode = mode; }
	/** 获取控件数量 */
	public int size() { return components.size(); }
	/** 遍历控件 */
	@Override
	public Iterator<IComponent> iterator() { return components.iterator(); }

	@Override
	public void onAddToGUI(Container con, EntityPlayer player) {
		if (mode != null && con instanceof IFrame) mode.accept(this);
		if (con instanceof MIFrame) {
			MIFrame frame = (MIFrame) con;
			components.forEach(it -> {
				frame.allocID(it);
				it.onAddToGUI(con, player);
			});
		} else components.forEach(it -> it.onAddToGUI(con, player));
	}

	@Override
	public void onAddToGUI(GuiContainer con, EntityPlayer player) {
		if (mode != null && con instanceof IFrame) mode.accept(this);
		components.forEach(it -> it.onAddToGUI(con, player));
	}

	@Override
	public void onRemoveFromGUI(Container con) {
		components.forEach(it -> it.onRemoveFromGUI(con));
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
	public void paint(@Nonnull Graphics g) {
		components.forEach(it -> paintHelper(g, it));
	}

	@SideOnly(Side.CLIENT)
	private void paintHelper(Graphics g, IComponent component) {
		component.paint(g.create(component.getX() - getX(), component.getY() - getY(),
				component.getWidth(), component.getHeight()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void realTimePaint(GuiContainer gui) {
		components.forEach(it -> it.realTimePaint(gui));
	}

	@Override
	public String toString() {
		return "Group{控件数量：" + size() +
				"；坐标：(" + getX() + "," + getY() + ")；" +
				"大小：(" + getWidth() + "," + getHeight() + ")}";
	}

}
