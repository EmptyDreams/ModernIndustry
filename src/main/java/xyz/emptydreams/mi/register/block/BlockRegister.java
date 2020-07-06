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
import xyz.emptydreams.mi.blocks.common.OreBlock;
import xyz.emptydreams.mi.register.item.ItemRegister;

import java.lang.reflect.InvocationTargetException;

import static xyz.emptydreams.mi.register.AutoRegister.Blocks;

/**
 * 方块注册类
 * @author EmptyDremas
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class BlockRegister {
	
	/** 铜矿石 */
	@OreCreat(yRange = 76 - 16, count = 11, name = OreBlock.NAME_COPPER)
	public static final OreBlock ORE_COPPER = new OreBlock(OreBlock.NAME_COPPER, ItemRegister.ITEM_COPPER_POWDER);
	/** 锡矿石 */
	@SuppressWarnings("unused")
	@OreCreat(yRange = 70 - 16, count = 7, time = 3, name = OreBlock.NAME_TIN)
	public static final OreBlock ORE_TIN = new OreBlock(OreBlock.NAME_TIN, ItemRegister.ITEM_TIN_POWER);
	
	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> register = event.getRegistry();
		for (Block b : Blocks.autoRegister) {
			register.register(b);
		}
		/* 注册带有DonnotRegister注解的方块 */
		for (Class<?> c : Blocks.selfRegister.keySet()) {
			try {
				c.getDeclaredMethod("register", IForgeRegistry.class, Block.class)
						.invoke(null, register, Blocks.selfRegister.get(c));
			} catch (NoSuchMethodException | SecurityException |
					IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				throw new RuntimeException("方块注册异常");
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

	private static void registerItemOnServer(RegistryEvent.Register<Item> event) {
		for(Block b : Blocks.autoRegister) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			event.getRegistry().register(item);
		}
		for (Block b : Blocks.selfRegister.values()) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			event.getRegistry().register(item);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemOnClint(RegistryEvent.Register<Item> event) {
		for(Block b : Blocks.autoRegister) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			event.getRegistry().register(item);
			ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, model);
		}
		for (Block b : Blocks.selfRegister.values()) {
			if (Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			event.getRegistry().register(item);
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
