package xyz.emptydreams.mi.api.utils.data.math;

import java.util.Iterator;

/**
 * 二维矩阵
 * @author EmptyDreams
 */
public class Mar2D implements Iterable<Mar2D.Node> {
	
	private final int[][] datas;
	private final int height, width;
	
	/**
	 * 构建一个指定大小的矩阵
	 * @param width 宽度
	 * @param height 高度
	 */
	public Mar2D(int width, int height) {
		datas = new int[height][width];
		this.height = height;
		this.width = width;
	}
	
	/**
	 * <p>两个矩阵相加.
	 * <p>如果两个矩阵大小不一样，则长和宽各区两者中最小的值进行运算
	 * @param mar2d 指定矩阵
	 */
	public void plus(Mar2D mar2d) {
		int width = Math.min(getWidth(), mar2d.getWidth());
		int height = Math.min(getHeight(), mar2d.getHeight());
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				datas[y][x] += mar2d.get(x, y);
			}
		}
	}
	
	/**
	 * <p>两个矩阵相减.
	 * <p>如果两个矩阵大小不一样，则长和宽各区两者中最小的值进行运算
	 * @param mar2d 指定矩阵
	 */
	public void minus(Mar2D mar2d) {
		int width = Math.min(getWidth(), mar2d.getWidth());
		int height = Math.min(getHeight(), mar2d.getHeight());
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				datas[y][x] -= mar2d.get(x, y);
			}
		}
	}
	
	/** 矩阵乘以一个数 */
	public void mul(int number) {
		forEach(it -> it.mul(number));
	}
	
	/** 矩阵除以一个数 */
	public void div(int number) {
		forEach(it -> it.div(number));
	}
	
	/**
	 * 两个矩阵相乘
	 * @param mar2d 指定矩阵
	 * @return 运算后的新矩阵
	 * @throws IllegalArgumentException 如果两个矩阵不满足乘法运算的要求
	 */
	public Mar2D mul(Mar2D mar2d) {
		if (getWidth() != mar2d.getHeight())
			throw new IllegalArgumentException(
					"矩阵A的列[" + getWidth() + "]应当等于矩阵B的行[" + mar2d.getHeight() + ']');
		Mar2D result = new Mar2D(getWidth(), mar2d.getHeight());
		for (Node node : result) {
			int value = 0;
			for (int k = 0; k < getHeight(); ++k) {
				value += get(node.getX(), k) * mar2d.get(k, node.getY());
			}
			node.setValue(value);
		}
		return result;
	}
	
	/** 矩阵的点乘 */
	public void mulPoint(Mar2D mar2d) {
		int width = Math.min(getWidth(), mar2d.getWidth());
		int height = Math.min(getHeight(), mar2d.getHeight());
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				datas[y][x] *= mar2d.get(x, y);
			}
		}
	}
	
	/** 矩阵的点除 */
	public void divPoint(Mar2D mar2d) {
		int width = Math.min(getWidth(), mar2d.getWidth());
		int height = Math.min(getHeight(), mar2d.getHeight());
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				datas[y][x] /= mar2d.get(x, y);
			}
		}
	}
	
	/**
	 * 获取矩阵中的指定数值
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @throws IndexOutOfBoundsException 如果下标超出范围
	 */
	public int get(int x, int y) {
		return datas[y][x];
	}
	
	/**
	 * 设置矩阵中指定位置的值
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @throws IndexOutOfBoundsException 如果下标超出范围
	 */
	public void set(int x, int y, int value) {
		datas[y][x] = value;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	@Override
	public MarIterator iterator() {
		return new MarIterator();
	}
	
	public final class Node {
		
		private final int x, y;
		
		Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getValue() {
			return Mar2D.this.get(x, y);
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public void setValue(int value) {
			Mar2D.this.set(x, y, value);
		}
		
		public void add(int k) {
			setValue(getValue() + k);
		}
		
		public void minus(int k) {
			setValue(getValue() - k);
		}
		
		public void mul(int k) {
			setValue(getValue() * k);
		}
		
		public void div(int k) {
			setValue(getValue() / k);
		}
		
	}
	
	public final class MarIterator implements Iterator<Node> {
		
		int x = -1, y = 0;
		
		
		@Override
		public boolean hasNext() {
			return x != getWidth() - 1 || y != getHeight() - 1;
		}
		
		@Override
		public Node next() {
			++x;
			if (x == getWidth()) {
				x = 0;
				++y;
			}
			return new Node(x, y);
		}
		
	}
	
}