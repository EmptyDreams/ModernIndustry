package xyz.emptydreams.mi.api.gui.craft;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.ButtonComponent;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.craft.handle.CraftHandle;
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import java.util.Map;

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
	private CraftHandle.Node node;
	/** 存储打开GUI前的窗体 */
	private final Container preGui;
	
	public CraftFrame(CraftGuide craft, EntityPlayer player) {
		super(craft.getName() + ".gui");
		this.craft = craft;
		int width = (craft.getShapeWidth() + craft.getProtectedWidth()) * 18
						+ CommonProgress.Style.ARROW.getWidth() + 15 * 4;
		int height = Math.max(craft.getProtectedHeight(), craft.getShapeHeight()) * 18 + 50;
		setSize(width, height);
		init();
		this.preGui = player.openContainer;
	}

	/** 解析合成表 */
	private void init() {
		if (size == craft.size()) return;
		removeAllComponent();
		size = craft.size();
		index = -1;
		CraftHandle handle = HandleRegister.get(craft);
		if (handle == null) {
			MISysInfo.err("[CraftFrame]合成表显示暂时不支持该类型的合成表：" + craft.getClass().getName());
			return;
		}
		node = handle.createGroup();
		CommonProgress progress = new CommonProgress();
		
		Group group = new Group(0, 20, getWidth(), 0, Panels::horizontalCenter);
		group.adds(node.raw, progress, node.pro);
		add(group, null);
		
		Group buttonGroup = new Group(0, group.getHeight() + group.getY() + 7,
								getWidth(), 0, Panels::horizontalCenter);
		ButtonComponent prevButton = new ButtonComponent(10, 10);
		ButtonComponent nextButton = new ButtonComponent(10, 10);
		prevButton.setAction((frame, isClient) -> preShape());
		nextButton.setAction((frame, isClient) -> nextShape());
		buttonGroup.adds(prevButton, nextButton);
		add(buttonGroup, null);
		
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
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (preGui.getClass() == getClass() || preGui.getClass() == ContainerPlayer.class
				|| preGui instanceof CraftFrame) return;
		//Node node = MAP.get(playerIn);
		//playerIn.openGui(node.mod, preGui.windowId, getWorld(), node.x, node.y, node.z);
	}
	
	private static final Map<EntityPlayer, Node> MAP = new Object2ObjectArrayMap<>();
	
	/**
	 * 在玩家打开GUI时记录信息
	 * @deprecated 内部方法，请勿调用
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static void onOpenGui(EntityPlayer player, Object mod, int x, int y, int z) {
		MAP.put(player, new Node(mod, x, y, z));
	}
	
	/**
	 * 在玩家关闭GUI时记录信息
	 */
	@Deprecated
	public static void onCloseGui(EntityPlayer player) {
		MAP.remove(player);
	}
	
	private static final class Node {
		
		Object mod;
		int x, y, z;
		
		Node(Object mod, int x, int y, int z) {
			this.mod = mod;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
	}
	
}
