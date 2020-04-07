package minedreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;

import minedreams.mi.api.gui.MIFrame;
import minedreams.mi.tools.MISysInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MBackpack extends MComponent {
	
	public MBackpack() {
		this(0, 0);
	}
	
	public MBackpack(int x, int y) {
		this.x = x;
		this.y = y;
		width = ImageData.BACKPACK.getWidth(null);
		height = ImageData.BACKPACK.getHeight(null);
	}
	
	@Override
	public void setSize(int width, int height) { }
	@Override
	public void setLocation(int x, int y) { }
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.BACKPACK, 0, 0, null);
	}
	
	private int startIndex = -1;
	
	@Override
	public void onAddToGUI(Container gui, EntityPlayer player) {
		if (gui instanceof MIFrame) {
			MIFrame frame = (MIFrame) gui;
			startIndex = gui.inventorySlots.size();
			for (int i = 0; i < 3; ++i) {
				for (int k = 0; k < 9; ++k) {
					frame.addSlotToContainer(new Slot(
							player.inventory, k + i * 9 + 9, getX() + k * 18 + 1, getY() + i * 18 + 1));
				}
			}
			for (int k = 0; k < 9; ++k) {
				frame.addSlotToContainer(new Slot(player.inventory, k, getX() + k * 18 + 1, getY() + 59));
			}
		} else {
			MISysInfo.err("MBackpack不支持：" + gui.getClass());
		}
	}
	
	@Override
	public void onRemoveFromGUI(Container con) {
		if (startIndex + 4 * 9 >= startIndex) {
			con.inventorySlots.subList(startIndex, startIndex + 4 * 9 + 1).clear();
		}
	}
}
