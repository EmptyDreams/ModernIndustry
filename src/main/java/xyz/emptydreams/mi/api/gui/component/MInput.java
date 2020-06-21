package xyz.emptydreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.MISysInfo;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MInput extends MComponent {
	
	public static final String RESOUCE_NAME = "slot";
	
	private Slot slot;
	private int xOffset, yOffset;
	
	public MInput() {
		this(null);
	}
	
	public MInput(Slot slot) {
		this(slot, 1, 1);
	}
	
	public MInput(Slot slot, int xOffset, int yOffset) {
		width = 18;
		height = 18;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		if (slot != null) {
			this.slot = slot;
			x = slot.xPos - xOffset;
			y = slot.yPos - yOffset;
		}
	}
	
	public void setSlot(Slot slot) {
		setSlot(slot, xOffset, yOffset);
	}
	
	public void setSlot(Slot slot, int xOffset, int yOffset) {
		WaitList.checkNull(slot, "slot");
		this.slot = slot;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		x = slot.xPos - xOffset;
		y = slot.yPos - yOffset;
	}
	
	public Slot getSlot() { return slot; }
	
	@Override
	public void setSize(int width, int height) { }
	@Override
	public void setLocation(int x, int y) { }
	@Override
	public boolean hasSlot() { return true; }
	@Override
	public List<Slot> getSlots() { return Lists.newArrayList(slot); }
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOUCE_NAME, getWidth(), getHeight()), 0, 0, null);
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
