package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;

/**
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
	
	@Shadow public Minecraft mc;
	
	/**
	 * @reason 玩家关闭GUI时只关闭子GUI，不关闭其他GUI
	 */
	@Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true, remap = false)
	private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
		if ((keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
				&& LocalChildFrame.hasContainer()) {
			//noinspection ConstantConditions
			if (((Object) this) != LocalChildFrame.getContainer()) setFocused(true);
			LocalChildFrame.closeGUI();
			ci.cancel();
		}
	}
	
	public void setFocused(boolean hasFocusedControlIn) { }
	
}