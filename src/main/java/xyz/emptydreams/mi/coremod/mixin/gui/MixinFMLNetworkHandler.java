package xyz.emptydreams.mi.coremod.mixin.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.emptydreams.mi.api.event.PlayerOpenGuiEvent;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("SpellCheckingInspection")
@Mixin(FMLNetworkHandler.class)
public class MixinFMLNetworkHandler {
	
	/**
	 * @reason 在玩家打开GUI时触发事件
	 */
	@Redirect(method = "openGui", remap = false,
			at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/NetworkRegistry;" +
					"getLocalGuiContainer(Lnet/minecraftforge/fml/common/ModContainer;" +
												"Lnet/minecraft/entity/player/EntityPlayer;" +
												"ILnet/minecraft/world/World;III)Ljava/lang/Object;"))
	private static Object openGui_getLocalGuiContainer(NetworkRegistry networkRegistry, ModContainer mc,
	                              EntityPlayer player, int modGuiId, World world, int x, int y, int z) {
		MinecraftForge.EVENT_BUS.post(new PlayerOpenGuiEvent(mc, player, modGuiId, world, x, y, z));
		return networkRegistry.getLocalGuiContainer(mc, player, modGuiId, world, x, y, z);
	}
	
	/**
	 * @reason 在玩家打开GUI时触发事件
	 */
	@Redirect(method = "openGui", remap = false,
			at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/NetworkRegistry;" +
					"getRemoteGuiContainer(Lnet/minecraftforge/fml/common/ModContainer;" +
					"Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraft/world/World;III)" +
					"Lnet/minecraft/inventory/Container;"))
	private static Container openGui_getRemoteGuiContainer(NetworkRegistry networkRegistry, ModContainer mc,
	                                  EntityPlayerMP player, int modGuiId,
	                                  World world, int x, int y, int z) {
		MinecraftForge.EVENT_BUS.post(new PlayerOpenGuiEvent(mc, player, modGuiId, world, x, y, z));
		return networkRegistry.getRemoteGuiContainer(mc, player, modGuiId, world, x, y, z);
	}
	
}