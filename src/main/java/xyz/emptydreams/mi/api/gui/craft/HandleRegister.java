package xyz.emptydreams.mi.api.gui.craft;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.craft.handle.CraftHandle;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.Map;
import java.util.function.Function;

/**
 * 注册{@link CraftHandle}
 * @author EmptyDreams
 */
@SuppressWarnings("rawtypes")
public final class HandleRegister {
	
	private static final Map<HandleInfo, Function<CraftGuide, CraftHandle>>
					registries = new Object2ObjectArrayMap<>();
	
	/**
	 * 注册一个{@link CraftHandle}，若已经有同样的Handle注册，那么该注册将覆盖上一次注册
	 */
	public static void registry(HandleInfo info, Function<CraftGuide, CraftHandle> creater) {
		StringUtil.checkNull(info,"clazz");
		StringUtil.checkNull(creater, "creater");
		registries.put(info, creater);
	}
	
	/**
	 * 根据合成表获取相应的{@link CraftHandle}
	 * @param craft 合成表
	 * @return 若没有找到合适的则返回null
	 */
	public static CraftHandle get(CraftGuide craft) {
		for (Map.Entry<HandleInfo, Function<CraftGuide, CraftHandle>> entry : registries.entrySet()) {
			if (entry.getKey().match(craft)) return entry.getValue().apply(craft);
		}
		return null;
	}
	
	/** 存储注册信息 */
	public static final class HandleInfo {
		
		private final Class itemSol;
		private final Class product;
		private final Class shape;
		
		/**
		 * @param itemSol 原料列表
		 * @param product 产物
		 * @param shape 合成表
		 */
		public HandleInfo(Class itemSol, Class product, Class shape) {
			this.itemSol = itemSol;
			this.product = product;
			this.shape = shape;
		}
		
		public Class getItemSol() { return itemSol; }
		public Class getProduct() { return product; }
		public Class getShape() { return shape; }
		
		public boolean match(CraftGuide craft) {
			return craft.getShapeClass() == shape && craft.getProtectClass() == product;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			
			HandleInfo that = (HandleInfo) o;
			
			if (!itemSol.equals(that.itemSol)) return false;
			if (!product.equals(that.product)) return false;
			return shape.equals(that.shape);
		}
		
		@Override
		public int hashCode() {
			return shape.hashCode();
		}
		
	}
	
}
