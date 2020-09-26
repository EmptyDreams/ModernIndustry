package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * 物品框
 * @author EmptyDreams
 */
public class MSlot extends MComponent {
	
	public static final String RESOURCE_NAME = "slot";
	
	private Slot slot;
	
	public MSlot() {
		this(null);
	}
	
	public MSlot(Slot slot) {
		this(slot, 1, 1);
	}
	
	public MSlot(Slot slot, int xOffset, int yOffset) {
		width = 18;
		height = 18;
		if (slot != null) {
			this.slot = slot;
			x = slot.xPos - xOffset;
			y = slot.yPos - yOffset;
		}
	}
	
	public Slot getSlot() { return slot; }

	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		slot.xPos = x + 1;
		slot.yPos = y + 1;
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOURCE_NAME, getWidth(), getHeight()), 0, 0, null);
	}
	
	private int index = -1;
	
	@Override
	public void onAddToGUI(Container con, EntityPlayer player) {
		StringUtil.checkNull(getSlot(), "slot");
		if (con instanceof MIFrame) {
			index = con.inventorySlots.size();
			((MIFrame) con).addSlotToContainer(slot);
		} else {
			MISysInfo.err("MBackpack不支持：" + con.getClass());
		}
	}

	@Override
	public void onRemoveFromGUI(Container con) {
		con.inventorySlots.remove(index);
	}
	
	/**
	 * 可以自动调用{@link TileEntity#markDirty()}的SlotItemHandler
	 */
	public static class SlotHandler extends SlotItemHandler {
		
		private final TileEntity entity;
		
		public SlotHandler(IItemHandler itemHandler, TileEntity entity, int index) {
			super(itemHandler, index, 0, 0);
			this.entity = entity;
		}
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			ItemStack stack = getStack();
			if (entity == null) return stack.getCount() < stack.getMaxStackSize();
			return stack.getCount() < stack.getMaxStackSize() &&
					playerIn.canPlayerEdit(entity.getPos(), playerIn.getHorizontalFacing(), stack);
		}
		
		@Override
		public void onSlotChanged() {
			super.onSlotChanged();
			if (entity != null) entity.markDirty();
		}
	}
	
}
