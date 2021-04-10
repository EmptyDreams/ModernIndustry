package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.interfaces.ThConsumer;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.data.math.Size2D;

import javax.annotation.Nonnull;
import java.awt.*;
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
	public void realTimePaint(GuiContainer gui) {
		//如果鼠标在按钮内则绘制特效
		if (isMouseIn()) {
			getStyle().drawOnMouseIn(gui, this, name);
		}
		//绘制文字
		if (getText().length() > 0) {
			getStyle().drawString(gui, this);
		}
	}
	
	@Override
	@SideOnly(CLIENT)
	public void paint(@Nonnull Graphics g) {
		getStyle().paint(g, new Size2D(getWidth(), getHeight()));
	}
	
	public enum Style {
	
		/** 矩形按钮 */
		REC(Style::recPaint, Style::recDrawMouseIn, Style::stringPainter),
		/** 向右三角形按钮 */
		TRIANGLE_RIGHT(Style::triangleRightPaint, Style::triangleRightDrawMouseIn, Style::stringPainter),
		/** 向左三角形按钮 */
		TRIANGLE_LEFT(Style::triangleLeftPaint, Style::triangleLeftDrawMouseIn, Style::stringPainter);
		
		private final BiConsumer<Graphics, Size2D> printer;
		private final ThConsumer<GuiContainer, IComponent, String> clickEffect;
		private final BiConsumer<GuiContainer, ButtonComponent> stringPainter;
		
		/**
		 * @param painter 底层图样绘制器
		 * @param clickEffect 点击效果绘制器
		 * @param stringPainter 字符串绘制器
		 */
		Style(BiConsumer<Graphics, Size2D> painter, ThConsumer<GuiContainer, IComponent, String> clickEffect,
		      BiConsumer<GuiContainer, ButtonComponent> stringPainter) {
			this.printer = painter;
			this.clickEffect = clickEffect;
			this.stringPainter = stringPainter;
		}
		
		/** 在按钮上绘制文本 */
		@SideOnly(CLIENT)
		public void drawString(GuiContainer gui, ButtonComponent button) {
			stringPainter.accept(gui, button);
		}
		
		/** 绘制材质资源 */
		@SideOnly(CLIENT)
		public void paint(Graphics g, Size2D size) {
			printer.accept(g, size);
		}
		
		/** 绘制鼠标在按钮范围内时的效果 */
		@SideOnly(CLIENT)
		public void drawOnMouseIn(GuiContainer gui, IComponent component, String name) {
			clickEffect.accept(gui, component, name);
		}
		
		//--------------------以下为工具方法-----------------//
		
		@SideOnly(CLIENT)
		private static void recPaint(Graphics g, Size2D size) {
			g.drawImage(ImageData.getImage(BUTTON_REC,
					size.getWidth(), size.getHeight()), 0, 0, null);
		}
		
		@SideOnly(CLIENT)
		private static void triangleRightPaint(Graphics g, Size2D size) {
			g.drawImage(ImageData.getImage(BUTTON_TRIANGLE_RIGHT,
					size.getWidth(), size.getHeight()), 0, 0, null);
		}
		
		@SideOnly(CLIENT)
		private static void triangleLeftPaint(Graphics g, Size2D size) {
			g.drawImage(ImageData.getImage(BUTTON_TRIANGLE_LEFT,
					size.getWidth(), size.getHeight()), 0, 0, null);
		}
		
		@SideOnly(CLIENT)
		private static void recDrawMouseIn(GuiContainer gui, IComponent component, String name) {
			imageDrawMouseIn(gui, component, name, BUTTON_REC_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void triangleRightDrawMouseIn(GuiContainer gui, IComponent component, String name) {
			imageDrawMouseIn(gui, component, name, BUTTON_TRIANGLE_RIGHT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void triangleLeftDrawMouseIn(GuiContainer gui, IComponent component, String name) {
			imageDrawMouseIn(gui, component, name, BUTTON_TRIANGLE_LEFT_CLICK);
		}
		
		@SideOnly(CLIENT)
		private static void stringPainter(GuiContainer gui, ButtonComponent button) {
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int width = button.getWidth(), height = button.getHeight();
			int x = button.getX(), y = button.getY();
			int textWidth = font.getStringWidth(button.getText());
			
			int centerX = x + (width / 2) + gui.getGuiLeft() + 1;
			int centerY = y + (height / 2) + gui.getGuiTop();
			
			int textX = centerX - (textWidth / 2);
			int textY = centerY - 5;
			
			font.drawString(button.getText(), textX, textY, button.getTextColor());
		}
		
		/**
		 * 绘制鼠标进入按钮后的图像
		 * @param gui 当前GUI
		 * @param component 按钮对象
		 * @param name 资源名称
		 * @param click 点击图像名称
		 */
		@SideOnly(CLIENT)
		private static void imageDrawMouseIn(GuiContainer gui, IComponent component, String name, String click) {
			GlStateManager.color(1, 1, 1);
			RuntimeTexture texture = RuntimeTexture.getInstance(name);
			int width = component.getWidth(), height = component.getHeight();
			if (texture == null) {
				texture = ImageData.createTexture(click, width, height, name);
			}
			texture.bindTexture();
			texture.drawToFrame(component.getX() + gui.getGuiLeft(), component.getY() + gui.getGuiTop(),
					0, 0, width, height);
		}
		
	}
	
}