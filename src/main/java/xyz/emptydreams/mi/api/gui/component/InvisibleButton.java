package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseEnteredListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseExitedListener;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 隐形按钮
 * @author EmptyDreams
 */
public class InvisibleButton extends MComponent {
	
	@SideOnly(Side.CLIENT)
	private static final Consumer<GuiContainer> SRC_ACTION = guiContainer -> {};
	
	/** 鼠标是否在控件中 */
	private boolean mouse = false;
	/** 点击时执行的操作 */
	private Consumer<GuiContainer> onAction = SRC_ACTION;
	
	public InvisibleButton(int width, int height) {
		setSize(width, height);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onAddToGUI(GuiContainer con, EntityPlayer player) {
		registryListener((MouseEnteredListener) (mouseX, mouseY) -> mouse = true);
		registryListener((MouseExitedListener) (mouseX, mouseY) -> mouse = false);
		registryListener((MouseActionListener) (mouseX, mouseY) -> onAction.accept(con));
	}
	
	/** 判断鼠标是否在控件中 */
	protected boolean isMouseIn() { return mouse; }
	
	/** 设置按钮被点击时的操作，默认为无任何操作 */
	public void setAction(Consumer<GuiContainer> consumer) {
		onAction = StringUtil.checkNull(consumer, "consumer");
	}
	
	@Override
	public void paint(@Nonnull Graphics g) { }
}
