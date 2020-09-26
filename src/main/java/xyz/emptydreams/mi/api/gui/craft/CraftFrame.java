package xyz.emptydreams.mi.api.gui.craft;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.craft.handle.CraftHandle;
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;

/**
 * 用于显示合成表
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CraftFrame extends MIFrame {

	/** 目标合成表 */
	private final CraftGuide craft;
	/** 大小缓存，用于判断合成表是否变化，虽然不准确但是基本不会出问题 */
	private int size = -1;
	/** 当前合成表下标 */
	private int index = -1;
	/** 对应的{@link CraftHandle.Node} */
	CraftHandle.Node node;
	
	public CraftFrame(CraftGuide craft) {
		this.craft = craft;
		int width = (craft.getShapeWidth() + craft.getProtectedWidth()) * 18
						+ CommonProgress.Style.ARROW.getWidth() + 15 * 4;
		int height = Math.max(craft.getProtectedHeight(), craft.getShapeHeight()) * 18 + 50;
		setSize(width, height);
		init();
	}

	/** 解析合成表 */
	private void init() {
		if (size == craft.size()) return;
		removeAllComponent();
		size = craft.size();
		index = -1;
		CraftHandle handle = HandleRegister.get(craft);
		node = handle.createGroup();
		CommonProgress progress = new CommonProgress();
		
		Group group = new Group(0, 10, getWidth(), getHeight() - 10, Panels::horizontalCenter);
		group.adds(node.raw, progress, node.pro);
		add(group, null);
		
		handle.update(node, craft.getShape(++index));
	}
	
	/** 强制重新初始化缓存 */
	public void reInit() {
		size = -1;
		init();
	}
	
	/** 切换到下一个合成表并刷新显示 */
	public void nextShape() {
		int now = ++index;
		if (now >= size) now = index = 0;
		CraftHandle handle = HandleRegister.get(craft);
		handle.update(node, craft.getShape(now));
	}
	
	/** 切换到上一个合成表并刷新显示 */
	public void preShape() {
		int now = --index;
		if (now < 0) now = index = size - 1;
		CraftHandle handle = HandleRegister.get(craft);
		handle.update(node, craft.getShape(now));
	}
	
	/** 获取当前显示的合成表 */
	public IShape getShape() {
		return craft.getShape(index);
	}
	
	/** 重新绘制当前合成表 */
	public void repaint() {
		CraftHandle handle = HandleRegister.get(craft);
		handle.update(node, getShape());
	}
	
}
