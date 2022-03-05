package top.kmar.mi.api.utils;

import top.kmar.mi.api.utils.data.math.Point2D;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 有关数学的计算
 * @author EmptyDreams
 */
public final class MathUtil {
	
	private static final Random RANDOM = new Random();
	
	/** 获取随机数 */
	@Nonnull
	public static Random random() {
		return RANDOM;
	}
	
	/**
	 * 计算出一个点数大于等于amount的正方形矩阵的边长
	 * @param amount 点数
	 * @return 正方形矩阵的边长
	 */
	public static int amount2Rec(int amount) {
		for (int i = 1; i < amount; ++i) {
			int result = i * i;
			if (result >= amount) return i;
		}
		return -1;
	}
	
	/**
	 * 将一维下标转换为二维矩阵坐标
	 * @param index 一维下标
	 * @param width 矩阵宽度
	 * @return 二维矩阵坐标
	 */
	@Nonnull
	public static Point2D indexToMatrix(int index, int width) {
		int x = index % width;
		int y = index / width;
		return new Point2D(x, y);
	}
	
}