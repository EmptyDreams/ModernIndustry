package xyz.emptydreams.mi.api.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;

import javax.annotation.Nonnull;

/**
 * GUI画笔
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public class GuiPainter {
	
	protected final GuiContainer gui;
	protected final int xOffset, yOffset;
	protected final int width, height;
	protected final int x, y;
	
	/** 绘制区域在裁剪时的X坐标 */
	protected int realX;
	/** 绘制区域在裁剪时的Y坐标 */
	protected int realY;
	/** 绘制区域在裁剪时的宽度 */
	protected int realWidth;
	/** 绘制区域在裁剪时的高度 */
	protected int realHeight;
	
	/**
	 * 在特定位置创建一个有限画板
	 * @param gui 当前被打开的GUI
	 * @param x 区域X轴坐标
	 * @param y 区域Y轴坐标
	 * @param width 绘制区域宽度
	 * @param height 绘制区域高度
	 */
	public GuiPainter(GuiContainer gui, int x, int y, int width, int height) {
		this(gui, x, y, 0, 0, width, height);
	}
	
	/**
	 * 在特定位置创建一个有限画板
	 * @param gui 当前被打开的GUI
	 * @param x 区域X轴坐标
	 * @param y 区域Y轴坐标
	 * @param xOffset 渲染时X轴偏移量
	 * @param yOffset 渲染时Y轴偏移量
	 * @param width 绘制区域宽度
	 * @param height 绘制区域高度
	 */
	public GuiPainter(GuiContainer gui, int x, int y, int xOffset, int yOffset, int width, int height) {
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = Math.min(width, gui.getXSize() - x);
		this.height = Math.min(height, gui.getYSize() - y);
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution res = new ScaledResolution(mc);
		double scaleViewX = mc.displayWidth / res.getScaledWidth_double();
		double scaleViewY = mc.displayHeight / res.getScaledHeight_double();
		realX = (int) ((x + getGuiContainer().getGuiLeft()) * scaleViewX);
		realY = (int) (mc.displayHeight - ((y + getGuiContainer().getGuiTop() + height) * scaleViewY));
		realWidth = width > 0 ? (int) (this.width * scaleViewX) : -1;
		realHeight = height > 0 ? (int) (this.height * scaleViewY) : -1;
	}
	
	/**
	 * <p>将材质绘制到GUI中
	 * <p><b>注意：使用前需自行装载材质</b>
	 * @param x 在画板中的横坐标
	 * @param y 在画板中的纵坐标
	 * @param u 材质横坐标
	 * @param v 材质纵坐标
	 * @param width 要绘制的宽度
	 * @param height 要绘制的高度
	 * @param texture 要绘制的材质
	 */
	public void drawTexture(int x, int y, int u, int v, int width, int height, RuntimeTexture texture) {
		int realX = x + getXOffset() + this.x;
		int realY = y + getYOffset() + this.y;
		if (scissor()) return;
		texture.drawToFrame(realX, realY, u, v, width, height);
		unscissor();
	}
	
	/**
	 * <p>将材质绘制到GUI中
	 * <p><b>注意：使用前需自行装载材质</b>
	 * @param x 在画板中的横坐标
	 * @param y 在画板中的纵坐标
	 * @param width 要绘制的宽度
	 * @param height 要绘制的高度
	 * @param texture 要绘制的材质
	 */
	public void drawTexture(int x, int y, int width, int height, RuntimeTexture texture) {
		drawTexture(x, y, 0, 0, width, height, texture);
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
		int realX = x + getXOffset() + this.x;
		int realY = y + getYOffset() + this.y;
		if (scissor()) return;
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
		int realX = x + getXOffset() + this.x;
		int realY = y + getYOffset() + this.y;
		if (scissor()) return;
		gui.drawTexturedModalRect(realX, realY, u, v, width, height);
		unscissor();
	}
	
	public void drawString(int x, int y, String text, int color) {
		int guiX = x + getXOffset() + this.x;
		int guiY = y + getYOffset() + this.y;
		if (scissor()) return;
		Minecraft.getMinecraft().fontRenderer.drawString(text, guiX, guiY, color);
		unscissor();
	}
	
	/** 绘制一个控件 */
	public void paintComponent(IComponent component) {
		component.paint(createPainterForComponent(component));
	}
	
	/** 根据控件大小、坐标创建一个子画笔 */
	public GuiPainter createPainterForComponent(IComponent component) {
		return createPainter(component.getX(), component.getY(), component.getWidth(), component.getHeight());
	}
	
	/**
	 * 构建一个子画笔
	 * @param x 相对于画板X轴坐标
	 * @param y 相对于画板的Y轴坐标
	 * @param width 宽度
	 * @param height 高度
	 */
	@Nonnull
	public GuiPainter createPainter(int x, int y, int width, int height) {
		return createPainter(x, y, 0, 0, width, height);
	}
	
	/**
	 * 构建一个子画笔
	 * @param x 相对于画板X轴坐标
	 * @param y 相对于画板的Y轴坐标
	 * @param xOffset 渲染时X轴偏移量
	 * @param yOffset 渲染时Y轴偏移量
	 * @param width 宽度
	 * @param height 高度
	 */
	public GuiPainter createPainter(int x, int y, int xOffset, int yOffset, int width, int height) {
		GuiPainter result = new GuiPainter(gui,
				this.x + x + this.xOffset, this.y + y + this.yOffset,
				xOffset, yOffset,
				Math.min(width, this.width - x - this.xOffset), Math.min(height, this.height - y - this.yOffset));
		result.correctY(this.realY, realHeight);
		result.correctX(this.realX, realWidth);
		return result;
	}
	
	/**
	 * 构建一个子画笔。与{@link #createPainter(int, int, int, int)}不同的是，该方法构建的画笔绘制范围不受父画笔限制。
	 * @param x 相对于画板X轴坐标
	 * @param y 相对于画板的Y轴坐标
	 * @param width 宽度
	 * @param height 高度
	 */
	@Nonnull
	public GuiPainter extraPainter(int x, int y, int width, int height) {
		return new GuiPainter(gui, x + this.x, y + this.y, width, height);
	}
	
	protected void correctX(int minRealX, int maxRealWidth) {
		int oldX = realX;
		realX = Math.max(minRealX, realX);
		if (oldX == realX) {
			realWidth = Math.min(realWidth, realWidth - ((oldX + realWidth) - (minRealX + maxRealWidth)));
		} else {
			realWidth = Math.min(realWidth, maxRealWidth - ((minRealX + maxRealWidth) - (oldX + realWidth)));
		}
	}
	
	protected void correctY(int minRealY, int maxRealHeight) {
		int oldY = realY;
		realY = Math.max(minRealY, realY);
		if (oldY == realY) {
			realHeight = Math.min(realHeight, realHeight - ((oldY + realHeight) - (minRealY + maxRealHeight)));
		} else {
			realHeight = Math.min(realHeight, maxRealHeight - ((minRealY + maxRealHeight) - (oldY + realHeight)));
		}
	}
	
	protected boolean scissor() {
		if (realWidth < 0 || realHeight < 0) return true;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(realX, realY, realWidth, realHeight);
		return false;
	}
	
	protected void unscissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	/** 获取横坐标起点偏移量 */
	public int getXOffset() {
		return xOffset + gui.getGuiLeft();
	}
	
	/** 获取纵坐标起点偏移量 */
	public int getYOffset() {
		return yOffset + gui.getGuiTop();
	}
	
	/** 获取可绘制区域宽度 */
	public int getWidth() {
		return width;
	}
	
	/** 获取可绘制区域高度 */
	public int getHeight() {
		return height;
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