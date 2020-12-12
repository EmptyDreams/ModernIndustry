package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 按钮
 * @author EmptyDreams
 */
public class ButtonComponent extends InvisibleButton {
	
	public static final String RESOURCE_NAME = "button";
	public static final String RESOURCE_CLICKED_NAME = "buttonClicked";
	
	/** 资源名称 */
	private String name;
	/** 要显示的文字 */
	private String text = "";
	/** 文字颜色 */
	private int textColor = 0;
	
	public ButtonComponent(int width, int height) {
		super(width, height);
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
	
	/** 移除要显示的文字 */
	public void removeText() {
		text = "";
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		name = RESOURCE_CLICKED_NAME + width + "!" + height;
	}
	
	@Override
	public void realTimePaint(GuiContainer gui) {
		//如果鼠标在按钮内则绘制特效
		if (isMouseIn()) {
			GlStateManager.color(1, 1, 1);
			RuntimeTexture texture = RuntimeTexture.getInstance(name);
			if (texture == null) {
				Image image = ImageData.getImage(RESOURCE_CLICKED_NAME, getWidth(), getHeight());
				BufferedImage buffered = new BufferedImage(getWidth(), getHeight(), 6);
				Graphics g = buffered.getGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				texture = RuntimeTexture.instance(name, buffered);
			}
			texture.bindTexture();
			texture.drawToFrame(getX() + gui.getGuiLeft(), getY() + gui.getGuiTop(),
					0, 0, getWidth(), getHeight());
		}
		//绘制文字
		if (getText().length() > 0) {
			FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
			int textWidth = renderer.getStringWidth(getText());
			int textX;
			if (textWidth < getWidth()) {
				textX = getX() + (textWidth / 2) + gui.getGuiLeft();
			} else if (textWidth == getWidth()) {
				textX = getX();
			} else {
				textX = getX() - (textWidth - getWidth()) / 2 + gui.getGuiLeft();
			}
			int textY;
			if (10 < getHeight()) {
				textY = getY() + 5 + gui.getGuiTop();
			} else if (10 == getHeight()) {
				textY = getY() + gui.getGuiTop();
			} else {
				textY = getY() - (10 - getHeight()) / 2 + gui.getGuiTop();
			}
			renderer.drawString(getText(), textX, textY, getTextColor());
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOURCE_NAME, getWidth(), getHeight()), 0, 0, null);
	}
	
}
