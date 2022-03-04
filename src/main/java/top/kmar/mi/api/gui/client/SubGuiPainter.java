package top.kmar.mi.api.gui.client;

import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * @author EmptyDreams
 */
public class SubGuiPainter extends GuiPainter {
	
	public SubGuiPainter(GuiContainer gui, int x, int y, int xOffset, int yOffset,
	                     int width, int height, GuiPainter superPainter) {
		super(gui,
				calculateX(superPainter, x, width),
				calculateY(superPainter, y, height),
				xOffset, yOffset,
				calculateWidth(superPainter, x, width),
				calculateHeight(superPainter, y, height));
	}
	
	private static int calculateHeight(GuiPainter superPainter, int y, int height) {
		int superLocation = superPainter.getY() + superPainter.getHeight();
		int location = y + height;
		return location < superLocation ? superLocation - location : location - superLocation;
	}
	
	private static int calculateWidth(GuiPainter superPainter, int x, int width) {
		int superLocation = superPainter.getX() + superPainter.getWidth();
		int location = x + width;
		return location < superLocation ? superLocation - location : location - superLocation;
	}
	
	private static int calculateY(GuiPainter superPainter, int y, int height) {
		if (y + height < superPainter.getY() + superPainter.getHeight()) {
			return superPainter.getY();
		} else {
			return y;
		}
	}
	
	private static int calculateX(GuiPainter superPainter, int x, int width) {
		if (x + width < superPainter.getX() + superPainter.getWidth()) {
			return superPainter.getX();
		} else {
			return x;
		}
	}
	
}