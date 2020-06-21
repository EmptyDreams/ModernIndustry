package xyz.emptydreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.utils.MISysInfo;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MBackpack extends MComponent {
	
	public static final String RESOUCE_NAME = "backpack";
	
	public MBackpack() {
		this(0, 0);
	}
	
	public MBackpack(int x, int y) {
		this.x = x;
		this.y = y;
		width = 162;
		height = 76;
	}
	
	@Override
	public void setSize(int width, int height) { }
	@Override
	public void setLocation(int x, int y) { }
	
	@Override
	public boolean hasSlot() {
		return true;
	}
	
	@Override
	public List<Slot> getSlots() {
		return SLOTS;
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOUCE_NAME), 0, 0, null);
	}
	
	private int startIndex = -1;
	private final List<Slot> SLOTS = new ArrayList<>(36);
	
	@Override
	public void onAddToGUI(Container gui, EntityPlayer player) {
		if (gui instanceof MIFrame) {
			MIFrame frame = (MIFrame) gui;
			Slot slot;
			startIndex = gui.inventorySlots.size();
			for (int i = 0; i < 3; ++i) {
				for (int k = 0; k < 9; ++k) {
					slot = new Slot(player.inventory,
							k + i * 9 + 9 + startIndex, getX() + k * 18 + 1, getY() + i * 18 + 1);
					frame.addSlotToContainer(slot);
					SLOTS.add(slot);
				}
			}
			for (int k = 0; k < 9; ++k) {
				slot = new Slot(player.inventory,
						k + startIndex, getX() + k * 18 + 1, getY() + 59);
				frame.addSlotToContainer(slot);
				SLOTS.add(slot);
			}
		} else {
			MISysInfo.err("MBackpack不支持：" + gui.getClass());
		}
	}
	
	public int getStartIndex() { return startIndex; }
	
	@Override
	public void onRemoveFromGUI(Container con) {
		if (startIndex + 4 * 9 >= startIndex) {
			con.inventorySlots.subList(startIndex, startIndex + 4 * 9 + 1).clear();
		}
	}
}
