package xyz.emptydreams.mi.coremod.mixin.debug;

import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.OpenGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.content.gui.ClassInfoViewerFrame;
import xyz.emptydreams.mi.content.net.ClassInfoViewerMessage;
import xyz.emptydreams.mi.coremod.other.MixinUtil;

import java.lang.reflect.Field;

/**
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
@Mixin(OpenGuiHandler.class)
public class MixinOpenGuiHandler {
	
	@Inject(method = "process", at = @At(("HEAD")), cancellable = true, remap = false)
	private void process_wait(FMLMessage.OpenGui msg, CallbackInfo ci) {
		try {
			Class<FMLMessage.OpenGui> clazz = FMLMessage.OpenGui.class;
			Field modidField = clazz.getDeclaredField("modId");
			modidField.setAccessible(true);
			String modid = (String) modidField.get(msg);
			if (modid.equals(ModernIndustry.MODID)) {
				Field guiIdField = clazz.getDeclaredField("modGuiId");
				guiIdField.setAccessible(true);
				int guiId = guiIdField.getInt(msg);
				if (guiId == GuiLoader.getID(ClassInfoViewerFrame.NAME)) {
					ci.cancel();
					modidField.set(msg, "内部" + modid);
					MixinUtil.msg = msg;
					//noinspection ConstantConditions
					MixinUtil.handler = (OpenGuiHandler) (Object) this;
				}
			} else if (modid.startsWith("内部") && ClassInfoViewerMessage.isUpdate()) {
				modidField.set(msg, modid.substring(2));
			}
		} catch (Exception e) {
			throw TransferException.instance(e);
		}
	}
	
}