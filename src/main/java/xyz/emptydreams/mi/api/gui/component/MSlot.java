package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.MISysInfo;

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
		WaitList.checkNull(getSlot(), "slot");
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
	
}
