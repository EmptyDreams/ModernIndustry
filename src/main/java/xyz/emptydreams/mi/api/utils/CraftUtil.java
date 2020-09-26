package xyz.emptydreams.mi.api.utils;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.utils.data.Size2D;

import java.util.function.Function;

/**
 * 关于合成表一些操作的封装
 * @author EmptyDreams
 */
@SuppressWarnings("rawtypes")
public final class CraftUtil {
	
	/**
	 * 获取指定合成表的最大大小
	 * @param craft
	 * @param function
	 * @return
	 */
	public static Size2D getCraftListMaxSize(CraftGuide craft,
	                                         Function<Object, IShape<? extends ItemList, ?>> function) {
		int height = Integer.MIN_VALUE;
		int width = Integer.MIN_VALUE;
		for (Object o : craft) {
			ItemList list = function.apply(o).getRawSol();
			height = Math.max(list.getHeight(), height);
			width = Math.max(list.getWidth(), width);
		}
		return new Size2D(width, height);
	}
	
}
