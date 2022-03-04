package top.kmar.mi.coremod.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.kmar.mi.ModernIndustry;

/**
 * @author EmptyDreams
 */
@Mixin(UniversalBucket.class)
public abstract class MixinUniversalBucket {
	
	@Inject(method = "getItemStackDisplayName", at = @At("HEAD"), cancellable = true, remap = false)
	private void getItemStackDisplayName(ItemStack stack, CallbackInfoReturnable<String> cir) {
		FluidStack fluidStack = getFluid(stack);
		if (fluidStack == null) return;
		Block block = fluidStack.getFluid().getBlock();
		if (!block.getRegistryName().getResourceDomain().equals(ModernIndustry.MODID)) return;
		cir.setReturnValue(fluidStack.getLocalizedName());
	}
	
	@Shadow(remap = false)
	public abstract FluidStack getFluid(ItemStack stack);
	
}