package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
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
	private final int x, y;
	
	/** 绘制区域在裁剪时的X坐标 */
	private final int realX;
	/** 绘制区域在裁剪时的Y坐标 */
	private final int realY;
	/** 绘制区域在裁剪时的宽度 */
	private final int realMaxWidth;
	/** 绘制区域在裁剪时的高度 */
	private final int realMaxHeight;
	
	
	public GuiPainter(GuiContainer gui, int x, int y) {
		this(gui, x, y, 0, 0, gui.width, gui.height);
	}
	
	/**
	 * 在特定位置创建一个有限画板
	 * @param gui 当前被打开的GUI
	 * @param x 区域X轴坐标
	 * @param y 区域Y轴坐标
	 * @param xOffset 绘制起点X轴偏移量（右正左负）
	 * @param yOffset 绘制起点Y轴偏移量（下正上负）
	 * @param maxWidth 绘制区域宽度
	 * @param maxHeight 绘制区域高度
	 */
	public GuiPainter(GuiContainer gui, int x, int y, int xOffset, int yOffset, int maxWidth, int maxHeight) {
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.xOffset = xOffset + gui.getGuiLeft();
		this.yOffset = yOffset + gui.getGuiTop();
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		float scaleViewX = Minecraft.getMinecraft().displayWidth / (float) gui.width;
		float scaleViewY = Minecraft.getMinecraft().displayHeight / (float) gui.height;
		realX = (int) ((getX() + getGuiContainer().getGuiLeft()) * scaleViewX);
		realY = (int) ((getY() + getGuiContainer().getGuiTop()) * scaleViewY);
		realMaxWidth = (int) (this.maxWidth * scaleViewX);
		realMaxHeight = (int) (this.maxHeight * scaleViewY);
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
		int realX = x + xOffset;
		int realY = y + yOffset;
		scissor();
		texture.drawToFrame(realX, realY, u, v, width, height);
		unscissor();
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
	public void drawTexture(int x, int y, int u, int v,
	                        int width, int height, int textureWidth, int textureHeight) {
		int realX = x + xOffset;
		int realY = y + yOffset;
		scissor();
		Gui.drawModalRectWithCustomSizedTexture(realX, realY, u, v, width, height, textureWidth, textureHeight);
		unscissor();
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
		scissor();
		gui.drawTexturedModalRect(realX, realY, u, v, width, height);
		unscissor();
	}
	
	public void drawString(int x, int y, String text, int color) {
		int guiX = x + xOffset;
		int guiY = y + yOffset;
		scissor();
		Minecraft.getMinecraft().fontRenderer.drawString(text, guiX, guiY, color);
		unscissor();
	}
	
	private void scissor() {
		GL11.glScissor(realX, Minecraft.getMinecraft().displayHeight - maxHeight - realY, realMaxWidth, realMaxHeight);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
	
	private void unscissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
	
	/** 获取绘制区域的X轴坐标 */
	public int getX() {
		return x;
	}
	
	/** 获取绘制区域的Y轴坐标 */
	public int getY() {
		return y;
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