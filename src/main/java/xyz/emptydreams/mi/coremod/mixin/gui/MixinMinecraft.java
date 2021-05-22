package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;

import java.io.File;
import java.io.IOException;

/**
 * 修改Minecraft类以实现子GUI的功能
 * @author EmptyDreams
 */
@SuppressWarnings("SpellCheckingInspection")
@SideOnly(Side.CLIENT)
@Mixin(Minecraft.class)
public class MixinMinecraft {
	
	@Shadow public GuiScreen currentScreen;
	@Shadow public GameSettings gameSettings;
	@Shadow public GuiIngame ingameGUI;
	@Final @Shadow public File mcDataDir;
	@Shadow public int displayWidth;
	@Shadow public int displayHeight;
	@Shadow private Framebuffer framebufferMc;
	
	/**
	 * 拦截GUI硬件输入
	 * @reason 为了触发子GUI的硬件输入
	 */
	@Redirect(method = "runTick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleInput()V"), remap = false)
	private void runTick_handleInput(GuiScreen guiScreen) throws IOException {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? guiScreen : child;
		container.handleInput();
	}
	
	/**
	 * 拦截GUI图像更新的方法
	 * @reason 为了触发子GUI的方法
	 */
	@Redirect(method = "runTick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;updateScreen()V"), remap = false)
	private void runTick_updateScreen(GuiScreen guiScreen) {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? guiScreen : child;
		container.updateScreen();
	}
	
	/**
	 * 拦截键盘输入的方法
	 * @reason 为了触发子GUI的方法
	 */
	@Redirect(method = "runTickKeyboard",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"), remap = false)
	private void runTickKeyboard_handleKeyboardInput(GuiScreen guiScreen) throws IOException {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? guiScreen : child;
		container.handleKeyboardInput();
	}
	
	/**
	 * 拦截鼠标输入的方法
	 * @reason 为了触发子GUI的方法
	 */
	@Redirect(method = "runTickMouse",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"), remap = false)
	private void runTickMouse_handleMouseInput(GuiScreen guiScreen) throws IOException {
		GuiScreen child = LocalChildFrame.getContainer();
		GuiScreen container = child == null ? guiScreen : child;
		container.handleMouseInput();
	}
	
	/**
	 * 监控关闭GUI的方法
	 * @reason 为了实现当GUI被关闭时子GUI同时关闭的功能
	 */
	@Inject(method = "displayGuiScreen", at = @At("HEAD"), remap = false)
	private void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
		if (guiScreenIn == null) {
			LocalChildFrame.closeGUI();
		}
	}
	
	/**
	 * 不太清楚这个方法干什么的，总之拦截掉就对了
	 * @reason 防止BUG
	 * @author EmptyDreams
	 */
	@Overwrite(remap = false)
	public void dispatchKeypresses() {
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
		if (Keyboard.isRepeatEvent()) return;
		GuiScreen container = LocalChildFrame.getContainer();
		GuiScreen currentScreen = container == null ? this.currentScreen : container;
		if (!(currentScreen instanceof GuiControls)
				|| ((GuiControls)currentScreen).time <= Minecraft.getSystemTime() - 20L) {
			if (Keyboard.getEventKeyState()) {
				if (gameSettings.keyBindFullscreen.isActiveAndMatches(i)) {
					toggleFullscreen();
				} else if (gameSettings.keyBindScreenshot.isActiveAndMatches(i)) {
					ingameGUI.getChatGUI().printChatMessage(
							ScreenShotHelper.saveScreenshot(mcDataDir,
									displayWidth, displayHeight, framebufferMc));
				} else if (i == 48 && GuiScreen.isCtrlKeyDown()
						&& (currentScreen == null || !currentScreen.isFocused())) {
					gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);
					if (currentScreen instanceof ScreenChatOptions) {
						((ScreenChatOptions)currentScreen).updateNarratorButton();
					}
				}
			} else if (currentScreen instanceof GuiControls) {
				((GuiControls)currentScreen).buttonId = null;
			}
		}
	}
	
	public void toggleFullscreen() { }
	
}