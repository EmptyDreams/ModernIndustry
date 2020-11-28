package xyz.emptydreams.mi.coremod.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author EmptyDreams
 */
@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {
	
	@Inject(method = "closeContainer", at = @At("HEAD"))
	private void onCloseContainer(CallbackInfo ci) {
		EntityPlayerMP player = (EntityPlayerMP) (Object) this;
		//CraftFrame.onCloseGui(player);
	}
	
}
