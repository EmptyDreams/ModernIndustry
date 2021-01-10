package xyz.emptydreams.mi.api.register.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import static xyz.emptydreams.mi.api.register.AutoRegister.Blocks;

/**
 * 方块注册类
 * @author EmptyDremas
 */
@Mod.EventBusSubscriber
public class BlockRegister {
	
	@SubscribeEvent
	public static void registryBlock(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> register = event.getRegistry();
		for (Block b : Blocks.autoRegister) {
			register.register(b);
		}
		/* 注册拥有独立注册机的方块 */
		for (Class<?> c : Blocks.selfRegister.keySet()) {
			try {
				c.getDeclaredMethod("register", IForgeRegistry.class, Block.class)
						.invoke(null, register, Blocks.selfRegister.get(c));
			} catch (Exception e) {
				throw new RuntimeException("方块注册异常", e);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registryModel(ModelRegistryEvent event) {
		for (Block b : Blocks.selfRegister.values()) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, model);
		}
		for(Block b : Blocks.autoRegister) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, model);
		}
	}

	@SubscribeEvent
	public static void registryItem(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		for(Block b : Blocks.autoRegister) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			registry.register(item);
		}
		for (Block b : Blocks.selfRegister.values()) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			registry.register(item);
		}
	}

	@SuppressWarnings("ConstantConditions")
	private static Item getItem(Block block) {
		//noinspection IfStatementWithIdenticalBranches
		if (block instanceof BlockItemHelper) {
			Item item = ((BlockItemHelper) block).getBlockItem();
			if (item.getRegistryName() == null)
				item.setRegistryName(block.getRegistryName());
			return item;
		} else {
			Item item = Item.getItemFromBlock(block);
			if (item == Items.AIR) item = new ItemBlock(block);
			if (item.getRegistryName() == null)
				item.setRegistryName(block.getRegistryName());
			return item;
		}
	}

}