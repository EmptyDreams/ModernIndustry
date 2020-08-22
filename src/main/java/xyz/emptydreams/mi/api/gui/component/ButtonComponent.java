package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseEnteredListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseExitedListener;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * 按钮
 * @author EmptyDreams
 */
public class ButtonComponent extends MComponent {
	
	public static final String RESOURCE_NAME = "button";
	public static final String RESOURCE_CLICKED_NAME = "buttonClicked";
	@SideOnly(Side.CLIENT)
	private static final Consumer<GuiContainer> SRC_ACTION = guiContainer -> {};
	
	/** 鼠标是否在控件中 */
	private boolean mouse = false;
	/** 资源名称 */
	private String name;
	/** 点击时执行的操作 */
	@SideOnly(Side.CLIENT)
	private Consumer<GuiContainer> onAction = SRC_ACTION;
	
	public ButtonComponent(int width, int height) {
		setSize(width, height);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		name = RESOURCE_CLICKED_NAME + width + "!" + height;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onAddToGUI(GuiContainer con, EntityPlayer player) {
		registryListener((MouseEnteredListener) (mouseX, mouseY) -> mouse = true);
		registryListener((MouseExitedListener) (mouseX, mouseY) -> mouse = false);
		registryListener((MouseActionListener) (mouseX, mouseY) -> onAction.accept(con));
	}
	
	/** 设置按钮被点击时的操作，默认为无任何操作 */
	@SideOnly(Side.CLIENT)
	public void setAction(Consumer<GuiContainer> consumer) {
		WaitList.checkNull(consumer, "consumer");
		onAction = consumer;
	}
	
	@Override
	public void realTimePaint(GuiContainer gui) {
		if (mouse) {
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
			texture.drawToFrame(getX() + gui.getGuiLeft(), getY() + gui.getGuiTop(), 0, 0, getWidth(), getHeight());
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(RESOURCE_NAME, getWidth(), getHeight()), 0, 0, null);
	}
	
}
