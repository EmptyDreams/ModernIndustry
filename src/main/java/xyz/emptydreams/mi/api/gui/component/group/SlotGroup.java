package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.component.MComponent;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponentManager;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static xyz.emptydreams.mi.api.gui.client.ImageData.createTexture;

/**
 * @author EmptyDreams
 */
public class SlotGroup extends MComponent implements Iterable<SlotGroup.Node> {
	
	private final int width, height;
	private final SlotItemHandler[][] slots;
	private final int size;
	private final int interval;
	
	/**
	 * 根据宽度和高度构建一个Slot列表
	 * @param width 横向的数量
	 * @param height 纵向的数量
	 * @param size 每个Slot的大小
	 * @param interval 每个Slot之间的间隔
	 */
	public SlotGroup(int width, int height, int size, int interval) {
		slots = new SlotItemHandler[height][width];
		this.width= width;
		this.height = height;
		this.size = size;
		this.interval = interval;
		setSize(width * (size + interval), height * (size + interval));
	}
	
	/**
	 * 从指定位置开始创建SlotItemHandler
	 * @param start 起始位置（包括）
	 * @param builder 构建器
	 */
	public void writeFrom(int start, IntFunction<SlotItemHandler> builder) {
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize(); ++x) {
				setSlot(x, y, builder.apply(start++));
			}
		}
	}
	
	/**
	 * 自动创建指定数量的Slot
	 * @param start 下标起点
	 * @param size 创建数量
	 * @param builder 构建一个新的SlotItemHandler
	 */
	public void writeFromBuilder(int start, int size, Function<Integer, SlotItemHandler> builder) {
		int nowSize = 0;
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize() && nowSize <= size; ++x, ++nowSize) {
				setSlot(x, y, builder.apply(start++));
			}
		}
	}
	
	/**
	 * 自动创建指定数量的Slot
	 * @param handler 指定ItemStackHandler
	 * @param start 下标起点
	 * @param size 创建数量
	 * @param test 测试是否允许输入
	 */
	public void writeFrom(ItemStackHandler handler, TileEntity entity,
	                        int start, int size, Predicate<ItemStack> test) {
		int nowSize = 0;
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize() && nowSize <= size; ++x, ++nowSize) {
				setSlot(x, y, new MSlot.SlotHandler(handler, entity, start++) {
					@Override
					public boolean isItemValid(@Nonnull ItemStack stack) {
						return test.test(stack) && super.isItemValid(stack);
					}
				});
			}
		}
	}
	
	/**
	 * 自动创建所有Slot
	 * @param handler 指定ItemStackHandler
	 * @param start 下标起点
	 * @param test 测试是否允许输入
	 */
	public void writeFrom(ItemStackHandler handler, TileEntity entity,
	                        int start, Predicate<ItemStack> test) {
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize(); ++x) {
				setSlot(x, y, new MSlot.SlotHandler(handler, entity, start++) {
					@Override
					public boolean isItemValid(@Nonnull ItemStack stack) {
						return test.test(stack) && super.isItemValid(stack);
					}
				});
			}
		}
	}
	
	/**
	 * 为指定位置设置Slot
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @param slot Slot
	 * @throws IndexOutOfBoundsException 如果x>={@link #getXSize()} ()}或y>={@link #getYSize()}
	 */
	public void setSlot(int x, int y, SlotItemHandler slot) {
		slots[y][x] = slot;
	}
	
	/** 设置指定位置的物品 */
	public void setItem(int x, int y, ItemStack stack) {
		getSlot(x, y).putStack(stack);
	}
	
	/** 设置指定位置的物品 */
	public void setItem(int x, int y, ItemElement element) {
		getSlot(x, y).putStack(element.getStack());
	}
	
	/**
	 * 获取指定位置的Slot
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @throws IndexOutOfBoundsException 如果x>={@link #getXSize()} ()}或y>={@link #getYSize()}
	 */
	public SlotItemHandler getSlot(int x, int y) {
		return slots[y][x];
	}
	
	/** 获取横向Slot的数量 */
	public int getXSize() {
		return width;
	}
	
	/** 获取纵向Slot的数量 */
	public int getYSize() {
		return height;
	}
	
	/** 获取单个Slot的大小 */
	public int getSlotSize() {
		return size;
	}
	
	/** 获取Slot之间的间隔 */
	public int getInterval() {
		return interval;
	}
	
	/** 是否为空 */
	public boolean isEmpty() {
		for (Node node : this) {
			if (!node.get().getStack().isEmpty()) return false;
		}
		return true;
	}
	
	/** 清空所有物品 */
	public void clear() {
		for (Node node : this) {
			node.get().putStack(ItemStack.EMPTY);
		}
	}
	
	@Override
	public void onAdd2Manager(IComponentManager manager, EntityPlayer player) {
		super.onAdd2Manager(manager, player);
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize(); ++x) {
				getSlot(x, y).xPos = getX() + (getSlotSize() * x) + (getInterval() * x) + 1 + manager.getX();
				getSlot(x, y).yPos = getY() + (getSlotSize() * y) + (getInterval() * y) + 1 + manager.getY();
				manager.addSlotToContainer(getSlot(x, y));
			}
		}
	}
	
	@Override
	public void paint(GuiPainter painter) {
		RuntimeTexture texture = createTexture(ImageData.SLOT, getSlotSize(), getSlotSize(), createTextureName());
		texture.bindTexture();
		for (int y = 0; y < getSlotSize(); ++y) {
			for (int x = 0; x < getSlotSize(); ++x) {
				int drawX = (getSlotSize() * x) + (interval * x);
				int drawY = (getSlotSize() * y) + (interval * y);
				painter.drawTexture(drawX, drawY, getSlotSize(), getSlotSize(), texture);
			}
		}
	}
	
	@Override
	public IComponent getMouseTarget(float mouseX, float mouseY) {
		return null;
	}
	
	@Override
	public Iterator<Node> iterator() {
		return new NodeIterator();
	}
	
	private final class NodeIterator implements Iterator<Node> {
		
		int x = -1, y = 0;
		
		@Override
		public boolean hasNext() {
			return x < getXSize() - 1 || y < getYSize() - 1;
		}
		
		@Override
		public Node next() {
			if (x >= getXSize() - 1) {
				x = -1;
				++y;
			}
			return new Node(SlotGroup.this, ++x, y);
		}
		
	}
	
	public static final class Node {
		
		private final int x;
		private final int y;
		private final SlotItemHandler slot;
		
		Node(SlotGroup slots, int x, int y) {
			this.x = x;
			this.y = y;
			slot = slots.getSlot(x, y);
		}
		
		/** 获取X轴坐标 */
		public int getX() { return x; }
		/** 获取Y轴坐标 */
		public int getY() { return y; }
		/** 获取当前坐标对应的SlotItemHandler */
		public SlotItemHandler get() { return slot; }
		
	}
	
}