package top.kmar.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.gui.component.interfaces.IProgressBar;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.gui.client.GuiPainter;
import top.kmar.mi.api.gui.client.RuntimeTexture;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

import static top.kmar.mi.api.gui.client.ImageData.PROGRESS_BAR;
import static top.kmar.mi.api.gui.client.ImageData.createTexture;
import static top.kmar.mi.api.gui.component.interfaces.IProgressBar.getTexture;

/**
 * 通用进度条
 * @author EmptyDreams
 */
public class CommonProgress extends MComponent implements IProgressBar {

	/** 最大 */
	private int max = Integer.MAX_VALUE;
	/** 现在 */
	private int now = 0;
	/** 样式 */
	private Style style;
	/** 方向 */
	private Front front;
	/** 是否含有进度条 */
	private ProgressStyle shower = null;
	
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

	@SideOnly(Side.CLIENT)
	@Override
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		paintBackground(painter);
		front.accept(this, painter);
		if (getStringShower() != null) getStringShower().draw(this, painter);
	}
	
	public void paintBackground(@Nonnull GuiPainter painter) {
		RuntimeTexture texture = createTexture(PROGRESS_BAR, createTextureName());
		texture.bindTexture();
		painter.drawTexture(0, 0, getStyle().getX(), getStyle().getY(),
				getStyle().getWidth(), getStyle().getHeight(), texture);
	}

	/** 是否含有进度条显示 */
	public ProgressStyle getStringShower() { return shower; }
	/** 设置进度条显示 */
	public void setStringShower(ProgressStyle value) { shower = value; }
	@Override
	public int getNow() { return now; }
	@Override
	public int getMax() { return max; }
	/** 获取显示风格 */
	public Style getStyle() { return style; }
	/** 获取进度条方向 */
	public Front getFront() { return front; }
	@Override
	public void setMax(int max) { this.max = max; }
	@Override
	public void setNow(int now) {
		if (now < 0) now = 0;
		this.now = Math.min(now, getMax());
	}
	@Override
	public boolean isReverse() { return getStyle().isReverse(); }
	/** 设置进度条风格 */
	public void setStyle(Style style) {
		this.style = StringUtil.checkNull(style, "style");
		setSize(style.getWidth(), style.getHeight());
	}
	/** 设置进度条方向 */
	public void setFront(Front front) {
		this.front = StringUtil.checkNull(front, "front");
	}
	
	@Override
	public void send(Container con, IContainerListener listener) {
		listener.sendWindowProperty(con, getCodeID(0), getNow());
		listener.sendWindowProperty(con, getCodeID(1), getMax());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean update(int codeID, int data) {
		switch (getPrivateCodeID(codeID)) {
			case 0: setNow(data); return true;
			case 1: setMax(data); return true;
			default: return false;
		}
	}

	public enum ProgressStyle {

		UP((bar, painter) -> drawHelper(bar, painter, -10)),
		DOWN((bar, painter) -> drawHelper(bar, painter, bar.getHeight())),
		CENTER((bar, painter) -> drawHelper(bar, painter, (bar.getHeight() - 9) / 2));

		private final BiConsumer<CommonProgress, GuiPainter> task;

		ProgressStyle(BiConsumer<CommonProgress, GuiPainter> task) {
			this.task = task;
		}

		/** 绘制 */
		@SideOnly(Side.CLIENT)
		public void draw(CommonProgress bar, GuiPainter painter) {
			task.accept(bar, painter);
		}

		/** 格式化字符串 */
		private static String format(CommonProgress bar) {
			return bar.getNow() + "/" + bar.getMax();
		}

		private static void drawHelper(CommonProgress bar, GuiPainter painter, int y) {
			String show = format(bar);
			Minecraft mc = Minecraft.getMinecraft();
			int x = (bar.getWidth() - mc.fontRenderer.getStringWidth(show)) / 2;
			painter.extraPainter(0, y, bar.getWidth(), 9).drawString(x, 0, show, 0);
		}

	}

	/** 风格 */
	public enum Style {

		/** 普通箭头 */
		ARROW(0, 0, 0, 15, 22, 15, false),
		/** 长条 */
		STRIPE(22, 0, 22, 4, 89, 4, false),
		/** 巨大箭头 */
		ARROW_BIG(22, 8, 22, 27, 68, 19, false),
		/** 燃烧 */
		FIRE(90, 8, 90, 21, 13, 13, true),
		/** 向下的箭头 */
		ARROW_DOWN(0, 30, 0, 52, 15, 22, false);
		
		private final int x, y, fillX, fillY, width, height;
		private final boolean reverse;

		/**
		 * @param x 空进度条的起点
		 * @param y 空进度条的起点
		 * @param fillX 满进度条的起点
		 * @param fillY 满进度条的起点
		 * @param width 进度条的宽度
		 * @param height 进度条的高度
		 * @param reverse 是否颠倒进度
		 */
		Style(int x, int y, int fillX, int fillY, int width, int height, boolean reverse) {
			this.x = x;
			this.y = y;
			this.fillX = fillX;
			this.fillY = fillY;
			this.width = width;
			this.height = height;
			this.reverse = reverse;
		}

		/** 获取空进度条的起点 */
		public int getX() { return x; }
		/** 获取空进度条的起点 */
		public int getY() { return y; }
		/** 获取填充条的起点 */
		public int getFillX() { return fillX; }
		/** 获取填充条的终点 */
		public int getFillY() { return fillY; }
		/** 获取宽度 */
		public int getWidth() { return width; }
		/** 获取高度 */
		public int getHeight() { return height; }
		/** 是否反转 */
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
		
		private final BiConsumer<CommonProgress, GuiPainter> consumer;
		
		Front(BiConsumer<CommonProgress, GuiPainter> consumer) {
			this.consumer = consumer;
		}
		
		/** 执行计算 */
		public void accept(CommonProgress progress, GuiPainter painter) {
			consumer.accept(progress, painter);
		}
		
	}
	
	/** 绘制从下到上的图形 */
	private static void paintUp(CommonProgress progress, GuiPainter painter) {
		Style style = progress.getStyle();
		int height = (int) (style.getHeight() * progress.getPer());
		int y = style.getHeight() - height;
		RuntimeTexture texture = getTexture();
		painter.drawTexture(0, y, style.getFillX(), style.getFillY(), style.getWidth(), height, texture);
	}
	
	/** 绘制从上到下的图形 */
	private static void paintDown(CommonProgress progress, GuiPainter painter) {
		Style style = progress.getStyle();
		int height = (int) (style.getHeight() * progress.getPer());
		RuntimeTexture texture = getTexture();
		painter.drawTexture(0, 0, style.getFillX(), style.getFillY(), style.getWidth(), height, texture);
	}
	
	/** 绘制从右到左的图形 */
	private static void paintLeft(CommonProgress progress, GuiPainter painter) {
		Style style = progress.getStyle();
		int width = (int) (style.getWidth() * progress.getPer());
		int x = style.getWidth() - width;
		int tX = style.getFillX() + style.getWidth() - width;
		RuntimeTexture texture = getTexture();
		painter.drawTexture(x, 0, width, tX, style.getFillY(), style.getHeight(), texture);
	}
	
	/** 绘制从左到右的图形 */
	private static void paintRight(CommonProgress progress, GuiPainter painter) {
		Style style = progress.getStyle();
		int width = (int) (style.getWidth() * progress.getPer());
		RuntimeTexture texture = getTexture();
		painter.drawTexture(0, 0, style.getFillX(), style.getFillY(), width, style.getHeight(), texture);
	}
	
}