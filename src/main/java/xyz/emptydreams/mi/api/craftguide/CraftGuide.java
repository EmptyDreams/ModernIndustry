package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.data.Size2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 合成表管理器
 * @param <T> 合成表的类型
 * @param <R> 产物类型
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CraftGuide<T extends IShape, R> implements Iterable<T> {
	
	/** 存储所有实例 */
	private static final Map<ResourceLocation, CraftGuide> instances = new HashMap<>();
	
	/**
	 * 获取实例
	 * @param impl 注册名，MODID使用{@link ModernIndustry#MODID}
	 * @param shapeSize 原料列表尺寸
	 * @param proSize 产物列表尺寸
	 * @param shape 原料列表的Class
	 * @param product 产物列表的Class
	 * @param <T> 合成表的类型
	 * @param <R> 产物列表类型
	 * @return 如果实例不存在则返回新的实例，存在则返回已有的实例
	 */
	public static <T extends IShape, R> CraftGuide<T, R>
			instance(Block impl, Size2D shapeSize, Size2D proSize,
			            Class<T> shape, Class<R> product) {
		return instance(new ResourceLocation(
				impl.getRegistryName().getResourceDomain(), impl.getUnlocalizedName()),
					shapeSize, proSize, shape, product);
	}
	
	/**
	 * 获取实例
	 * @param name 注册名
	 * @param shapeSize 原料列表尺寸
	 * @param proSize 产物列表尺寸
	 * @param shape 原料列表的Class
	 * @param product 产物列表的Class
	 * @param <T> 合成表的类型
	 * @param <R> 产物列表类型
	 * @return 如果实例不存在则返回新的实例，存在则返回已有的实例
	 */
	public static <T extends IShape, R> CraftGuide<T, R>
			instance(ResourceLocation name, Size2D shapeSize, Size2D proSize, Class<T> shape, Class<R> product) {
		return instances.computeIfAbsent(name,
				it -> new CraftGuide<>(it, shapeSize, proSize, shape, product));
	}
	
	/**
	 * 获取已有实例
	 * @param name 注册名
	 * @param <T> 合成表的类型
	 * @param <R> 产物类型
	 * @return 若存在已有实例则返回已有实例，否则返回null
	 */
	@Nullable
	public static <T extends IShape, R> CraftGuide<T, R> getInstance(ResourceLocation name) {
		return instances.getOrDefault(name, null);
	}
	
	/**
	 * 移除一个实例，<b>不可以用来清空合成表，因为该方法不会影响已经创建的实例</b>
	 * @param name 注册名
	 */
	@SuppressWarnings("unused")
	public static void deleteInstance(ResourceLocation name) {
		instances.remove(name);
	}
	
	/* 存储类型 */
	private final Class<T> ipClass;
	private final Class<R> opClass;
	/** 存储合成表 */
	private final List<T> shapes = new LinkedList<T>() {
		@Override
		public boolean add(T t) {
			return super.add(StringUtil.checkNull(t, "shape"));
		}
	};
	private final ResourceLocation name;
	/** 存储最大尺寸 */
	private final Size2D maxSize;
	/** 存储产品列表最大尺寸 */
	private final Size2D proSize;
	
	private CraftGuide(ResourceLocation name, Size2D size, Size2D proSize, Class<T> shape, Class<R> product) {
		this.name = name;
		this.maxSize = size;
		this.proSize = proSize;
		this.ipClass = shape;
		this.opClass = product;
		
	}
	
	/**
	 * 注册一个合成表
	 * @param shapes 合成表
	 */
	public void registry(T... shapes) {
		Collections.addAll(this.shapes, StringUtil.checkNull(shapes, "shapes"));
	}
	
	/**
	 * 根据原料删除一个合成表
	 * @param sol 物品列表
	 */
	@SuppressWarnings("unused")
	public void unregister(ItemSol sol) {
		shapes.removeIf(shape -> shape.apply(sol));
	}
	
	/**
	 * 判断原料中是否包含指定物品
	 * @param stack 物品
	 */
	public boolean haveInput(ItemStack stack) {
		for (T shape : shapes) {
			if (shape.haveItem(stack)) return true;
		}
		return false;
	}
	
	/**
	 * 判断原料中是否含有指定元素
	 * @param element 元素
	 */
	@SuppressWarnings("unused")
	public boolean haveInput(ItemElement element) {
		for (T shape : shapes) {
			if (shape.haveElement(element)) return true;
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
			if (shape.apply(sol)) return (R) shape.getOutput();
		}
		return null;
	}
	
	/** 获取合成表大小 */
	public Size2D getShapeSize() {
		return maxSize;
	}
	/** 获取合成表高度 */
	public int getShapeHeight() {
		return maxSize.getHeight();
	}
	/** 获取合成表宽度 */
	public int getShapeWidth() {
		return maxSize.getWidth();
	}
	
	/** 获取产品列表大小 */
	public Size2D getProtectSize() {
		return proSize;
	}
	/** 获取产品列表高度 */
	public int getProtectedHeight() {
		return proSize.getHeight();
	}
	/** 获取产品列表宽度 */
	public int getProtectedWidth() {
		return proSize.getWidth();
	}
	
	/** 获取合成表的Class */
	public Class<T> getShapeClass() {
		return ipClass;
	}
	
	/**
	 * 获取合成表的原料列表的Class
	 * @throws IllegalArgumentException 如果管理器中还未添加任何合成表
	 */
	public Class<? extends ItemSol> getInputClass() {
		if (shapes.isEmpty()) throw new IllegalArgumentException("管理器内还未添加任何合成表，无法获取合成表原料类型");
		return shapes.get(0).getInputClass();
	}
	/** 获取产物的Class */
	public Class<R> getOutputClass() {
		return opClass;
	}
	
	public String getLocalName() {
		return name.getResourcePath() + ".name";
	}
	
	/** 获取合成表的名称 */
	@Nonnull
	public String getName() {
		return name.toString();
	}
	
	/** @see Collection#stream()  */
	@SuppressWarnings("unused")
	@Nonnull
	public Stream<T> stream() {
		return shapes.stream();
	}
	
	@Override
	@Nonnull
	public Iterator<T> iterator() {
		return shapes.iterator();
	}
	
	public T getShape(int index) {
		return shapes.get(index);
	}
	
	public int size() {
		return shapes.size();
	}
	
}
