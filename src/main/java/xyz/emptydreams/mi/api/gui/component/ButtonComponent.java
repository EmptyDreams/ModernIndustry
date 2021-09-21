package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.data.math.Size2D;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static xyz.emptydreams.mi.api.gui.client.ImageData.*;

/**
 * 按钮
 * @author EmptyDreams
 */
public class ButtonComponent extends InvisibleButton {
	
	/** 要显示的文字 */
	private String text = "";
	/** 文字颜色 */
	private int textColor = 0;
	/** 样式 */
	private final Style style;
	
	public ButtonComponent(int width, int height) {
		this(width, height, Style.REC);
	}
	
	public ButtonComponent(int width, int height, Style style) {
		super(width, height);
		this.style = style;
	}
	
	/** 设置文字颜色 */
	public void setTextColor(int color) {
		textColor = color;
	}
	
	/** 获取文字颜色 */
	public int getTextColor() {
		return textColor;
	}
	
	/** 设置要显示的文字及颜色 */
	@SuppressWarnings("unused")
	public void setText(String text, int color) {
		setText(text);
		setTextColor(color);
	}
	
	/** 设置要显示的文字 */
	public void setText(String text) {
		this.text = StringUtil.checkNull(text, "text");
	}
	
	/** 获取要显示的文字 */
	@Nonnull
	public String getText() {
		return text;
	}
	
	/** 获取按钮样式 */
	@Nonnull
	public Style getStyle() {
		return style;
	}
	
	/** 移除要显示的文字 */
	@SuppressWarnings("unused")
	public void removeText() {
		text = "";
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}
	
