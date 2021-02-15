package xyz.emptydreams.mi.api.utils;

import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.utils.data.Point2D;

/**
 * 有关数学的计算
 * @author EmptyDreams
 */
public final class MathUtil {
	
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
	 * 判断鼠标是否在指定的组件内，鼠标坐标必须与组件坐标使用同一坐标系
	 * @param mouseX 鼠标X轴坐标
	 * @param mouseY 鼠标Y轴坐标
	 * @param component 组件
	 */
	public static boolean checkMouse2DRec(float mouseX, float mouseY, IComponent component) {
		return checkMouse2DRec(mouseX, mouseY, component.getX(), component.getY(),
				component.getWidth(), component.getHeight());
	}
	
	/**
	 * 判断鼠标是否在指定矩形内
	 * @param mouseX 鼠标X轴坐标
	 * @param mouseY 鼠标Y轴坐标
	 * @param recX 矩形X轴起始坐标
	 * @param recY 矩形Y轴起始坐标
	 * @param width 矩形宽度
	 * @param height 矩形高度
	 */
	public static boolean checkMouse2DRec(float mouseX, float mouseY,
	                                      int recX, int recY, int width, int height) {
		return mouseX >= recX && (mouseX <= recX + width) &&
				mouseY >= recY && (mouseY <= recY + height);
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