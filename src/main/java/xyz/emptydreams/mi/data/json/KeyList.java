package xyz.emptydreams.mi.data.json;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.Block;
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
	
	private static final Map<String, Function<Block, String>> KEYS =
			new Object2ObjectArrayMap<String, Function<Block, String>>() {
		{
			put("template::name", KeyList::getName);
			put("template::src", block -> "mi:blocks/machine/" + getName(block) + "/src");
			put("template::working", block -> "mi:blocks/machine/" + getName(block) + "/working");
			put("template::empty", block -> "mi:blocks/machine/" + getName(block) + "/empty");
		}
	};
	
	public static String get(Block block, String key) {
		return KEYS.get(key).apply(block);
	}
	
	public static Set<Map.Entry<String, Function<Block, String>>> entrySet() {
		return KEYS.entrySet();
	}
	
	private static String getName(IForgeRegistryEntry.Impl<?> impl) {
		return impl.getRegistryName().getResourcePath();
	}
	
}