package xyz.emptydreams.mi.register;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleTransfer;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.proxy.ClientProxy;
import xyz.emptydreams.mi.proxy.CommonProxy;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;
import xyz.emptydreams.mi.register.block.OreCreate;
import xyz.emptydreams.mi.register.block.WorldCreater;
import xyz.emptydreams.mi.register.item.AutoItemRegister;
import xyz.emptydreams.mi.register.json.BlockJsonBuilder;
import xyz.emptydreams.mi.register.json.ItemJsonBuilder;
import xyz.emptydreams.mi.register.sorter.BlockSorter;
import xyz.emptydreams.mi.register.sorter.ItemSorter;
import xyz.emptydreams.mi.register.tileentity.AutoTileEntity;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 自动注册的总类，自动注册的功能由init()函数完成，该类的注册顺序如下：
 * <pre>
 * 1.被{@link AutoBlockRegister}注解的方块
 * 2.被{@link RegisterManager}注解的类
 * 3.被{@link AutoTileEntity}注解的TileEntity
 * 4.被{@link AutoItemRegister}注解的物品
 * 5.被{@link AutoManager}注解的类
 * 6.被{@link OreCreate}注解的矿石生成器
 * 7.被{@link AutoTrusteeshipRegister}注解的托管
 * 8.被{@link AutoLoader}注解的类
 * </pre>
 * @author EmptyDremas
 */
public final class AutoRegister {
	
	public static final class Blocks {
		/** 所有方块 */
		public static final List<Block> blocks = new ArrayList<>(50);
		/** 需要自动注册的方块 */
		public static final List<Block> autoRegister = new ArrayList<>(50);
		/** 不需要自动注册的方块的注册地址 */
		public static final HashMap<Class<?>, Block> selfRegister = new HashMap<>();
		/** 地图生成方块 */
		public static final HashMap<Block, WorldGenerator> worldCreate = new HashMap<>();
		/** 不注册物品的方块 */
		public static final Set<Block> noItem = new TreeSet<>(BlockSorter::compare);
	}
	
	public static final class Items {
		/** 所有物品 */
		public static final List<Item> items = new ArrayList<>(50);
		/** 需要注册的物品 */
		public static final List<Item> autoItems = new ArrayList<>(50);
	}
	
	public static boolean isRun = false;
	
	public static void init() {
		
		if (isRun) return;
		isRun = true;
		//是否为客户端
		final boolean client = WorldUtil.isClient(null);
		
		try {
			//注册debug物品
			addAutoItem(ModernIndustry.DEBUG);
			final ASMDataTable ASM = client ? ClientProxy.getAsm() : CommonProxy.getAsm();

			reAutoBlock(ASM);
			reAutoTE(ASM);
			reAutoItem(ASM);
			reManager(ASM);
			reRegisterManager(ASM);
			reOreCreate(ASM);
			reAutoTR(ASM);
			triggerAutoLoader(ASM);

			Blocks.autoRegister.sort(BlockSorter::compare);
			Items.autoItems.sort(ItemSorter::compare);
			
			BlockJsonBuilder.build();
			ItemJsonBuilder.build();
		} catch (IllegalAccessException e) {
			MISysInfo.err("需要的函数不可见，原因可能是：",
							"用户提供的需初始化的类没有提供可视的构造函数");
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			MISysInfo.err("没有找到对应的方法，原因可能可能是：\n",
					              "1).用户的类使用了RegisterManager注解却未在类中定义static register()\n",
							      "2).使用@AutoTrusteeshipRegister注解的托管没有提供默认构造函数\n");
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			MISysInfo.err("反射过程中发生了空指针错误，原因可能是：\n",
							"\t1).本程序内部错误\n",
							"\t2).用户修改本程序导致反射过程中出现空指针");
			throw e;
		} catch (ClassNotFoundException e) {
			MISysInfo.err("反射过程中寻找类时出现错误，原因可能是：\n",
							"\t1).用户或程序内部的类因为某些原因被卸载\n",
							"\t2).用户提供的类路径错误");
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException("发生了未知错误！", e);
		}
	}
	