	@Override
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		//如果鼠标在按钮内则绘制特效
		if (isMouseIn()) {
			MISysInfo.print(getStyle());
			getStyle().drawOnMouseIn(painter, new Size2D(getWidth(), getHeight()));
		} else {
			getStyle().paint(painter, new Size2D(getWidth(), getHeight()));
		}
		//绘制文字
		if (getText().length() > 0) {
			getStyle().drawString(painter, this);
		}
	}
	
	public enum Style {
	
		/** 矩形按钮 */
		REC(Style::recPaint, Style::recDrawMouseIn, Style::stringPainter),
		/** 向右三角形按钮 */
		TRIANGLE_RIGHT(Style::triangleRightPaint, Style::triangleRightDrawMouseIn, Style::stringPainter),
		/** 向左三角形按钮 */
		TRIANGLE_LEFT(Style::triangleLeftPaint, Style::triangleLeftDrawMouseIn, Style::stringPainter),
		/** 向左翻页按钮 */
		PAGE_LEFT(Style::pageLeftPaint, Style::pageLeftDrawMouseIn, Style::stringPainterNon),
		/** 向右翻页按钮 */
		PAGE_RIGHT(Style::pageRightPaint, Style::pageRightDrawMouseIn, Style::stringPainterNon),
		/** */
		PAGE_UP(Style::pageUpPaint, Style::pageUpDrawMouseIn, Style::stringPainterNon),
		/** */
		PAGE_DOWN(Style::pageDownPaint, Style::pageDownDrawMouseIn, Style::stringPainterNon);
		
		private final BiConsumer<GuiPainter, Size2D> printer;
		private final BiConsumer<GuiPainter, Size2D> clickEffect;
		private final BiConsumer<GuiPainter, ButtonComponent> stringPainter;
		
		/**
		 * @param painter 底层图样绘制器
		 * @param clickEffect 点击效果绘制器
		 * @param stringPainter 字符串绘制器
		 */
		Style(BiConsumer<GuiPainter, Size2D> painter, BiConsumer<GuiPainter, Size2D> clickEffect,
		      BiConsumer<GuiPainter, ButtonComponent> stringPainter) {
			this.printer = painter;
			this.clickEffect = clickEffect;
			this.stringPainter = stringPainter;
		}
		
		/** 在按钮上绘制文本 */
		@SideOnly(CLIENT)
		public void drawString(GuiPainter painter, ButtonComponent button) {
			stringPainter.accept(painter, button);
		}
		
		/** 绘制材质资源 */
		@SideOnly(CLIENT)
		public void paint(GuiPainter painter, Size2D size) {
			printer.accept(painter, size);
		}
		
		/** 绘制鼠标在按钮范围内时的效果 */
		@SideOnly(CLIENT)
		public void drawOnMouseIn(GuiPainter painter, Size2D size) {
			clickEffect.accept(painter, size);
		}
		
		//--------------------以下为工具方法-----------------//
		
		@SideOnly(CLIENT)
		private static void recPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_REC,
					size.getWidth(), size.getHeight(), REC.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageLeftPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_PAGE_LEFT,
					size.getWidth(), size.getHeight(), TRIANGLE_LEFT.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageRightPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_PAGE_RIGHT,
					size.getWidth(), size.getHeight(), TRIANGLE_LEFT.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageUpPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_PAGE_UP,
					size.getWidth(), size.getHeight(), TRIANGLE_LEFT.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageDownPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_PAGE_DOWN,
					size.getWidth(), size.getHeight(), TRIANGLE_LEFT.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void triangleRightPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_TRIANGLE_RIGHT,
					size.getWidth(), size.getHeight(), TRIANGLE_RIGHT.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void triangleLeftPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(BUTTON_TRIANGLE_LEFT,
					size.getWidth(), size.getHeight(), TRIANGLE_LEFT.getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void recDrawMouseIn(GuiPainter gui, Size2D size) {
			imageDrawMouseIn(gui, size, REC.getTextureName(size, true), BUTTON_REC_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageLeftDrawMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size,
					PAGE_LEFT.getTextureName(size, true), BUTTON_PAGE_LEFT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageRightDrawMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size,
					PAGE_RIGHT.getTextureName(size, true), BUTTON_PAGE_RIGHT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageUpDrawMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size,
					PAGE_UP.getTextureName(size, true), BUTTON_PAGE_UP_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageDownDrawMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size,
					PAGE_DOWN.getTextureName(size, true), BUTTON_PAGE_DOWN_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void triangleRightDrawMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size,
					TRIANGLE_RIGHT.getTextureName(size, true), BUTTON_TRIANGLE_RIGHT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void triangleLeftDrawMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size,
					TRIANGLE_LEFT.getTextureName(size, true), BUTTON_TRIANGLE_LEFT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void stringPainterNon(GuiPainter painter, ButtonComponent button) { }
		
		@SideOnly(CLIENT)
		private static void stringPainter(GuiPainter painter, ButtonComponent button) {
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int width = button.getWidth(), height = button.getHeight();
			int x = painter.getX(), y = painter.getY();
			int textWidth = font.getStringWidth(button.getText());
			
			int centerX = x + (width / 2) + painter.getGuiContainer().getGuiLeft() + 1;
			int centerY = y + (height / 2) + painter.getGuiContainer().getGuiTop();
			
			int textX = centerX - (textWidth / 2);
			int textY = centerY - 5;
			
			font.drawString(button.getText(), textX, textY, button.getTextColor());
		}
		
		/**
		 * 绘制鼠标进入按钮后的图像
		 * @param painter GUI画笔
		 * @param size 按钮大小
		 * @param name 资源名称
		 * @param click 点击图像名称
		 */
		@SideOnly(CLIENT)
		private static void imageDrawMouseIn(GuiPainter painter, Size2D size, String name, String click) {
			GlStateManager.color(1, 1, 1);
			RuntimeTexture texture = RuntimeTexture.getInstance(name);
			int width = size.getWidth(), height = size.getHeight();
			if (texture == null) {
				texture = ImageData.createTexture(click, width, height, name);
			}
			texture.bindTexture();
			painter.drawTexture(0, 0, width, height, texture);
		}
		
		private String getTextureName(Size2D size, boolean activate) {
			return FMLCommonHandler.instance().getModName() + ":" + "ButtonComponent" + "!"
					+ name() + "*" + activate + "@" + size.getWidth() + "#" + size.getHeight();
		}
		
	}
	
}