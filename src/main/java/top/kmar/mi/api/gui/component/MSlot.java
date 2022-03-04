package top.kmar.mi.api.gui.component;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.gui.component.interfaces.IComponentManager;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.gui.client.GuiPainter;
import top.kmar.mi.api.gui.client.ImageData;
import top.kmar.mi.api.gui.client.RuntimeTexture;

import static top.kmar.mi.api.gui.client.ImageData.SLOT;

/**
 * 物品框
 * @author EmptyDreams
 */
public class MSlot extends MComponent {
	
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
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		RuntimeTexture texture = ImageData.createTexture(SLOT, getWidth(), getHeight(), createTextureName());
		texture.bindTexture();
		painter.drawTexture(0, 0, getWidth(), getHeight(), texture);
	}
	
	@Override
	public void onAdd2Manager(IComponentManager con) {
		StringUtil.checkNull(getSlot(), "slot");
		int xSum = 0;
		int ySum = 0;
		IComponentManager now = con;
		do {
			xSum += now.getX();
			ySum += now.getY();
			now = now.getSuperManager();
		} while (!now.isFrame());
		getSlot().xPos += xSum;
		getSlot().yPos += ySum;
		con.addSlotToContainer(slot);
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