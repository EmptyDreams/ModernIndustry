package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;

/**
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {
	
	/**
	 * @reason 玩家退出GUI时只关闭子GUI不关闭其他GUI
	 */
	@Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
	private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
		GuiScreen child = LocalChildFrame.getContainer();
		if ((keyCode == 1
				|| Minecraft.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
				&& child != null) {
			LocalChildFrame.closeGUI();
			//noinspection ConstantConditions
			if (((Object) this) != child) setFocused(true);
			ci.cancel();
		}
	}
	
	public void setFocused(boolean hasFocusedControlIn) { }
	
}