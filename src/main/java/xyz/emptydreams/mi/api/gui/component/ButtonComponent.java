package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.interfaces.ThConsumer;
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
	
	/** 资源名称 */
	private String name;
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
		name = getStyle().name() + width + "!" + height;
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
		if (style != null) name = getStyle().name() + width + "!" + height;
	}
	
	@Override
	public void realTimePaint(GuiPainter painter) {
		getStyle().paint(painter, new Size2D(getWidth(), getHeight()));
		//如果鼠标在按钮内则绘制特效
		if (isMouseIn()) {
			getStyle().drawOnMouseIn(painter, this, name);
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
		private final ThConsumer<GuiPainter, IComponent, String> clickEffect;
		private final BiConsumer<GuiPainter, ButtonComponent> stringPainter;
		
		/**
		 * @param painter 底层图样绘制器
		 * @param clickEffect 点击效果绘制器
		 * @param stringPainter 字符串绘制器
		 */
		Style(BiConsumer<GuiPainter, Size2D> painter, ThConsumer<GuiPainter, IComponent, String> clickEffect,
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
		public void drawOnMouseIn(GuiPainter painter, IComponent component, String name) {
			clickEffect.accept(painter, component, name);
		}
		
		//--------------------以下为工具方法-----------------//
		
		@SideOnly(CLIENT)
		private static void recPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(REC.getTextureName(size),
										size.getWidth(), size.getHeight(), BUTTON_REC);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageLeftPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(TRIANGLE_LEFT.getTextureName(size),
										size.getWidth(), size.getHeight(), BUTTON_PAGE_LEFT);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageRightPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(TRIANGLE_LEFT.getTextureName(size),
					size.getWidth(), size.getHeight(), BUTTON_PAGE_RIGHT);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageUpPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(TRIANGLE_LEFT.getTextureName(size),
					size.getWidth(), size.getHeight(), BUTTON_PAGE_UP);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void pageDownPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(TRIANGLE_LEFT.getTextureName(size),
					size.getWidth(), size.getHeight(), BUTTON_PAGE_DOWN);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void triangleRightPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(TRIANGLE_LEFT.getTextureName(size),
					size.getWidth(), size.getHeight(), BUTTON_TRIANGLE_RIGHT);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void triangleLeftPaint(GuiPainter painter, Size2D size) {
			RuntimeTexture texture = createTexture(TRIANGLE_LEFT.getTextureName(size),
					size.getWidth(), size.getHeight(), BUTTON_TRIANGLE_LEFT);
			texture.bindTexture();
			painter.drawTexture(0, 0, size.getWidth(), size.getHeight(), texture);
		}
		
		@SideOnly(CLIENT)
		private static void recDrawMouseIn(GuiPainter gui, IComponent component, String name) {
			imageDrawMouseIn(gui, component, name, BUTTON_REC_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageLeftDrawMouseIn(GuiPainter painter, IComponent component, String name) {
			imageDrawMouseIn(painter, component, name, BUTTON_PAGE_LEFT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageRightDrawMouseIn(GuiPainter painter, IComponent component, String name) {
			imageDrawMouseIn(painter, component, name, BUTTON_PAGE_RIGHT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageUpDrawMouseIn(GuiPainter painter, IComponent component, String name) {
			imageDrawMouseIn(painter, component, name, BUTTON_PAGE_UP_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void pageDownDrawMouseIn(GuiPainter painter, IComponent component, String name) {
			imageDrawMouseIn(painter, component, name, BUTTON_PAGE_DOWN_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void triangleRightDrawMouseIn(GuiPainter painter, IComponent component, String name) {
			imageDrawMouseIn(painter, component, name, BUTTON_TRIANGLE_RIGHT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void triangleLeftDrawMouseIn(GuiPainter painter, IComponent component, String name) {
			imageDrawMouseIn(painter, component, name, BUTTON_TRIANGLE_LEFT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void stringPainterNon(GuiPainter painter, ButtonComponent button) { }
		
		@SideOnly(CLIENT)
		private static void stringPainter(GuiPainter painter, ButtonComponent button) {
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int width = button.getWidth(), height = button.getHeight();
			int x = button.getX(), y = button.getY();
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
		 * @param component 按钮对象
		 * @param name 资源名称
		 * @param click 点击图像名称
		 */
		@SideOnly(CLIENT)
		private static void imageDrawMouseIn(GuiPainter painter, IComponent component, String name, String click) {
			GlStateManager.color(1, 1, 1);
			RuntimeTexture texture = RuntimeTexture.getInstance(name);
			int width = component.getWidth(), height = component.getHeight();
			if (texture == null) {
				texture = ImageData.createTexture(click, width, height, name);
			}
			texture.bindTexture();
			texture.drawToFrame(component.getX() + painter.getGuiContainer().getGuiLeft(),
					component.getY() + painter.getGuiContainer().getGuiTop(),
					0, 0, width, height);
		}
		
		private String getTextureName(Size2D size) {
			return FMLCommonHandler.instance().getModName() + ":" + "ButtonComponent" + "!"
					+ name() + "@" + size.getWidth() + "#" + size.getHeight();
		}
		
	}
	
}