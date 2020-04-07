package minedreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;

import minedreams.mi.api.gui.MIFrame;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.tools.MISysInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MOutput extends MComponent {
	
	private Slot slot;
	private int xOffset, yOffset;
	
	public MOutput() {
		this(null);
	}
	
	public MOutput(Slot slot) {
		this(slot, 5, 5);
	}
	
	public MOutput(Slot slot, int xOffset, int yOffset) {
		width = 26;
		height = 26;
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
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.OUTPUT, 0, 0, null);
	}
	
	private int index = -1;
	
	@Override
	public void onAddToGUI(net.minecraft.inventory.Container con, EntityPlayer player) {
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
