package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("SpellCheckingInspection")
@SideOnly(Side.CLIENT)
@Mixin(GuiTextField.class)
public class MixinGuiTextField {
	
	/**
	 * @reason 若子GUI存在，则将方法转发至子GUI
	 */
	@Redirect(method = "setFocused",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;setFocused(Z)V"))
	private void setFocused(GuiScreen guiScreen, boolean hasFocusedControlIn) {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? guiScreen : child;
		container.setFocused(hasFocusedControlIn);
	}
	
}