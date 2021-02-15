package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.emptydreams.mi.api.gui.common.ChildFrame;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("SpellCheckingInspection")
@Mixin(FMLNetworkHandler.class)
public class MixinFMLNetworkHandler {
	
	/**
	 * @reason 在玩家打开GUI时记录信息
	 */
	@Redirect(method = "openGui", remap = false,
			at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/NetworkRegistry;" +
					"getLocalGuiContainer(Lnet/minecraftforge/fml/common/ModContainer;" +
												"Lnet/minecraft/entity/player/EntityPlayer;" +
												"ILnet/minecraft/world/World;III)Ljava/lang/Object;"))
	private static Object openGui(NetworkRegistry networkRegistry, ModContainer mc,
	                              EntityPlayer player, int modGuiId, World world, int x, int y, int z) {
		ChildFrame.setGui(world, new BlockPos(x, y, z), player);
		return networkRegistry.getLocalGuiContainer(mc, player, modGuiId, world, x, y, z);
	}
	
}