package minedreams.mi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import minedreams.mi.blocks.te.AutoTileEntity;
import minedreams.mi.blocks.register.BlockAutoRegister;
import minedreams.mi.blocks.register.BlockRegister;
import minedreams.mi.blocks.wire.WireCopper;
import minedreams.mi.blocks.world.OreCreat;
import minedreams.mi.blocks.world.WorldCreater;
import minedreams.mi.items.register.AutoItemRegister;
import minedreams.mi.items.register.ItemRegister;
import minedreams.mi.items.tools.ToolRegister;
import minedreams.mi.proxy.ClientProxy;
import minedreams.mi.proxy.CommonProxy;
import minedreams.mi.tools.MISysInfo;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author EmptyDremas
 * @version 1.1
 */
public final class AutoRegister {
	
	public static final class Blocks {
		/** 所有方块 */
		public static final HashMap<Integer, Block> blocks = new HashMap<>();
		/** 需要自动注册的方块 */
		public static final ArrayList<Block> autoRegister = new ArrayList<>();
		/** 不需要自动注册的方块的注册地址 */
		public static final HashMap<Class<?>, Block> selfRegister = new HashMap<>();
		/** 地图生成方块 */
		public static final HashMap<Block, WorldCreater> worldCreater = new HashMap<>();
		/** 不注册物品的方块 */
		public static final ArrayList<Block> noItem = new ArrayList<>();
	}
	
	public static final class Items {
		/** 所有物品 */
		public static final HashMap<String, Item> items = new HashMap<>();
		/** 需要注册的物品 */
		public static final ArrayList<Item> autoItems = new ArrayList<>();
	}
	
	static {
		//是否为客户端
		final boolean client = FMLCommonHandler.instance().getSide().isClient();
		
		try {
			MISysInfo.print((WireCopper.BLOCK.getBlockItem() != null) ? "提前加载方块数据......" : null);
			//注册debug物品
			addAutoItem(ModernIndustry.DEBUG);
			
			final String NAME = ModernIndustry.MODID + ":";
			
			int index = 0;
			/* BlockRegister类 */
			Field[] bfs = BlockRegister.class.getFields();
			for (Field f : bfs) {
				Object o = f.get(null);
				if (o instanceof Block) {
					Block b = (Block) o;
					Blocks.blocks.put(--index, b);
					Blocks.autoRegister.add(b);
					if (f.isAnnotationPresent(OreCreat.class)) {
						OreCreat oc = f.getAnnotation(OreCreat.class);
						Blocks.worldCreater.put(b, new WorldCreater(oc, b.getDefaultState()));
					}
				}
			}
			
			final ASMDataTable ASM = client ? ClientProxy.getAsm() : CommonProxy.getAsm();
			Map<String, Object> valueMap;
			Class<?> nowClass;
			
			/* 自动注册方块 */
			Set<ASMData> classSet = ASM.getAll(BlockAutoRegister.class.getName());
			if (classSet != null) {
				for (ASMDataTable.ASMData data : classSet) {
					valueMap = data.getAnnotationInfo();
					nowClass = Class.forName(data.getClassName());
					Block bt = (Block) nowClass.newInstance();
					bt.setRegistryName(ModernIndustry.MODID, (String) valueMap.get("registryName"));
					bt.setUnlocalizedName(bt.getRegistryName().getResourcePath());
					bt.setHardness((float) valueMap.getOrDefault("hardnexx", BlockAutoRegister.HARDNEXX));
					if ((boolean) valueMap.getOrDefault("tab", BlockAutoRegister.TAB))
						bt.setCreativeTab(ModernIndustry.TAB_BLOCK);
					String tool = (String) valueMap.getOrDefault("tool", BlockAutoRegister.TOOL);
					if (!"".equals(tool)) {
						bt.setHarvestLevel(tool, (int) valueMap.getOrDefault("level", BlockAutoRegister.LEVEL));
					}
					Blocks.blocks.put((int) valueMap.get("name"), bt);
					
					Class<?> register = (Class<?>) valueMap.getOrDefault("register", BlockAutoRegister.REGISTER);
					if (BlockAutoRegister.REGISTER.equals(register))
						Blocks.autoRegister.add(bt);
					else
						Blocks.selfRegister.put(register, bt);
				}
			}
			
			classSet = ASM.getAll(OreCreat.class.getName());
			if (classSet != null) {
				Block b;
				for (ASMData data : classSet) {
					valueMap = data.getAnnotationInfo();
					b = findBlock(valueMap.get("name").toString());
					if (b == null) {
						MISysInfo.err("发现了一个没有对应方块的矿石生成器[",
								valueMap.get("name"), "] -> continue");
						continue;
					}
					Blocks.worldCreater.put(b, new WorldCreater(data, b));
				}
			}
			
			classSet = ASM.getAll(AutoTileEntity.class.getName());
			if (classSet != null) {
				for (ASMData data : classSet) {
					valueMap = data.getAnnotationInfo();
					GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(data.getClassName()),
							NAME + valueMap.get("value"));
				}
			}
			
			/* ItemRegister类 */
			Field[] fs = ItemRegister.class.getFields();
			for (Field f : fs) {
				Object o = f.get(null);
		        	if (o instanceof Item) {
		        		Item i = (Item) o;
		        		addAutoItem(i);
		        	}
			}
			fs = ToolRegister.class.getFields();
			for (Field f : fs) {
				Object o = f.get(null);
		        	if (o instanceof Item) {
		        		Item i = (Item) o;
		        		addAutoItem(i);
		        	}
			}
			
			classSet = ASM.getAll(AutoItemRegister.class.getName());
			String ID;
			String name;
			Item item;
			if (classSet != null) {
				for (ASMData data : classSet) {
					valueMap = data.getAnnotationInfo();
					ID = valueMap.getOrDefault("ID", AutoItemRegister.ID).toString();
					name = valueMap.get("value").toString();
					nowClass = Class.forName(data.getClassName());
					item = (Item) nowClass.newInstance();
					
					item.setRegistryName(ID, name);
					item.setUnlocalizedName(name);
					addAutoItem(item, name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("在反射过程中发生了意料之外的错误！");
		}
	}
	
	private static Block findBlock(String name) {
		for (Block b : Blocks.blocks.values()) {
			if (b.getRegistryName().getResourcePath().equals(name))
				return b;
		}
		return null;
	}
	
	/** 添加一个自动注册的方块 */
	public static void addAutoBlock(Block block) {
		addAutoBlock(block, BlockRegister.next());
	}
	
	/** 添加一个自动注册的方块 */
	public static void addAutoBlock(Block block, int index) {
		addBlock(block, index);
		Blocks.autoRegister.add(block);
	}
	
	/** 添加一个方块 */
	public static void addBlock(Block block, int index) {
		Blocks.blocks.put(index, block);
	}
	
	/** 添加一个方块 */
	public static void addBlock(Block block) {
		addBlock(block, BlockRegister.next());
	}
	
	/** 添加一个自动注册的物品 */
	public static void addAutoItem(Item item) {
		addAutoItem(item, item.getRegistryName().getResourcePath());
	}
	
	/** 添加一个自动注册的物品 */
	public static void addAutoItem(Item item, String name) {
		Items.autoItems.add(item);
		addItem(item, name);
	}
	
	/** 添加一个物品 */
	public static void addItem(Item item, String name) {
		Items.items.put(name, item);
	}
	
}
