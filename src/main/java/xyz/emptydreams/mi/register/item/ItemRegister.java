package xyz.emptydreams.mi.register.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.blocks.common.OreBlock;
import xyz.emptydreams.mi.items.tools.ToolRegister;
import xyz.emptydreams.mi.register.AutoRegister;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.items.common.CommonItems.*;

/**
 * @author EmptyDremas
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class ItemRegister {
	
	/**
	 * 注册物品以及合成表
	 */
	@SubscribeEvent
	public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
		MISysInfo.print("注册MOD物品......");
		
		IForgeRegistry<Item> register = event.getRegistry();
	   if (FMLCommonHandler.instance().getSide().isClient()) {
		   for (Item i : AutoRegister.Items.autoItems) {
		    	register.register(i);
				ModelLoader.setCustomModelResourceLocation(
						i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
		    }
	   } else {
		   for (Item i : AutoRegister.Items.autoItems) {
		    	register.register(i);
		    }
	   }

	    for (OreBlock block : OreBlock.LIST.values()) {
	    	GameRegistry.addSmelting(block.getBlockItem(), new ItemStack(OreBlock.LIST.get(block)), 0.5F);
	    }
	}

}
