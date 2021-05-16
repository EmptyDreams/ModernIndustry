package xyz.emptydreams.mi.api.register.item;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IRegistryDelegate;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.register.machines.ItemRegistryMachine;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 物品注册机
 * @author EmptyDremas
 */
@Mod.EventBusSubscriber
public class ItemRegister {

	private static final Map<IRegistryDelegate<Item>, ItemMeshDefinition> customMeshDefinitions;
	
	static {
		try {
			Field field = ModelLoader.class.getDeclaredField("customMeshDefinitions");
			field.setAccessible(true);
			//noinspection unchecked
			customMeshDefinitions = (Map<IRegistryDelegate<Item>, ItemMeshDefinition>) field.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new TransferException(e);
		}
	}
	
	@SubscribeEvent
	public static void registryItems(@Nonnull RegistryEvent.Register<Item> event) {
		MISysInfo.print("注册MOD物品......");
		IForgeRegistry<Item> register = event.getRegistry();
		for (Item i : ItemRegistryMachine.Items.autoItems) register.register(i);
	}

	@SubscribeEvent
	public static void registryModel(ModelRegistryEvent event) {
		for (Item item : ItemRegistryMachine.Items.autoItems) {
			if (customMeshDefinitions.containsKey(item.delegate)) continue;
			ModelLoader.setCustomModelResourceLocation(
					item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}

}