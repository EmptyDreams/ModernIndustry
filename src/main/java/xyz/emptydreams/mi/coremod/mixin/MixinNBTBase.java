package xyz.emptydreams.mi.coremod.mixin;

import net.minecraft.nbt.NBTBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.emptydreams.mi.api.nbt.NBTRegister;

/**
 * @author EmptyDreams
 */
@Mixin(NBTBase.class)
public class MixinNBTBase {
	
	/**
	 * @reason 在ID为MI注册的ID时返回MI的NBTBase对象
	 */
	@Inject(method = "createNewByType", at = @At("HEAD"), cancellable = true)
	private static void createNewByType(byte id, CallbackInfoReturnable<NBTBase> cir) {
		if (!(id > 12 || id < 0)) {
			return;
		}
		cir.setReturnValue(NBTRegister.createNBTBase(id));
		cir.cancel();
	}
	
}