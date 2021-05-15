package xyz.emptydreams.mi.api.register;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Data;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 管理自动注册类型
 * @author EmptyDreams
 */
@SuppressWarnings("rawtypes")
public final class AutoManager {
	
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
		INSTANCE.put(key, register);
	}
	
	private AutoManager() { throw new AssertionError("不应该调用的构造函数"); }
	
	/** 注册机仅允许通过事件注册 */
	public static final class AutoRegisterRegistryEvent extends Event {
		
		public void registry(String key, AutoRegisterMachine register) {
			AutoManager.registry(key, register);
		}
		
	}
	
	@Data
	private static final class Node {
		final String key;
		final AutoRegisterMachine register;
	}
	
}