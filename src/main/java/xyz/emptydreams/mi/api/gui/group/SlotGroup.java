package xyz.emptydreams.mi.api.gui.group;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.MComponent;
import xyz.emptydreams.mi.api.gui.component.MSlot;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * @author EmptyDreams
 */
public class SlotGroup extends MComponent {
	
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
	
	public void writeFrom(int start, IntFunction<SlotItemHandler> builder) {
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize(); ++x) {
				setSlot(x, y, builder.apply(start++));
			}
		}
	}
	
	/**
	 * 自动创建所有Slot
	 * @param handler 指定ItemStackHandler
	 * @param start 下标起点
	 * @param test 测试是否允许输入
	 */
	public void writeFrom(ItemStackHandler handler, TileEntity entity, int start, Predicate<ItemStack> test) {
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
	
	@Override
	public void onAddToGUI(Container con, EntityPlayer player) {
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize(); ++x) {
				getSlot(x, y).xPos = getX() + (getSlotSize() * x) + (getInterval() * x) + 1;
				getSlot(x, y).yPos = getY() + (getSlotSize() * y) + (getInterval() * y) + 1;
				((MIFrame) con).addSlotToContainer(getSlot(x, y));
			}
		}
	}
	
	@Override
	public void onAddToGUI(GuiContainer con, EntityPlayer player) {
		for (int y = 0; y < getYSize(); ++y) {
			for (int x = 0; x < getXSize(); ++x) {
				getSlot(x, y).xPos = getX() + (getSlotSize() * x) + (getInterval() * x) + 1;
				getSlot(x, y).yPos = getY() + (getSlotSize() * y) + (getInterval() * y) + 1;
			}
		}
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		paintGroup(g, 0, 0, getSlotSize(), getXSize(), getYSize(), getInterval());
	}
	
	/**
	 * 绘制图像
	 * @param g 画笔
	 * @param offsetX X轴偏移量
	 * @param offsetY Y轴偏移量
	 * @param slotSize 单个Slot的大小
	 * @param xSize X轴方向Slot数量
	 * @param ySize Y轴方向Slot数量
	 * @param interval Slot之间的间隔
	 */
	public static void paintGroup(Graphics g, int offsetX, int offsetY,
	                              int slotSize, int xSize, int ySize, int interval) {
		Image image = ImageData.getImage(MSlot.RESOURCE_NAME, slotSize, slotSize);
		for (int y = 0; y < ySize; ++y) {
			for (int x = 0; x < xSize; ++x) {
				int drawX = (slotSize * x) + (interval * x) + offsetX;
				int drawY = (slotSize * y) + (interval * y) + offsetY;
				g.drawImage(image, drawX, drawY, null);
			}
		}
	}
	
}
