package xyz.emptydreams.mi.register.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.register.AutoRegister;

import javax.annotation.Nonnull;

/**
 * 物品注册机
 * @author EmptyDremas
 */
@Mod.EventBusSubscriber
public class ItemRegister {

	@SubscribeEvent
	public static void registryItems(@Nonnull RegistryEvent.Register<Item> event) {
		MISysInfo.print("注册MOD物品......");
		IForgeRegistry<Item> register = event.getRegistry();
		for (Item i : AutoRegister.Items.autoItems) register.register(i);
	}

	@SubscribeEvent
	public static void registryModel(ModelRegistryEvent event) {
		for (Item item : AutoRegister.Items.autoItems) {
			ModelLoader.setCustomModelResourceLocation(
					item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}

}
