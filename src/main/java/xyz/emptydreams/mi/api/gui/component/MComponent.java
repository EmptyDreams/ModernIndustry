package xyz.emptydreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class MComponent implements IComponent {
	
	protected int x, y, width, height;
	private int code;
	
	@Override
	public String getString() { return null; }
	@Override
	public boolean isString() { return false; }
	@Override
	public int getStringColor() { return 0; }
	@Override
	public void setStringColor(int color) { }
	@Override
	public void setString(String str) { }
	
	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void setSize(int width, int height) {
		if (width < 0) throw new IllegalArgumentException("width[" + width + "] < 0");
		if (height < 0) throw new IllegalArgumentException("height[" + height + "] < 0");
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getY() { return y; }
	@Override
	public int getX() { return x; }
	@Override
	public int getHeight() { return height; }
	@Override
	public int getWidth() { return width; }
	@Override
	public boolean hasSlot() { return false; }
	@Override
	public List<Slot> getSlots() { return null; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public abstract void paint(@Nonnull Graphics g);
	
	@Override
	public void onAddToGUI(Container con, EntityPlayer player) { }
	
	@Override
	public void onRemoveFromGUI(Container con) { }
	
	@Override
	public int getCode() {
		return code;
	}
	
	@Override
	public void setCodeStart(int code) {
		this.code = code;
	}
}
