package minedreams.mi.register.block;

import static minedreams.mi.register.AutoRegister.Blocks;

import java.lang.reflect.InvocationTargetException;

import minedreams.mi.ModernIndustry;
import minedreams.mi.blocks.ore.OreBlock;
import minedreams.mi.blocks.world.OreCreat;
import minedreams.mi.register.item.ItemRegister;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * 方块注册类
 * @author EmptyDremas
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class BlockRegister {
	
	private static int nextID = Integer.MAX_VALUE / 2;
	
	/** 自动分配ID */
	public static int next() {
		return ++nextID;
	}
	
	/** 压缩机 */
	public static final int COMPRESSOR_TBLOCK = 1;
	/** 铜线 */
	public static final int WIRE_COPPER = 2;
	
	/** 铜矿石 */
	@OreCreat(yRange = 76 - 16, name = OreBlock.NAME_COPPER)
	public static final OreBlock ORE_COPPER = new OreBlock(OreBlock.NAME_COPPER, ItemRegister.ITEM_COPPER_POWDER);
	/** 锡矿石 */
	@OreCreat(yRange = 70 - 16, count = 5, time = 3, name = OreBlock.NAME_TIN)
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
		if (FMLCommonHandler.instance().getSide().isClient()) {
			for(Block b : Blocks.autoRegister) {
				if (Blocks.noItem.contains(b))
					continue;
				if (b instanceof BlockItemHelper) {
					Item item = ((BlockItemHelper) b).getBlockItem();
					event.getRegistry().register(item);
					ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
					ModelLoader.setCustomModelResourceLocation(item, 0, model);
				} else {
					Item item = new ItemBlock(b);
					item.setRegistryName(ModernIndustry.MODID, b.getRegistryName().getResourcePath());
					event.getRegistry().register(item);
					ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
					ModelLoader.setCustomModelResourceLocation(item, 0, model);
				}
			}
			for (Block b : Blocks.selfRegister.values()) {
				if (Blocks.noItem.contains(b)) {
					continue;
				}
				Item item;
				if (b instanceof BlockItemHelper)
					item = ((BlockItemHelper) b).getBlockItem();
				else
					item = Item.getItemFromBlock(b);
				event.getRegistry().register(item);
				ModelResourceLocation model = new ModelResourceLocation(b.getRegistryName(), "inventory");
				ModelLoader.setCustomModelResourceLocation(item, 0, model);
			}
		} else {
			for(Block b : Blocks.autoRegister) {
				if (Blocks.noItem.contains(b))
					continue;
				if (b instanceof BlockItemHelper) {
					Item item = ((BlockItemHelper) b).getBlockItem();
					event.getRegistry().register(item);
				} else {
					Item item = new ItemBlock(b);
					item.setRegistryName(ModernIndustry.MODID, b.getRegistryName().getResourcePath());
					event.getRegistry().register(item);
				}
			}
			for (Block b : Blocks.selfRegister.values()) {
				if (Blocks.noItem.contains(b))
					continue;
				Item item;
				if (b instanceof BlockItemHelper)
					item = ((BlockItemHelper) b).getBlockItem();
				else
					item = Item.getItemFromBlock(b);
				event.getRegistry().register(item);
			}
		}
	}
	
	public static Block getBlock(int index) {
		return Blocks.blocks.get(index);
	}
	
}
