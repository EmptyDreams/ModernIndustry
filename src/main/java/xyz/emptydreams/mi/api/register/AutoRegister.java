package xyz.emptydreams.mi.api.register;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.register.machines.AgentRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.AutoLoadMachine;
import xyz.emptydreams.mi.api.register.machines.AutoManagerRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.BlockRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.FluidRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.ItemRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.ManagerRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.OreCreateRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.PlayerHandleRegistryMachine;
import xyz.emptydreams.mi.api.register.machines.TileEntityRegistryMachine;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static xyz.emptydreams.mi.ModernIndustry.MODID;

/**
 * 管理自动注册类型
 * @author EmptyDreams
 */
@SuppressWarnings("rawtypes")
@Mod.EventBusSubscriber
public final class AutoRegister {
	
	static {
		MinecraftForge.EVENT_BUS.post(new AutoRegisterRegistryEvent());
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
		StringUtil.checkNull(key, "key");
		StringUtil.checkNull(register, "register");
		if (!key.contains(":"))
			MISysInfo.err("MI推荐使用＂modid:value＂的方式注册AutoRegisterMachine，而非[" + key + "]");
		INSTANCE.put(key, register);
	}
	
	@SubscribeEvent
	public static void registryAllMachine(AutoRegisterRegistryEvent event) {
		event.registryByClass(MODID, new AgentRegistryMachine());
		event.registryByClass(MODID, new AutoLoadMachine());
		event.registryByClass(MODID, new AutoManagerRegistryMachine());
		event.registryByClass(MODID, new BlockRegistryMachine());
		event.registryByClass(MODID, new FluidRegistryMachine());
		event.registryByClass(MODID, new ItemRegistryMachine());
		event.registryByClass(MODID, new ManagerRegistryMachine());
		event.registryByClass(MODID, new OreCreateRegistryMachine());
		event.registryByClass(MODID, new PlayerHandleRegistryMachine());
		event.registryByClass(MODID, new TileEntityRegistryMachine());
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