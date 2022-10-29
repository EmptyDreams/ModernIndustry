package top.kmar.mi.api.regedits.block;

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
import top.kmar.mi.api.exception.TransferException;
import top.kmar.mi.api.regedits.machines.BlockRegistryMachine;
import top.kmar.mi.api.utils.MISysInfo;

import java.lang.reflect.Method;

/**
 * 方块注册类
 * @author EmptyDremas
 */
@Mod.EventBusSubscriber
public class BlockRegister {
	
	@SubscribeEvent
	public static void registryBlock(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> register = event.getRegistry();
		for (Block b : BlockRegistryMachine.Blocks.autoRegister) {
			register.register(b);
		}
		/* 注册拥有独立注册机的方块 */
		for (Class<?> c : BlockRegistryMachine.Blocks.selfRegister.keySet()) {
			try {
				c.getDeclaredMethod("registry", IForgeRegistry.class, Block.class)
						.invoke(null, register, BlockRegistryMachine.Blocks.selfRegister.get(c));
			} catch (Exception e) {
				throw TransferException.instance("方块注册异常", e);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registryModel(ModelRegistryEvent event) {
		BlockRegistryMachine.Blocks.selfRegister.values().forEach(BlockRegister::registryModelHelp);
		BlockRegistryMachine.Blocks.autoRegister.forEach(BlockRegister::registryModelHelp);
	}

	private static void registryModelHelp(Block block) {
		if (BlockRegistryMachine.Blocks.noItem.contains(block)) return;
		String methodName = BlockRegistryMachine.Blocks.customModelBlocks.getOrDefault(block, null);
		if (methodName == null) {
			Item item = getItem(block);
			ModelResourceLocation model = new ModelResourceLocation(block.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, model);
		} else if (!methodName.equals("null")) {
			try {
				Method method = block.getClass().getMethod(methodName, Block.class, Item.class);
				method.invoke(null, block, getItem(block));
			} catch (Exception e) {
				MISysInfo.err("注册[" + block.getRegistryName() + "]的模型时出现意外的错误", e);
			}
		}
	}
	
	@SubscribeEvent
	public static void registryItem(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		for(Block b : BlockRegistryMachine.Blocks.autoRegister) {
			if (BlockRegistryMachine.Blocks.noItem.contains(b)) continue;
			Item item = getItem(b);
			registry.register(item);
		}
		for (Block b : BlockRegistryMachine.Blocks.selfRegister.values()) {
			if (BlockRegistryMachine.Blocks.noItem.contains(b)) continue;
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