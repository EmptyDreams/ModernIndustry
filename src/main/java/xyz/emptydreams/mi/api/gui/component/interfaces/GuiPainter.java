package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;

import javax.annotation.Nonnull;

/**
 * GUI画笔
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class GuiPainter {
	
	private final GuiContainer gui;
	private final int xOffset, yOffset;
	private final int maxWidth, maxHeight;
	
	public GuiPainter(GuiContainer gui) {
		this(gui, 0, 0, gui.width, gui.height);
	}
	
	public GuiPainter(GuiContainer gui, int xOffset, int yOffset, int maxWidth, int maxHeight) {
		this.gui = gui;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}
	
	/**
	 * <p>将材质绘制到GUI中
	 * <p><b>注意：使用前需自行装载材质</b>
	 * @param x 在GUI中的横坐标
	 * @param y 在GUI中的纵坐标
	 * @param u 材质横坐标
	 * @param v 材质纵坐标
	 * @param width 要绘制的宽度
	 * @param height 要绘制的高度
	 * @param texture 要绘制的材质
	 */
	public void drawTexture(int x, int y, int u, int v, int width, int height, RuntimeTexture texture) {
		int realX = x + gui.getGuiLeft() + xOffset;
		int realY = y + gui.getGuiTop() + yOffset;
		if (realX + width > maxWidth) width = maxWidth - realX;
		if (realY + height > maxHeight) height = maxHeight - realY;
		texture.drawToFrame(realX, realY, u, v, width, height);
	}
	
	/**
	 * <p>将材质绘制到GUI中
	 * <p><b>注意：使用前需自行装载材质</b>
	 * @param x 在GUI中的横坐标
	 * @param y 在GUI中的纵坐标
	 * @param u 材质横坐标
	 * @param v 材质纵坐标
	 * @param width 要绘制的宽度
	 * @param height 要绘制的高度
	 * @param textureWidth 材质宽度
	 * @param textureHeight 材质高度
	 */
	public void drawTexture(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		int realX = x + gui.getGuiLeft() + xOffset;
		int realY = y + gui.getGuiTop() + yOffset;
		if (realX + width > maxWidth) width = maxWidth - realX;
		if (realY + height > maxHeight) height = maxHeight - realY;
		Gui.drawModalRectWithCustomSizedTexture(realX, realY, u, v, width, height, textureWidth, textureHeight);
	}
	
	/**
	 * <p>将材质绘制到GUI中
	 * <p><b>注意：使用前需自行装载材质</b>
	 * @param x 在GUI中的横坐标
	 * @param y 在GUI中的纵坐标
	 * @param u 材质横坐标
	 * @param v 材质纵坐标
	 * @param width 要绘制的宽度
	 * @param height 要绘制的高度
	 */
	public void drawTexture(int x, int y, int u, int v, int width, int height) {
		int realX = x + xOffset;
		int realY = y + yOffset;
		if (realX + width > maxWidth) width = maxWidth - realX;
		if (realY + height > maxHeight) height = maxHeight - realY;
		gui.drawTexturedModalRect(realX, realY, u, v, width, height);
	}
	
	/** 获取横坐标起点偏移量 */
	public int getXOffset() {
		return xOffset;
	}
	
	/** 获取纵坐标起点偏移量 */
	public int getYOffset() {
		return yOffset;
	}
	
	/** 获取可绘制区域宽度 */
	public int getMaxWidth() {
		return maxWidth;
	}
	
	/** 获取可绘制区域高度 */
	public int getMaxHeight() {
		return maxHeight;
	}
	
	/**
	 * <p>获取当前绘制的GUI对象。
	 * <p>不推荐使用该方法进行GUI图像绘制，因为可能导致无法和其它控件完全配合。
	 */
	@Nonnull
	public GuiContainer getGuiContainer() {
		return gui;
	}
	
}