package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.interfaces.ThConsumer;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.data.Size2D;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

import static xyz.emptydreams.mi.api.gui.client.ImageData.BUTTON_REC;
import static xyz.emptydreams.mi.api.gui.client.ImageData.BUTTON_REC_CLICK;

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
		name = BUTTON_REC + width + "!" + height;
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
	@SideOnly(Side.CLIENT)
	public void paint(@Nonnull Graphics g) {
		getStyle().paint(g, new Size2D(getWidth(), getHeight()));
	}
	
	public enum Style {
	
		REC(Style::recPaint, Style::recDrawMouseIn, Style::recDrawString);
		
		private final BiConsumer<Graphics, Size2D> printer;
		private final ThConsumer<GuiContainer, IComponent, String> clickEffect;
		private final BiConsumer<GuiContainer, ButtonComponent> stringPainter;
		
		Style(BiConsumer<Graphics, Size2D> painter, ThConsumer<GuiContainer, IComponent, String> clickEffect,
		      BiConsumer<GuiContainer, ButtonComponent> stringPainter) {
			this.printer = painter;
			this.clickEffect = clickEffect;
			this.stringPainter = stringPainter;
		}
		
		@SideOnly(Side.CLIENT)
		public void drawString(GuiContainer gui, ButtonComponent button) {
			stringPainter.accept(gui, button);
		}
		
		@SideOnly(Side.CLIENT)
		public void paint(Graphics g, Size2D size) {
			printer.accept(g, size);
		}
		
		/** 绘制鼠标在按钮范围内时的效果 */
		@SideOnly(Side.CLIENT)
		public void drawOnMouseIn(GuiContainer gui, IComponent component, String name) {
			clickEffect.accept(gui, component, name);
		}
		
		@SideOnly(Side.CLIENT)
		private static void recPaint(Graphics g, Size2D size) {
			g.drawImage(ImageData.getImage(BUTTON_REC,
					size.getWidth(), size.getHeight()), 0, 0, null);
		}
		
		@SideOnly(Side.CLIENT)
		private static void recDrawMouseIn(GuiContainer gui, IComponent component, String name) {
			GlStateManager.color(1, 1, 1);
			RuntimeTexture texture = RuntimeTexture.getInstance(name);
			int width = component.getWidth(), height = component.getHeight();
			if (texture == null) {
				Image image = ImageData.getImage(BUTTON_REC_CLICK, width, height);
				BufferedImage buffered = new BufferedImage(width, height, 6);
				Graphics g = buffered.getGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				texture = RuntimeTexture.instance(name, buffered);
			}
			texture.bindTexture();
			texture.drawToFrame(component.getX() + gui.getGuiLeft(), component.getY() + gui.getGuiTop(),
					0, 0, width, height);
		}
		
		@SideOnly(Side.CLIENT)
		private static void recDrawString(GuiContainer gui, ButtonComponent button) {
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int width = button.getWidth(), height = button.getHeight();
			int x = button.getX(), y = button.getY();
			int textWidth = font.getStringWidth(button.getText());
			int textX;
			if (textWidth < width) {
				textX = x + (textWidth / 2) + gui.getGuiLeft();
			} else if (textWidth == width) {
				textX = x;
			} else {
				textX = x - (textWidth - width) / 2 + gui.getGuiLeft();
			}
			int textY;
			if (10 < height) {
				textY = y + 5 + gui.getGuiTop();
			} else if (10 == height) {
				textY = y + gui.getGuiTop();
			} else {
				textY = y - (10 - height) / 2 + gui.getGuiTop();
			}
			font.drawString(button.getText(), textX, textY, button.getTextColor());
		}
		
	}
	
}
