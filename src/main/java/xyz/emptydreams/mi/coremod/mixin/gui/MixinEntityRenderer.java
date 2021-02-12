package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("SpellCheckingInspection")
@SideOnly(Side.CLIENT)
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
	
	@Final @Shadow private Minecraft mc;
	
	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE",
			target = "Lnet/minecraftforge/client/ForgeHooksClient;" +
					"drawScreen(Lnet/minecraft/client/gui/GuiScreen;IIF)V"))
	private void updateCameraAndRender_drawScreen(GuiScreen screen,
	                                              int mouseX, int mouseY, float partialTicks) {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? screen : child;
		final ScaledResolution scaledresolution = new ScaledResolution(mc);
		int i1 = scaledresolution.getScaledWidth();
		int j1 = scaledresolution.getScaledHeight();
		final int k1 = Mouse.getX() * i1 / mc.displayWidth;
		final int l1 = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;
		ForgeHooksClient.drawScreen(container, k1, l1, mc.getTickLength());
	}
	
	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE",
			target = "Ljava/lang/Object;getClass()Ljava/lang/Class;"))
	private Class<?> updateCameraAndRender_getClass(Object o) {
		GuiScreen child = LocalChildFrame.getContainer();
		Object container = child == null ? o : child;
		return container.getClass();
	}
	
}