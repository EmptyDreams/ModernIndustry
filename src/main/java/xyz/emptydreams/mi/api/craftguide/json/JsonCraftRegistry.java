package xyz.emptydreams.mi.api.craftguide.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.multi.OrderlyShape;
import xyz.emptydreams.mi.api.craftguide.multi.UnorderlyShape;
import xyz.emptydreams.mi.api.craftguide.only.OrderlyShapeOnly;
import xyz.emptydreams.mi.api.craftguide.only.UnorderlyShapeOnly;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.JsonUtil;

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
		WaitList.checkNull(name, "name");
		WaitList.checkNull(accept, "accept");
		INSTANCE.put(name, accept);
	}
	
	/**
	 * 注册一个JSON
	 * @return 是否注册成功
	 */
	public static boolean registryJson(JsonObject json) {
		WaitList.checkNull(json, "json");
		String type = json.get("type").getAsString();
		if (type == null) return false;
		BiConsumer<JsonObject, Char2ObjectMap<ItemElement>> predicate =
										INSTANCE.getOrDefault(type, null);
		if (predicate == null) return false;
		predicate.accept(json, JsonUtil.getKeyMap(json.getAsJsonObject("key")));
		return true;
	}
	
	static {
		registryInstance("mi:orderly_shape", OrderlyShape::pares);
		registryInstance("mi:unorderly_shape", UnorderlyShape::pares);
		registryInstance("mi:orderly_shape_only", OrderlyShapeOnly::pares);
		registryInstance("mi:unorderly_shape_only", UnorderlyShapeOnly::pares);
	}
	
	private JsonCraftRegistry() { throw new UnsupportedOperationException(); }
	
}
