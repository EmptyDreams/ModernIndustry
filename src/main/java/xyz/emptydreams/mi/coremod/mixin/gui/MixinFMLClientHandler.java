package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiNotification;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;

import java.io.IOException;

/**
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
@Mixin(FMLClientHandler.class)
public class MixinFMLClientHandler {
	
	@Shadow(remap = false) private Minecraft client;
	
	/**
	 * @reason 替换客户端GUI渲染方法
	 * @author EmptyDreams
	 */
	@Overwrite(remap = false)
	public boolean handleLoadingScreen(ScaledResolution scaledResolution) throws IOException {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? client.currentScreen : child;
		if (container instanceof GuiNotification) {
			int width = scaledResolution.getScaledWidth();
			int height = scaledResolution.getScaledHeight();
			int mouseX = Mouse.getX() * width / client.displayWidth;
			int mouseZ = height - Mouse.getY() * height / client.displayHeight - 1;
			container.drawScreen(mouseX, mouseZ, 0);
			container.handleInput();
			return true;
		} else {
			return false;
		}
	}
	
}