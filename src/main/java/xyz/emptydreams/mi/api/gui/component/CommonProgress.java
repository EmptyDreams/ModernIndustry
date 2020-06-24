package xyz.emptydreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Consumer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.net.WaitList;

import static xyz.emptydreams.mi.api.gui.component.IProgressBar.getTexture;

/**
 * 通用进度条
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
public class CommonProgress extends MComponent implements IProgressBar {
	
	private int max;
	private int now;
	private Style style;
	private Front front;
	
	/** 创建一个默认风格({@link Style#ARROW})和方向({@link Front#RIGHT})的进度条 */
	public CommonProgress() {
		this(Style.ARROW, Front.RIGHT);
	}
	
	/** 创建一个自定义风格和尺寸的进度条 */
	public CommonProgress(Style style, Front front) {
		this.style = style;
		this.front = front;
		setSize(style.getWidth(), style.getHeight());
	}
	
	@Override
	public void realTimePaint(GuiContainer gui) {
		front.accept(new Node(gui));
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOURCE_NAME).getSubimage(
				style.getX(), style.getY(), style.getWidth(), style.getHeight()), 0, 0, null);
	}
	
	@Override
	public int getNow() {
		return now;
	}
	
	@Override
	public int getMax() {
		return max;
	}
	
	public Style getStyle() {
		return style;
	}
	
	public Front getFront() {
		return front;
	}
	
	@Override
	public void setMax(int max) {
		this.max = max;
	}
	
	@Override
	public void setNow(int now) {
		this.now = Math.min(now, getMax());
	}
	
	@Override
	public boolean isReverse() {
		return style.isReverse();
	}
	
	public void setStyle(Style style) {
		WaitList.checkNull(style, "style");
		this.style = style;
		setSize(style.getWidth(), style.getHeight());
	}
	
	public void setFront(Front front) {
		WaitList.checkNull(front, "front");
		this.front = front;
	}
	
	@Override
	public void send(Container con, IContainerListener listener) {
		listener.sendWindowProperty(con, getCode(), getNow());
		listener.sendWindowProperty(con, getCode() + 1, getMax());
	}
	
	@Override
	public boolean update(int codeID, int data) {
		if (codeID == getCode()) {
			setNow(data);
			return true;
		} else if (codeID == getCode() + 1) {
			setMax(data);
			return true;
		}
		return false;
	}
	
	/** 风格 */
	public enum Style {
		
		ARROW(0, 0, 0, 15, 22, 15, false),
		STRIPE(22, 0, 22, 4, 89, 4, false),
		ARROW_BIG(22, 8, 22, 27, 68, 19, false),
		FIRE(90, 8, 90, 21, 13, 13, true);
		
		private final int x, y, x2, y2, width, height;
		private final boolean reverse;
		
		Style(int x, int y, int x2, int y2, int width, int height, boolean reverse) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.width = width;
			this.height = height;
			this.reverse = reverse;
		}
		
		public int getX() { return x; }
		public int getY() { return y; }
		public int getX2() { return x2; }
		public int getY2() { return y2; }
		public int getWidth() { return width; }
		public int getHeight() { return height; }
		public boolean isReverse() { return reverse; }
		
	}
	
	/**
	 * 进度条方向
	 */
	public enum Front {
		/** 从下到上 */
		UP(CommonProgress::paintUp),
		/** 从上到下 */
		DOWN(CommonProgress::paintDown),
		/** 从右到左 */
		LEFT(CommonProgress::paintLeft),
		/** 从左到右 */
		RIGHT(CommonProgress::paintRight);
		
		private final Consumer<Node> consumer;
		
		Front(Consumer<Node> consumer) {
			this.consumer = consumer;
		}
		
		/** 执行计算 */
		public void accept(Node node) {
			consumer.accept(node);
		}
		
	}
	
	private class Node {
		/** 图形在窗口中的坐标 */
		final int x, y;
		
		Node(GuiContainer gui) {
			int offsetX = (gui.width - gui.getXSize()) / 2;
			int offsetY = (gui.height - gui.getYSize()) / 2;
			x = offsetX + getX();
			y = offsetY + getY();
		}
		
		CommonProgress getThis() { return CommonProgress.this; }
		
	}
	
	/** 绘制从下到上的图形 */
	public static void paintUp(Node node) {
		Style style = node.getThis().style;
		int height = (int) (style.getHeight() * node.getThis().getPer());
		int y = node.y + style.getHeight() - height;
		int tY = style.getY2() + style.getHeight() - height;
		RuntimeTexture texture = getTexture();
		//Gui.drawModalRectWithCustomSizedTexture(node.x, y, style.getX2(), tY,
		//		style.getWidth(), height, texture.getTextureWidth(), texture.getTextureHeight());
		texture.drawToFrame(node.x, y, style.getX2(), style.getY2(), style.getWidth(), height);
	}
	
	/** 绘制从上到下的图形 */
	public static void paintDown(Node node) {
		Style style = node.getThis().style;
		int height = (int) (style.getHeight() * node.getThis().getPer());
		RuntimeTexture texture = getTexture();
		//Gui.drawModalRectWithCustomSizedTexture(node.x, node.y, style.getX2(), style.getY2(),
		//		style.getWidth(), height, texture.getTextureWidth(), texture.getTextureHeight());
		texture.drawToFrame(node.x, node.y, style.getX2(), style.getY2(), style.getWidth(), height);
	}
	
	/** 绘制从右到左的图形 */
	public static void paintLeft(Node node) {
		Style style = node.getThis().style;
		int width = (int) (style.getWidth() * node.getThis().getPer());
		int x = node.x + style.getWidth() - width;
		int tX = style.getX2() + style.getWidth() - width;
		RuntimeTexture texture = getTexture();
		//Gui.drawModalRectWithCustomSizedTexture(x, node.y, tX, style.getY2(),
		//		width, style.getHeight(), texture.getTextureWidth(), texture.getTextureHeight());
		texture.drawToFrame(x, node.y, tX, style.getY2(), width, style.getHeight());
	}
	
	/** 绘制从左到右的图形 */
	public static void paintRight(Node node) {
		Style style = node.getThis().style;
		int width = (int) (style.getWidth() * node.getThis().getPer());
		RuntimeTexture texture = getTexture();
		//Gui.drawModalRectWithCustomSizedTexture(node.x, node.y, style.getX2(), style.getY2(),
		//		width, style.getHeight(), texture.getTextureWidth(), texture.getTextureHeight());
		texture.drawToFrame(node.x, node.y, style.getX2(), style.getY2(), width, style.getHeight());
	}
	
}
