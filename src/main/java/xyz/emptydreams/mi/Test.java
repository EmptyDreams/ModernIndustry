package xyz.emptydreams.mi;

import java.awt.*;

public class Test {

	public static void main(String[] args) {
		Point point = new Point();
		check(args, point);
		print(point.x, point.y);
	}

	private static void print(int width, int height) {
		//计算间距
		int spacing = (height - 3) / 2;
		int spacing2 = height - 3 - spacing;

		//第一行
		printLine(width);
		printColumn(spacing);
		//第二行
		printLine(width);
		printColumn(spacing2);
		//第三行
		printLine(width);
	}

	private static void check(String[] args, Point point) {
		//输入并解析数据
		if (args.length != 2) {
			System.err.println("输入的参数格式应为：[int(width)] [int(height)]");
			System.exit(-1);
		}
		point.x = Integer.parseInt(args[0]);
		point.y = Integer.parseInt(args[1]);
		//数据正确性检查
		if (point.x < 3 || point.y < 5) {
			System.err.println("宽度必须大于等于3，高度必须大于等于5");
			System.exit(-1);
		}
	}

	/** 打印间距 */
	private static void printColumn(int spacing) {
		for (int i = 0; i < spacing; ++i) println();
	}

	/** 打印一个新行 */
	private static void printLine(int width) {
		for (int i = 0; i < width; ++i) print();
		newLine();
	}

	/** 打印一个■ */
	private static void print() {
		System.out.print('■');
	}

	/** 换行 */
	private static void newLine() {
		System.out.println();
	}

	/** 打印一个■并换行 */
	private static void println() {
		System.out.println('■');
	}

}