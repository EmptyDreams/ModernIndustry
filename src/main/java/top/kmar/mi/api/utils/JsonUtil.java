package top.kmar.mi.api.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.Item;
import top.kmar.mi.api.craftguide.ItemElement;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Json解析
 * @author EmptyDreams
 */
public final class JsonUtil {
	
	/** 读取一个{@link ItemElement} */
	@Nonnull
	public static ItemElement getElement(JsonObject json) {
		String itemName = json.get("item").getAsString();
		int meta = 0;
		if (json.has("data")) meta = json.get("data").getAsShort();
		int count = 1;
		if (json.has("count")) count = json.get("count").getAsInt();
		Item item = Item.getByNameOrId(itemName);
		return ItemElement.instance(item, count, meta);
	}
	
	/** 读取keyMap */
	public static Char2ObjectMap<ItemElement> getKeyMap(JsonObject keyJson) {
		Char2ObjectMap<ItemElement> keyMap = new Char2ObjectOpenHashMap<>();
		keyMap.put(' ', ItemElement.empty());
		for (Map.Entry<String, JsonElement> entry : keyJson.entrySet()) {
			keyMap.put(entry.getKey().charAt(0), getElement(entry.getValue().getAsJsonObject()));
		}
		return keyMap;
	}
	
}