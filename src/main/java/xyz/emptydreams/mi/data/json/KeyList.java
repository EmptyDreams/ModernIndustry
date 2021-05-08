package xyz.emptydreams.mi.data.json;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 存储Key值表
 * @author EmptyDreams
 */
public final class KeyList {
	
	private KeyList() { throw new AssertionError("不应该调用的构造函数"); };
	
	private static final Map<String, Function<Object, String>> KEYS =
			new Object2ObjectArrayMap<String, Function<Object, String>>() {
		{
			put("template::name", KeyList::getName);
			put("template::src", element -> "mi:blocks/machine/" + getName(element) + "/src");
			put("template::working", element -> "mi:blocks/machine/" + getName(element) + "/working");
			put("template::empty", element -> "mi:blocks/machine/" + getName(element) + "/empty");
		}
	};
	
	public static String get(Object element, String key) {
		return KEYS.get(key).apply(element);
	}
	
	public static Set<Map.Entry<String, Function<Object, String>>> entrySet() {
		return KEYS.entrySet();
	}
	
	private static String getName(Object element) {
		if (element instanceof IForgeRegistryEntry.Impl) {
			return ((IForgeRegistryEntry.Impl<?>) element).getRegistryName().getResourcePath();
		}
		if (element instanceof Fluid) {
			return ((Fluid) element).getName();
		}
		throw new NullPointerException("只支持继承自IForgeRegistryEntry.Impl及Fluid的类！");
	}
	
}