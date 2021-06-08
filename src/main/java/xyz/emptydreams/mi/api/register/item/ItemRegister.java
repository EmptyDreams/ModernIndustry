package xyz.emptydreams.mi.api.register.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.emptydreams.mi.api.register.machines.ItemRegistryMachine;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

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
		for (Item i : ItemRegistryMachine.Items.autoItems) register.register(i);
	}

	@SubscribeEvent
	public static void registryModel(ModelRegistryEvent event) {
		for (Item item : ItemRegistryMachine.Items.autoItems) {
			String methodName = ItemRegistryMachine.Items.customModelItems.getOrDefault(item, null);
			if (methodName == null) {
				ModelLoader.setCustomModelResourceLocation(
						item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			} else if (!methodName.equals("null")) {
				try {
					Method method = item.getClass().getMethod(methodName, Item.class);
					method.invoke(null, item);
				} catch (Exception e) {
					MISysInfo.err("注册[" + item.getRegistryName() + "]的customModel时出现意外的错误", e);
				}
			}
		}
	}

}