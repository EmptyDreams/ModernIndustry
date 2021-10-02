package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
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
		REC(BUTTON_REC, BUTTON_REC_CLICK, Style::stringPainter),
		/** 向右三角形按钮 */
		TRIANGLE_RIGHT(BUTTON_TRIANGLE_RIGHT, BUTTON_TRIANGLE_LEFT_CLICK, Style::stringPainter),
		/** 向左三角形按钮 */
		TRIANGLE_LEFT(BUTTON_TRIANGLE_LEFT, BUTTON_TRIANGLE_LEFT_CLICK, Style::stringPainter),
		
		/** 向左翻页按钮（矩形） */
		REC_PAGE_LEFT(BUTTON_REC_PAGE_LEFT, BUTTON_REC_PAGE_LEFT_CLICK, Style::stringPainterNon),
		/** 向右翻页按钮（矩形） */
		REC_PAGE_RIGHT(BUTTON_REC_PAGE_RIGHT, BUTTON_REC_PAGE_RIGHT_CLICK, Style::stringPainterNon),
		/** 向上翻页按钮（矩形） */
		REC_PAGE_UP(BUTTON_REC_PAGE_UP, BUTTON_REC_PAGE_UP_CLICK, Style::stringPainterNon),
		/** 向下翻页按钮（矩形） */
		REC_PAGE_DOWN(BUTTON_REC_PAGE_DOWN, BUTTON_REC_PAGE_DOWN_CLICK, Style::stringPainterNon),
		
		/** 向左翻页按钮（弧形） */
		ARC_PAGE_LEFT(BUTTON_ARC_PAGE_LEFT, BUTTON_ARC_PAGE_LEFT_CLICK, Style::stringPainterNon),
		/** 向右翻页按钮（弧形） */
		ARC_PAGE_RIGHT(BUTTON_ARC_PAGE_RIGHT, BUTTON_ARC_PAGE_RIGHT_CLICK, Style::stringPainterNon);
		
		
		private final String SRC_NAME;
		private final String CLICKED_NAME;
		private final BiConsumer<GuiPainter, ButtonComponent> stringPainter;
		
		/**
		 * @param src 默认显示的按钮
		 * @param clicked 鼠标点击时的显示
		 * @param stringPainter 字符串绘制器
		 */
		Style(String src, String clicked,
		      BiConsumer<GuiPainter, ButtonComponent> stringPainter) {
			this.SRC_NAME = src;
			this.CLICKED_NAME = clicked;
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
			RuntimeTexture texture = createTexture(SRC_NAME,
					size.getWidth(), size.getHeight(), getTextureName(size, false));
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		/** 绘制鼠标在按钮范围内时的效果 */
		@SideOnly(CLIENT)
		public void drawOnMouseIn(GuiPainter painter, Size2D size) {
			imageDrawMouseIn(painter, size, getTextureName(size, true), CLICKED_NAME);
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