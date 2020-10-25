package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.MIFrame;

import javax.annotation.Nonnull;

/**
 * 按钮的接口。<br>
 * 在按钮被添加到GUI时
 *      {@link IComponent#onAddToGUI(MIFrame, EntityPlayer)}及
 *      {@link IComponent#onAddToGUI(StaticFrameClient, EntityPlayer)}方法中EntityPlayer可能为null，
 *      但是其余方法依然可以正常工作。
 * @author EmptyDreams
 */
public interface IButton extends IComponent {
	
	/** 在初始化GUI时调用该方法生成{@link GuiButton}对象 */
	@SideOnly(Side.CLIENT)
	@Nonnull
	GuiButton createGuiButtonObject(int id);

	/** 点击后的操作 */
	void click();
	
}
