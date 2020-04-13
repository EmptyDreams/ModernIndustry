package xyz.emptydreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.net.WaitList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MProgressBar extends MComponent {
	
	private EnumStyle style;
	private int max;
	private int now;
	
	public MProgressBar() {
		this(0);
	}
	
	public MProgressBar(int max) {
		this(EnumStyle.ARROW, max);
	}
	
	public MProgressBar(EnumStyle style, int max) {
		WaitList.checkNull(style, "style");
		this.style = style;
		setSize(style.getWidth(), style.getHeight());
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.PROGRESS_BAR.getSubimage(
				style.getX(), style.getY(), style.getWidth(), style.getHeight()), 0, 0, null);
	}
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			ModernIndustry.MODID, "textures/gui/progressbar.png");
	
	@Override
	public void realTimePaint(GuiContainer gui) {
		int offsetX = (gui.width - gui.getXSize()) / 2, offsetY = (gui.height - gui.getYSize()) / 2;
		int x = offsetX + getX(), y = offsetY + getY();
		double d = now / (double) max;
		gui.mc.getTextureManager().bindTexture(TEXTURE);
		gui.drawTexturedModalRect(x, y, 0, 166,
				Math.min((int) (style.getWidth() * d), style.getWidth()), style.getHeight());
	}
	
	public int get() { return now; }
	public void set(int now) { this.now = now; }
	public void setPer(double per) { now = (int) (max * per); }
	public void setMax(int max) { this.max = max; }
	public void setStyle(EnumStyle style) {
		this.style = style;
		setSize(style.getWidth(), style.getHeight());
	}
	
	@Override
	public void send(Container con, IContainerListener listener) {
		listener.sendWindowProperty(con, getCode(), get());
		listener.sendWindowProperty(con, getCode() + 1, max);
	}
	
	@Override
	public boolean update(int codeID, int data) {
		if (codeID == getCode()) {
			set(data);
			return true;
		} else if (codeID == getCode() + 1) {
			setMax(data);
			return true;
		}
		return false;
	}
	
	public enum EnumStyle {
		
		ARROW(0, 0, 10, 15, 22, 15),
		STRIPE(22, 0, 22, 4, 89, 4),
		ARROW_BIG(22, 8, 22, 27, 68, 19);
		
		private int x, y, x2, y2, width, height;
		
		EnumStyle(int x, int y, int x2, int y2, int width, int height) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.width = width;
			this.height = height;
		}
		
		public int getX() { return x; }
		public int getY() { return y; }
		public int getX2() { return x2; }
		public int getY2() { return y2; }
		public int getWidth() { return width; }
		public int getHeight() { return height; }
		
	}
	
}
