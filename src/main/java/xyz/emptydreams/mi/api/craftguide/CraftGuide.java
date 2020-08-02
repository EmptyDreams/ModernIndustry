package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 合成表管理器
 * @param <T> 产物列表
 * @param <R> 产物
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CraftGuide<T extends IShape, R> {
	
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
		return instances.computeIfAbsent(name, it -> new CraftGuide<>());
	}
	
	/**
	 * 移除一个实例，可以用来清空合成表
	 * @param name 注册名
	 */
	public static void deleteInstance(ResourceLocation name) {
		instances.remove(name);
	}
	
	/** 存储合成表 */
	private final List<T> shapes = new LinkedList<T>() {
		@Override
		public boolean add(T t) {
			WaitList.checkNull(t, "shape");
			return super.add(t);
		}
	};
	
	private CraftGuide() { }
	
	/**
	 * 注册一个合成表
	 * @param shapes 合成表
	 */
	public void registry(T... shapes) {
		WaitList.checkNull(shapes, "shapes");
		Collections.addAll(this.shapes, shapes);
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
	
}
