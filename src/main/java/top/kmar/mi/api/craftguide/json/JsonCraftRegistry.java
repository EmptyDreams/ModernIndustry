package top.kmar.mi.api.craftguide.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.multi.OrderedShape;
import top.kmar.mi.api.craftguide.multi.UnorderedShape;
import top.kmar.mi.api.craftguide.only.OrderedShapeOnly;
import top.kmar.mi.api.craftguide.only.UnorderedShapeOnly;
import top.kmar.mi.api.utils.JsonUtil;
import top.kmar.mi.api.utils.StringUtil;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 供JSON注册合成表
 * @author EmptyDreams
 */
public final class JsonCraftRegistry {
	
	/** 存储所有注册的实例 */
	private static final Map<String,
			BiConsumer<JsonObject, Char2ObjectMap<ItemElement>>> INSTANCE = Maps.newHashMap();
	
	/**
	 * 注册一个实例
	 * @param name 注册名
	 * @param accept 用于处理并注册JSON
	 */
	public static void registryInstance(String name,
	                                    BiConsumer<JsonObject, Char2ObjectMap<ItemElement>> accept) {
		INSTANCE.put(StringUtil.checkNull(name, "name"), StringUtil.checkNull(accept, "accept"));
	}
	
	/**
	 * 注册一个JSON
	 * @return 是否注册成功
	 */
	public static boolean registryJson(JsonObject json) {
		String type = StringUtil.checkNull(json, "json").get("type").getAsString();
		if (type == null) return false;
		BiConsumer<JsonObject, Char2ObjectMap<ItemElement>> predicate =
										INSTANCE.getOrDefault(type, null);
		if (predicate == null) return false;
		predicate.accept(json, JsonUtil.getKeyMap(json.getAsJsonObject("key")));
		return true;
	}
	
	static {
		registryInstance("mi:orderly_shape", OrderedShape::pares);
		registryInstance("mi:unorderly_shape", UnorderedShape::pares);
		registryInstance("mi:orderly_shape_only", OrderedShapeOnly::pares);
		registryInstance("mi:unorderly_shape_only", UnorderedShapeOnly::pares);
	}
	
	private JsonCraftRegistry() { throw new UnsupportedOperationException(); }
	
}