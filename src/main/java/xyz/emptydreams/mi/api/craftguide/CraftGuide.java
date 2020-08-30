package xyz.emptydreams.mi.api.craftguide;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.utils.JsonUtil;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * 合成表管理器
 * @param <T> 产物列表
 * @param <R> 产物
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CraftGuide<T extends IShape, R> implements Iterable<T> {
	
	/** 存储所有实例 */
	private static final Map<ResourceLocation, CraftGuide> instances = new HashMap<>();
	
	/**
	 * 获取实例
	 * @param name 注册名，MODID使用{@link ModernIndustry#MODID}
	 * @param <T> 合成表的类型
	 * @return 如果实例不存在则返回新的实例，存在则返回已有的实例
	 */
	public static <T extends IShape, R> CraftGuide<T, R> instance(String name) {
		return instance(new ResourceLocation(ModernIndustry.MODID, name));
	}
	
	/**
	 * 获取实例
	 * @param name 注册名
	 * @param <T> 合成表的类型
	 * @return 如果实例不存在则返回新的实例，存在则返回已有的实例
	 */
	public static <T extends IShape, R> CraftGuide<T, R> instance(ResourceLocation name) {
		return instances.computeIfAbsent(name, it -> new CraftGuide<>(it.toString()));
	}
	
	/**
	 * 移除一个实例，<b>不可以用来清空合成表，因为该方法不会影响已经创建的实例</b>
	 * @param name 注册名
	 */
	public static void deleteInstance(ResourceLocation name) {
		instances.remove(name);
	}
	
	/** 存储合成表 */
	private final List<T> shapes = new LinkedList<T>() {
		@Override
		public boolean add(T t) {
			return super.add(StringUtil.checkNull(t, "shape"));
		}
	};
	private final String name;
	
	private CraftGuide(String name) {
		this.name = name;
	}
	
	/**
	 * 注册一个合成表
	 * @param shapes 合成表
	 */
	public void registry(T... shapes) {
		Collections.addAll(this.shapes, StringUtil.checkNull(shapes, "shapes"));
	}
	
	private static final JsonParser PARSER = new JsonParser();
	/**
	 * 注册指定的JSON合成表，目前只支持MI
	 * @param path json在mi_recipes中的路径
	 * @param builder 通过传入原料列表和产品构建一个合成表
	 */
	public void registry(String path, BiFunction<ItemSol, R, T> builder) {
		InputStream stream =
				ModernIndustry.class.getResourceAsStream("../../../assets/mi/mi_recipes/" + path);
		JsonObject jsonObject = PARSER.parse(
				new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
		JsonObject resultInfo = jsonObject.getAsJsonObject("result");
		Char2ObjectMap<ItemElement> keyMap = JsonUtil.getKeyMap(jsonObject.getAsJsonObject("key"));
		R result;
		if (resultInfo.get("type").getAsString().equals("ItemElement")) {
			result = (R) JsonUtil.getElement(resultInfo);
		} else {
			result = (R) JsonUtil.getItemSol(resultInfo, keyMap);
		}
		ItemSol sol = JsonUtil.getItemSol(jsonObject, keyMap);
		shapes.add(builder.apply(sol, result));
	}
	/**
	 * 注册多个JSON合成表
	 * @see #registry(String, BiFunction)
	 */
	public void registry(BiFunction<ItemSol, R, T> builder, String... paths) {
		for (String path : paths) {
			registry(path, builder);
		}
	}
	
	/**
	 * 根据原料删除一个合成表
	 * @param sol 物品列表
	 */
	public void unregistry(ItemSol sol) {
		shapes.removeIf(shape -> shape.apply(sol));
	}
	
	/**
	 * 判断原料中是否包含指定物品
	 * @param stack 物品
	 */
	public boolean rawHas(ItemStack stack) {
		for (T shape : shapes) {
			if (shape.hasItem(stack)) return true;
		}
		return false;
	}
	
	/**
	 * 判断原料中是否含有指定元素
	 * @param element 元素
	 */
	public boolean rawHas(ItemElement element) {
		for (T shape : shapes) {
			if (shape.hasElement(element)) return true;
		}
		return false;
	}
	
	/**
	 * 根据原料获取产品
	 * @param sol 物品列表
	 * @return 如果没有找到则返回null
	 */
	@Nullable
	public R apply(ItemSol sol) {
		for (T shape : shapes) {
			if (shape.apply(sol)) return (R) shape.getProduction();
		}
		return null;
	}
	
	/** 获取合成表的名称 */
	@Nonnull
	public String getName() {
		return name;
	}
	
	/** @see Collection#stream()  */
	@Nonnull
	public Stream<T> stream() {
		return shapes.stream();
	}
	
	@Override
	@Nonnull
	public Iterator<T> iterator() {
		return shapes.iterator();
	}
	
}
