package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;

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
	
	public ButtonComponent(int width, int height) {
		super(width, height);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		name = RESOURCE_CLICKED_NAME + width + "!" + height;
	}
	
	@Override
	public void realTimePaint(GuiContainer gui) {
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
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOURCE_NAME, getWidth(), getHeight()), 0, 0, null);
	}
	
}