	/** 添加一个自动注册的方块 */
	public static void addAutoBlock(Block block) {
		addBlock(block);
		Blocks.autoRegister.add(block);
	}
	
	/** 添加一个方块 */
	public static void addBlock(Block block) {
		Blocks.blocks.add(block);
	}
	
	/** 添加一个自动注册的物品 */
	public static void addAutoItem(Item item) {
		Items.autoItems.add(item);
		addItem(item);
	}
	
	/** 添加一个物品 */
	public static void addItem(Item item) {
		Items.items.add(item);
	}

	/** 注册自动注册管理器 */
	private static void reManager(ASMDataTable ASM)
					throws ClassNotFoundException, IllegalAccessException,
							NoSuchMethodException, InvocationTargetException {
		Set<ASMData> classSet = ASM.getAll(AutoManager.class.getName());
		for (ASMData data : classSet) {
			Class<?> clazz = Class.forName(data.getClassName());
			AutoManager manager = clazz.getAnnotation(AutoManager.class);
			for (Field field : clazz.getFields()) {
				if ((manager.block() && Block.class.isAssignableFrom(field.getType()))) {
					if (Modifier.isStatic(field.getModifiers())) {
						Block block = (Block) field.get(null);
						addAutoBlock(block);
						if (manager.blockCustom()) {
							clazz.getDeclaredMethod("blockCustom", Block.class).invoke(null, block);
						}
					}
				} else if (manager.item() && Item.class.isAssignableFrom(field.getType())) {
					if (Modifier.isStatic(field.getModifiers())) {
						Item item = (Item) field.get(null);
						addAutoItem(item);
						if (manager.itemCustom()) {
							clazz.getDeclaredMethod("itemCustom", Item.class).invoke(null, item);
						}
					}
				}
			}
		}
	}
	
	/** 自动加载 */
	private static void triggerAutoLoader(ASMDataTable ASM) throws ClassNotFoundException {
		Set<ASMData> classSet = ASM.getAll(AutoLoader.class.getName());
		for (ASMData data : classSet) {
			Class.forName(data.getClassName());
		}
	}
	
	/** 自动注册托管 */
	private static void reAutoTR(ASMDataTable ASM)
			throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchFieldException {
		Set<ASMData> classSet = ASM.getAll(AutoTrusteeshipRegister.class.getName());
		for (ASMData data : classSet) {
			Class<?> nowClass = Class.forName(data.getClassName());
			Object o = nowClass.newInstance();
			String field = (String) data.getAnnotationInfo().getOrDefault("value", null);
			if (field != null) {
				Field declaredField = nowClass.getDeclaredField(field);
				declaredField.setAccessible(true);
				declaredField.set(o, o);
			}
			boolean isTrue = false;
			if (o instanceof IEleTransfer) {
				EleWorker.registerTransfer((IEleTransfer) o);
				isTrue = true;
			}
			if (o instanceof IEleOutputer) {
				EleWorker.registerOutputer((IEleOutputer) o);
				isTrue = true;
			}
			if (o instanceof IEleInputer) {
				EleWorker.registerInputer((IEleInputer) o);
				isTrue = true;
			}
			if (!isTrue) throw new ClassNotFoundException("该类没有实现三个托管中的任意一个：" + o);
		}
	}
	
	/** 注册管理类 */
	private static void reRegisterManager(ASMDataTable ASM)
					throws ClassNotFoundException, NoSuchMethodException,
							InvocationTargetException, IllegalAccessException {
		Set<ASMData> classSet = ASM.getAll(RegisterManager.class.getName());
		for (ASMData data : classSet) {
			Method method = Class.forName(data.getClassName()).getDeclaredMethod("registry");
			method.setAccessible(true);
			method.invoke(null);
		}
	}
	
