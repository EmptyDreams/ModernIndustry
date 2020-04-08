package minedreams.mi.register;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;
import minedreams.mi.blocks.world.OreCreat;
import minedreams.mi.blocks.world.WorldCreater;
import minedreams.mi.items.tools.ToolRegister;
import minedreams.mi.proxy.ClientProxy;
import minedreams.mi.proxy.CommonProxy;
import minedreams.mi.register.block.AutoBlockRegister;
import minedreams.mi.register.block.BlockRegister;
import minedreams.mi.register.item.AutoItemRegister;
import minedreams.mi.register.item.ItemRegister;
import minedreams.mi.register.te.AutoTileEntity;
import minedreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import minedreams.mi.utils.MISysInfo;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static minedreams.mi.ModernIndustry.MODID;

/**
 * 自动注册的总类，自动注册的功能由init()函数完成，该类的运行架构如下：
 * <pre>
 *         开始---->>注册debug物品---->>注册{@link BlockRegister}类中的方块
 *                                                 |
 *                                                 ↓
 * 注册被{@link OreCreat}注解的矿石生成器<<----注册被{@link AutoBlockRegister}注解的方块
 *            |
 *            ↓
 * 注册被{@link AutoTileEntity}注解的TE---->>注册{@link ItemRegister}类中的物品
 *                                            |
 *                                            ↓
 * 注册被{@link AutoItemRegister}注解的物品<<----注册{@link ToolRegister}类中的物品
 *                |
 *                ↓
 * 运行被{@link RegisterManager}注解的注册管理类---->>注册被{@link AutoTrusteeshipRegister}注解的托管
 * </pre>
 *
 * @author EmptyDremas
 * @version 2.1
 */
public final class AutoRegister {
	
	public static final class Blocks {
		/** 所有方块 */
		public static final List<Block> blocks = new ArrayList<>(50);
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
		//public static final HashMap<String, Item> items = new HashMap<>();
		public static final List<Item> items = new ArrayList<>(50);
		/** 需要注册的物品 */
		public static final ArrayList<Item> autoItems = new ArrayList<>();
	}
	
	public static boolean isRun = false;
	
	public static void init() {
		if (isRun) return;
		isRun = true;
		//是否为客户端
		final boolean client = FMLCommonHandler.instance().getSide().isClient();
		
		try {
			//注册debug物品
			addAutoItem(ModernIndustry.DEBUG);
			final ASMDataTable ASM = client ? ClientProxy.getAsm() : CommonProxy.getAsm();
			
			regBlockRegister();
			reAutoBlock(ASM);
			reOreCreater(ASM);
			reAutoTE(ASM);
			reItemRegister();
			reAutoItem(ASM);
			reRegisterManager(ASM);
			reAutoTR(ASM);
			triggerAutoLoader(ASM);
			
		} catch (IllegalAccessException e) {
			MISysInfo.err("需要的函数不可见，原因可能是：",
							"用户提供的需初始化的类没有提供可视的构造函数");
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			MISysInfo.err("没有找到对应的方法，原因可能可能是：\n",
					              "1).用户的类使用了RegisterManager注解却未在类中定义static register()\n",
							      "2).使用@AutoTrusteeshipRegister注解的托管没有提供默认构造函数");
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
	
	private static void triggerAutoLoader(ASMDataTable ASM) throws ClassNotFoundException {
		Set<ASMData> classSet = ASM.getAll(AutoLoader.class.getName());
		for (ASMData data : classSet) {
			Class.forName(data.getClassName());
		}
	}
	
	/* 自动注册托管 */
	private static void reAutoTR(ASMDataTable ASM)
			throws NoSuchMethodException, IllegalAccessException,
					       InvocationTargetException, InstantiationException, ClassNotFoundException {
		Set<ASMData> classSet = ASM.getAll(AutoTrusteeshipRegister.class.getName());
		for (ASMData data : classSet) {
			Constructor<?> con = Class.forName(data.getClassName()).getConstructor();
			con.setAccessible(true);
			Object o = con.newInstance();
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
	
	/* 注册管理类 */
	private static void reRegisterManager(ASMDataTable ASM)
			throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		Set<ASMData> classSet = ASM.getAll(RegisterManager.class.getName());
		for (ASMData data : classSet) {
			Method method = Class.forName(data.getClassName()).getDeclaredMethod("register");
			method.setAccessible(true);
			method.invoke(null);
		}
	}
	
	/** 自动注册物品 */
	private static void reAutoItem(ASMDataTable ASM)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Set<ASMData> classSet = ASM.getAll(AutoItemRegister.class.getName());
		Map<String, Object> valueMap;
		Class<?> nowClass;
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
				addAutoItem(item);
			}
		}
	}
	
	/* ItemRegister类 */
	private static void reItemRegister() throws IllegalAccessException {
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
	}
	
	/* 注册TE */
	private static void reAutoTE(ASMDataTable ASM) throws ClassNotFoundException {
		Set<ASMData> classSet = ASM.getAll(AutoTileEntity.class.getName());
		Map<String, Object> valueMap;
		if (classSet != null) {
			for (ASMData data : classSet) {
				valueMap = data.getAnnotationInfo();
				GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(data.getClassName()),
						new ResourceLocation(MODID, (String) valueMap.get("value")));
			}
		}
	}
	
	/* 矿石生成器 */
	private static void reOreCreater(ASMDataTable ASM) {
		Set<ASMData> classSet = ASM.getAll(OreCreat.class.getName());
		Map<String, Object> valueMap;
		if (classSet != null) {
			Block b = null;
			for (ASMData data : classSet) {
				valueMap = data.getAnnotationInfo();
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
				Blocks.worldCreater.put(b, new WorldCreater(data, b));
			}
		}
	}
	
	/* 自动注册方块 */
	private static void reAutoBlock(ASMDataTable ASM)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Set<ASMData> classSet = ASM.getAll(AutoBlockRegister.class.getName());
		Map<String, Object> valueMap;
		Class<?> nowClass;
		if (classSet != null) {
			for (ASMDataTable.ASMData data : classSet) {
				valueMap = data.getAnnotationInfo();
				nowClass = Class.forName(data.getClassName());
				Block bt = (Block) nowClass.newInstance();
				bt.setRegistryName(ModernIndustry.MODID, (String) valueMap.get("registryName"));
				String unName = valueMap.getOrDefault("unlocalizedName", "").toString();
				bt.setUnlocalizedName(unName.equals("") ? bt.getRegistryName().getResourcePath() : unName);
				Blocks.blocks.add(bt);
				
				Class<?> register = (Class<?>) valueMap.getOrDefault("register", AutoBlockRegister.REGISTER);
				if (AutoBlockRegister.REGISTER.equals(register))
					Blocks.autoRegister.add(bt);
				else
					Blocks.selfRegister.put(register, bt);
			}
		}
	}
	
	/** BlockRegister类 */
	private static void regBlockRegister() throws IllegalAccessException {
		Field[] bfs = BlockRegister.class.getFields();
		for (Field f : bfs) {
			Object o = f.get(null);
			if (o instanceof Block) {
				Block b = (Block) o;
				addAutoBlock(b);
				if (f.isAnnotationPresent(OreCreat.class)) {
					OreCreat oc = f.getAnnotation(OreCreat.class);
					Blocks.worldCreater.put(b, new WorldCreater(oc, b.getDefaultState()));
				}
			}
		}
	}
	
}
