package xyz.emptydreams.mi.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.gui.component.IComponent;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.net.WaitList;

import java.util.LinkedList;
import java.util.List;

/**
 * MI版本窗体，通过该类可以便捷的创建和控制UI界面
 * @author EmptyDreams
 */
public class MIFrame extends Container implements IFrame {
	
	/** 存储窗体的尺寸，可以更改 */
	private int width, height;
	/** 所在世界 */
	private World world;
	/** 是否包含玩家背包 */
	private final boolean hasBackpack;
	/** 玩家背包的位置 */
	private final int backpackX, backpackY;
	
	public MIFrame(int width, int height) {
		this.width = width;
		this.height = height;
		hasBackpack = false;
		backpackX = backpackY = Integer.MIN_VALUE;
	}
	
	/**
	 * 通过该构造函数创建一个指定尺寸的UI并且包含玩家背包
	 * @param width 宽度
	 * @param height 高度
	 * @param player 玩家对象
	 */
	public MIFrame(int width, int height, EntityPlayer player, int backpackX, int backpackY) {
		this.width = width;
		this.height = height;
		this.backpackX = backpackX;
		this.backpackY = backpackY;
		if (player != null) {
			hasBackpack = true;
			Slot slot;
			for (int i = 0; i < 3; ++i) {
				for (int k = 0; k < 9; ++k) {
					slot = new Slot(player.inventory, k + i * 9 + 9,
							backpackX + k * 18 + 1, backpackY + i * 18 + 1);
					addSlotToContainer(slot);
				}
			}
			for (int k = 0; k < 9; ++k) {
				slot = new Slot(player.inventory,
						k, backpackX + k * 18 + 1, backpackY + 59);
				addSlotToContainer(slot);
			}
			StringComponent shower = new StringComponent();
			shower.setLocation(backpackX, backpackY - 10);
			add(shower, player);
		} else hasBackpack = false;
	}
	
	/**
	 * 初始化内部数据
	 * @param world 当前世界
	 *
	 * @throws NullPointerException 如果world == null || pos == null
	 * @throws IllegalArgumentException 如果第二次调用该函数
	 */
	@Override
	public void init(World world) {
		WaitList.checkNull(world, "world");
		this.world = world;
	}
	
	/**
	 * 重新设置UI大小
	 * @param width 宽度
	 * @param height 高度
	 *
	 * @throws IllegalArgumentException 如果width <= 0 || height <= 0
	 */
	public void setSize(int width, int height) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("width/height应该大于0，而这里为[" + width + "/" + height + "]");
		this.width = width;
		this.height = height;
	}
	
	/** 获取宽度 */
	@Override
	public int getWidth() { return width; }
	/** 获取高度 */
	@Override
	public int getHeight() { return height; }
	/** 获取所在世界 */
	public World getWorld() { return world; }
	/** 是否包含背包 */
	public boolean hasBackpack() { return hasBackpack; }
	/** 获取背包坐标 */
	public int getBackpackX() { return backpackX; }
	/** 获取背包坐标 */
	public int getBackpackY() { return backpackY; }
	
	/**
	 * 判断玩家是否可以打开UI，默认返回true
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public Slot addSlotToContainer(Slot slotIn) {
		return super.addSlotToContainer(slotIn);
	}
	
	/** shift+左键 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = getSlot(index);                 //被点击的Slot
		ItemStack stack = slot.getStack();          //原有的ItemStack
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (index < 0) throw new IllegalArgumentException("传入的index<0 [" + index + "]");
		ItemStack oldStack = stack.copy();          //拷贝的ItemStack
		
		if (hasBackpack()) {
			if (index < 27) {
				boolean success = mergeItemStack(stack, 36,
						inventoryItemStacks.size(), false);
				if (success) return oldStack;
				success = mergeItemStack(stack, 27, 36, true);
				return success ? oldStack : ItemStack.EMPTY;
			} else if (index < 36) {
				boolean success = mergeItemStack(stack, 36,
						inventoryItemStacks.size(), false);
				if (success) return oldStack;
				success = mergeItemStack(stack, 0, 27, false);
				return success ? oldStack : ItemStack.EMPTY;
			} else {
				boolean success = mergeItemStack(stack, 0, 36, true);
				return success ? oldStack : ItemStack.EMPTY;
			}
		}
		boolean success = mergeItemStack(stack, 0, index, false);
		if (success) return oldStack;
		success = mergeItemStack(stack, index + 1, inventoryItemStacks.size(), false);
		return success ? oldStack : ItemStack.EMPTY;
	}
	
	//--------------------关于组件的操作--------------------//
	
	/** 保存组件 */
	private final List<IComponent> components = new LinkedList<>();
	
	/**
	 * 移除一个组件
	 * @param component 要移除的组件
	 */
	public void remove(IComponent component) {
		components.remove(component);
		component.onRemoveFromGUI(this);
	}
	
	private int codeStart = 0;
	/**
	 * 添加一个组件
	 *
	 * @param component 要添加的组件
	 * @throws NullPointerException 如果component == null
	 */
	public void add(IComponent component, EntityPlayer player) {
		WaitList.checkNull(component, "component");
		components.add(component);
		component.onAddToGUI(this, player);
	}

	public void allocID(IComponent component) {
		component.setCodeStart(codeStart);
		codeStart += 100;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			components.forEach(it -> it.send(this, listener));
		}
	}
	
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		for (IComponent component : components) {
			if (component.update(id, data)) break;
		}
	}
}
