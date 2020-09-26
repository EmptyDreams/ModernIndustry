package xyz.emptydreams.mi.api.utils;

import xyz.emptydreams.mi.api.utils.data.Point2D;

/**
 * @author EmptyDreams
 */
public final class MathUtil {
	
	/**
	 * 计算大于等于number的最小2的整数幂<br>
	 * 例如：3 -> 4; 10 -> 16; 8 -> 8
	 */
	public static int integerPower(int number) {
		number |= (number >>>  1);
		number |= (number >>>  2);
		number |= (number >>>  4);
		number |= (number >>>  8);
		number |= (number >>> 16);
		return ++number;
	}
	
	/**
	 * 将一维下标转换为二维矩阵坐标
	 * @param index 一维下标
	 * @param width 矩阵宽度
	 * @return 二维矩阵坐标
	 */
	public static Point2D indexToMatrix(int index, int width) {
		int x = index % width;
		int y = index / width;
		return new Point2D(x, y);
	}
	
}
