package xyz.emptydreams.mi.register.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import static xyz.emptydreams.mi.register.AutoRegister.Blocks;

/**
 * 方块注册类
 * @author EmptyDremas
 */
@Mod.EventBusSubscriber
public class BlockRegister {
	
	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event) {
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
	public static void registerItem(RegistryEvent.Register<Item> event) {
		if (WorldUtil.isServer(null)) {
			registerItemOnServer(event);
		} else {
			registerItemOnClint(event);
		}
	}

	/** 服务端注册物品 */
	private static void registerItemOnServer(RegistryEvent.Register<Item> event) {
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

	/** 客户端注册物品 */
	@SideOnly(Side.CLIENT)
	private static void registerItemOnClint(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		for(Block b : Blocks.autoRegister) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			registry.register(item);
			ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, model);
		}
		for (Block b : Blocks.selfRegister.values()) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			registry.register(item);
			ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, model);
		}
	}

	private static Item getItem(Block block) {
		//noinspection IfStatementWithIdenticalBranches
		if (block instanceof BlockItemHelper) {
			Item item = ((BlockItemHelper) block).getBlockItem();
			if (item.getRegistryName() == null) {
				item.setRegistryName(block.getRegistryName());
			}
			return item;
		} else {
			Item item = new ItemBlock(block);
			item.setRegistryName(block.getRegistryName());
			return item;
		}
	}

}