	/** 自动注册物品 */
	private static void reAutoItem(ASMDataTable ASM)
					throws ClassNotFoundException, IllegalAccessException,
							InstantiationException, NoSuchFieldException {
		Set<ASMData> classSet = ASM.getAll(AutoItemRegister.class.getName());
		if (classSet != null) {
			for (ASMData data : classSet) {
				Map<String, Object> valueMap = data.getAnnotationInfo();
				String modid = valueMap.getOrDefault("modid", ModernIndustry.MODID).toString();
				String name = valueMap.get("value").toString();
				String field = (String) valueMap.getOrDefault("field", null);
				Class<?> nowClass = Class.forName(data.getClassName());
				String[] ores = (String[]) valueMap.getOrDefault("oreDic", null);
				Item item = (Item) nowClass.newInstance();

				item.setRegistryName(modid, name);
				item.setUnlocalizedName(getUnlocalizedName(valueMap, name, modid));
				if (ores != null)
					for (String ore : ores)
						OreDictionary.registerOre(ore, item);
				addAutoItem(item);
				if (field != null) {
					Field deField = nowClass.getDeclaredField(field);
					deField.setAccessible(true);
					deField.set(null, item);
				}
			}
		}
	}
	
	/** 注册TE */
	@SuppressWarnings("unchecked")
	private static void reAutoTE(ASMDataTable ASM) throws ClassNotFoundException {
		Set<ASMData> classSet = ASM.getAll(AutoTileEntity.class.getName());
		if (classSet != null) {
			for (ASMData data : classSet) {
				Map<String, Object> valueMap = data.getAnnotationInfo();
				GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(data.getClassName()),
						new ResourceLocation(ModernIndustry.MODID, (String) valueMap.get("value")));
			}
		}
	}
	
	/** 矿石生成器 */
	private static void reOreCreate(ASMDataTable ASM) {
		Set<ASMData> classSet = ASM.getAll(OreCreate.class.getName());
		if (classSet != null) {
			Block b = null;
			for (ASMData data : classSet) {
				Map<String, Object> valueMap = data.getAnnotationInfo();
				String name = valueMap.get("name").toString();
				for (Block block : Blocks.blocks) {
					if (block.getRegistryName().getResourcePath().equals(name)) {
						b = block;
						break;
					}
				}
				if (b == null) {
					MISysInfo.err("发现了一个没有对应方块的矿石生成器[",
							valueMap.get("name"), "] -> continue");
					continue;
				}
				Blocks.worldCreate.put(b, new WorldCreater(data, b));
			}
		}
	}
	
	/** 自动注册方块 */
	private static void reAutoBlock(ASMDataTable ASM)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		Set<ASMData> classSet = ASM.getAll(AutoBlockRegister.class.getName());
		if (classSet != null) {
			for (ASMDataTable.ASMData data : classSet) {
				Map<String, Object> valueMap = data.getAnnotationInfo();
				Class<?> nowClass = Class.forName(data.getClassName());
				Block block = (Block) nowClass.newInstance();
				String field = (String) valueMap.getOrDefault("field", null);
				String[] ores = (String[]) valueMap.getOrDefault("oreDic", null);
				String reName = valueMap.get("registryName").toString();
				String modid = valueMap.getOrDefault("modid", ModernIndustry.MODID).toString();

				block.setRegistryName(ModernIndustry.MODID, reName);
				block.setUnlocalizedName(getUnlocalizedName(valueMap, reName, modid));

				Blocks.blocks.add(block);
				if (ores != null)
					for (String ore : ores)
						OreDictionary.registerOre(ore, block);
				if (field != null) {
					Field declaredField = nowClass.getDeclaredField(field);
					declaredField.setAccessible(true);
					declaredField.set(block, block);
				}

				Class<?> register = (Class<?>) valueMap.getOrDefault("register", AutoBlockRegister.REGISTER);
				if (AutoBlockRegister.REGISTER.equals(register))
					Blocks.autoRegister.add(block);
				else
					Blocks.selfRegister.put(register, block);
			}
		}
	}

	/** 获取modid */
	@Nonnull
	public static String getUnlocalizedName(String modid, String name) {
		return modid + "." + name;
	}

	/** 获取modid */
	public static String getUnlocalizedName(String name) {
		return ModernIndustry.MODID + "." + name;
	}

	@Nonnull
	private static String getUnlocalizedName(Map<String, Object> valueMap, String reName, String modid) {
		Object value = valueMap.getOrDefault("unlocalizedName", null);
		if (value == null) return modid + "." + reName;
		return value.toString();
	}
	
}
