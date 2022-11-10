package top.kmar.mi.api.regedits;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.kmar.mi.api.regedits.machines.AutoLoadMachine;
import top.kmar.mi.api.regedits.machines.AutoManagerRegistryMachine;
import top.kmar.mi.api.regedits.machines.AutoTypeRegistryMachine;
import top.kmar.mi.api.regedits.machines.BlockRegistryMachine;
import top.kmar.mi.api.regedits.machines.CmptRegistryMachine;
import top.kmar.mi.api.regedits.machines.FluidRegistryMachine;
import top.kmar.mi.api.regedits.machines.ItemRegistryMachine;
import top.kmar.mi.api.regedits.machines.ManagerRegistryMachine;
import top.kmar.mi.api.regedits.machines.OreCreateRegistryMachine;
import top.kmar.mi.api.regedits.machines.PlayerHandlerRegistryMachine;
import top.kmar.mi.api.regedits.machines.TileEntityRegistryMachine;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static top.kmar.mi.ModernIndustry.MODID;

/**
 * 管理自动注册类型
 * @author EmptyDreams
 */
@SuppressWarnings("rawtypes")
@Mod.EventBusSubscriber
public final class AutoRegister {
	
	private static boolean isInit = false;
	
	/** 初始化注册机 */
	public static void init() {
		if (isInit) return;
		MinecraftForge.EVENT_BUS.post(new AutoRegisterRegistryEvent());
		isInit = true;
		registryAll();
	}
	
	public static AutoRegisterMachine<?, ?> getInstance(String key) {
		return INSTANCE.get(key);
	}
	
	private static final Map<String, AutoRegisterMachine> INSTANCE = new Object2ObjectOpenHashMap<>();
	
	private static void registryAll() {
		List<Node> list = new ArrayList<>(INSTANCE.size());
		INSTANCE.forEach((key, it) -> list.add(new Node(key, it)));
		//noinspection unchecked
		list.sort(Comparator.comparing(Node::getRegister));
		list.forEach(node -> node.register.registryAll(CommonProxy.getAsm()));
	}
	
	private static void registry(String key, AutoRegisterMachine register) {
		if (isInit) throw new AssertionError("不能在初始化后继续注册注册机");
		StringUtil.checkNull(key, "key");
		StringUtil.checkNull(register, "register");
		if (!key.contains(":"))
			MISysInfo.err("MI推荐使用＂modid:value＂的方式注册AutoRegisterMachine，而非[" + key + "]");
		INSTANCE.put(key, register);
	}
	
	@SubscribeEvent
	public static void registryAllMachine(AutoRegisterRegistryEvent event) {
		event.registryByClass(MODID, new AutoLoadMachine());
		event.registryByClass(MODID, new AutoManagerRegistryMachine());
		event.registryByClass(MODID, new BlockRegistryMachine());
		event.registryByClass(MODID, new FluidRegistryMachine());
		event.registryByClass(MODID, new ItemRegistryMachine());
		event.registryByClass(MODID, new ManagerRegistryMachine());
		event.registryByClass(MODID, new OreCreateRegistryMachine());
		event.registryByClass(MODID, new PlayerHandlerRegistryMachine());
		event.registryByClass(MODID, new TileEntityRegistryMachine());
		event.registryByClass(MODID, CmptRegistryMachine.INSTANCE);
		event.registryByClass(MODID, new AutoTypeRegistryMachine());
	}
	
	private AutoRegister() { throw new AssertionError("不应该调用的构造函数"); }
	
	/** 注册机仅允许通过事件注册 */
	public static final class AutoRegisterRegistryEvent extends Event {
		
		public void registry(String key, AutoRegisterMachine register) {
			AutoRegister.registry(key, register);
		}
		
		/** 通过简单类名注册 */
		public void registryByClass(String modid, AutoRegisterMachine register) {
			registry(modid + ":" + register.getClass().getSimpleName(), register);
		}
		
	}
	
	private static final class Node {
		
		final String key;
		final AutoRegisterMachine register;
		
		Node(String key, AutoRegisterMachine register) {
			this.key = key;
			this.register = register;
		}
		
		public AutoRegisterMachine getRegister() {
			return register;
		}
	}
	
}