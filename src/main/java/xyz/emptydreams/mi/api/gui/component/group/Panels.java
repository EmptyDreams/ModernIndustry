package xyz.emptydreams.mi.api.gui.component.group;

import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.interfaces.IntInt2IntFunction;

/**
 * 存储一系列居中的排列方案
 * @author EmptyDreams
 */
public final class Panels {

	private Panels() { throw new AssertionError("不应该被调用的构造函数"); }

	/** 什么都不做 */
	public static void non(Group group) { }
	
	/**
	 * 水平靠下对齐
	 * <p>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 */
	public static void horizontalDown(Group group) {
		horizontalHelper(group, Integer::sum, (groupHeight, componentHeight) -> groupHeight - componentHeight);
	}
	
	/**
	 * 水平靠上对齐
	 * <p>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 */
	public static void horizontalUp(Group group) {
		horizontalHelper(group, Integer::sum, ((arg0, arg1) -> 0));
	}
	
	/**
	 * 水平居中对齐
	 * <p>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 */
	public static void horizontalCenter(Group group) {
		horizontalHelper(group,
				Integer::sum, (groupHeight, componentHeight) -> (groupHeight - componentHeight) / 2);
	}
	
	/**
	 * @param group 正在运算的Group
	 * @param xGetter 控件X轴坐标获取器，第一个参数为past(上一个控件[右边缘+距离]坐标)，第二个参数为interval(控件距离)
	 * @param yGetter 控件Y轴坐标获取器，第一个参数为Group高度，第二个参数为控件高度
	 */
	private static void horizontalHelper(Group group, IntInt2IntFunction xGetter, IntInt2IntFunction yGetter) {
		int size = group.componentSize();
		int width = group.getWidth();
		if (size == 0) return;
		
		int allWidth = 0;
		int allHeight = 0;
		for (IComponent component : group) {
			allWidth += component.getWidth();
			allHeight = Math.max(allHeight, component.getHeight());
		}
		
		int realWidth;      //真实宽度
		int interval;       //两个控件间的间隔
		if (width <= allWidth) {
			interval = group.getMinDistance();
			realWidth = (size + 1) * interval + allWidth;
		} else {
			int k = (width - allWidth) / (size + 1);
			k = Math.max(k, group.getMinDistance());
			interval = Math.min(k, group.getMaxDistance());
			realWidth = width;
		}
		group.setSize(realWidth, Math.max(group.getHeight(), allHeight));
		
		int past = (realWidth - (allWidth + interval * (size + 1))) / 2;
		for (IComponent component : group) {
			component.setLocation(
					xGetter.apply(past, interval), yGetter.apply(group.getHeight(), component.getHeight()));
			past += interval + component.getWidth();
		}
	}
	
	/**
	 * <p>竖直靠右对齐
	 * <p>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 */
	public static void verticalRight(Group group) {
		verticalHelper(group, ((groupWidth, componentWidth) -> groupWidth - componentWidth), Integer::sum);
	}
	
	/**
	 * <p>竖直靠左对齐
	 * <p>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 */
	public static void verticalLeft(Group group) {
		verticalHelper(group, ((arg0, arg1) -> 0), Integer::sum);
	}
	
	/**
	 * <p>竖直居中对齐
	 * <p>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 */
	public static void verticalCenter(Group group) {
		verticalHelper(group, (groupWidth, componentWidth) -> (groupWidth - componentWidth) / 2, Integer::sum);
	}
	
	/**
	 * @param group 正在运算的Group
	 * @param xGetter 控件X轴坐标获取器，第一个参数为Group宽度，第二个参数为控件宽度
	 * @param yGetter 控件Y轴坐标获取器，第一个参数为past(上一个控件[下边缘+距离]坐标)，第二个参数为interval(控件距离)
	 */
	private static void verticalHelper(Group group, IntInt2IntFunction xGetter, IntInt2IntFunction yGetter) {
		int size = group.componentSize();
		int height = group.getHeight();
		if (size == 0) return;
		
		int allWidth = 0;
		int allHeight = 0;
		for (IComponent component : group) {
			allWidth = Math.max(allWidth, component.getWidth());
			allHeight += component.getHeight();
		}
		
		int realHeight;      //真实宽度
		int interval;        //两个控件间的间隔
		if (height <= allHeight) {
			interval = group.getMinDistance();
			realHeight = (size + 1) * interval + allHeight;
		} else {
			int k = (height - allHeight) / (size + 1);
			k = Math.max(k, group.getMinDistance());
			interval = Math.min(k, group.getMaxDistance());
			realHeight = height;
		}
		group.setSize(Math.max(group.getWidth(), allWidth), realHeight);
		
		int past = (realHeight - (allHeight + interval * (size + 1))) / 2;
		for (IComponent component : group) {
			component.setLocation(
					xGetter.apply(group.getWidth(), component.getWidth()), yGetter.apply(past, interval));
			past += interval + component.getHeight();
		}
	}
	
}