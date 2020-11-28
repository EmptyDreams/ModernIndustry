package xyz.emptydreams.mi.coremod.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.emptydreams.mi.api.gui.craft.CraftFrame;

/**
 * @author EmptyDreams
 */
@Mixin(FMLNetworkHandler.class)
public class MixinFMLNetworkHandler {
	
	@Inject(method = "openGui", at = @At("HEAD"), cancellable = true)
	private static void openGui(EntityPlayer entityPlayer, Object mod, int modGuiId,
	                            World world, int x, int y, int z, CallbackInfo ci) {
		if (entityPlayer instanceof EntityPlayerMP || FMLCommonHandler.instance().getSide().isClient()) {
			CraftFrame.onOpenGui(entityPlayer, mod, x, y, z);
		} else {
			ci.cancel();
		}
	}

}
