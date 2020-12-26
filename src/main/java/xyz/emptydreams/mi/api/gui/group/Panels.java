package xyz.emptydreams.mi.api.gui.group;

import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;

/**
 * 存储一系列居中的排列方案
 * @author EmptyDreams
 */
public final class Panels {

	private Panels() { throw new AssertionError("不应该被调用的构造函数"); }

	/**
	 * 水平居中对齐
	 * <h3>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 * <h3>使用的注意事项：
	 *      <p><b>如果Group是嵌套在另一个Group中的，
	 *      那么内层的Group必须手动设置大小，
	 *      外层Group的高度最好手动设置</b>
	 * @param group 组
	 */
	public static void horizontalCenter(Group group) {
		int size = group.size();
		int width = group.getWidth();
		if (size <= 0) return;

		int allWidth = 0;
		int allHeight = 0;
		for (IComponent component : group) {
			allWidth += component.getWidth();
			allHeight = Math.max(allHeight, component.getHeight());
		}
		width = Math.max(width, allWidth);
		group.setSize(width, Math.max(group.getHeight(), allHeight));

		int remain = width - allWidth;
		if (remain < 0) remain = 0;
		int interval = remain / (size + 1);
		interval = Math.max(interval, group.getMinDistance());
		interval = Math.min(interval, group.getMaxDistance());

		int past = (width - (allWidth + interval * (size + 1))) / 2;
		for (IComponent component : group) {
			component.setLocation(past + interval + group.getX(),
					(group.getHeight() - component.getHeight()) / 2 + group.getY());
			past += interval + component.getWidth();
		}
	}

	/**
	 * <p>竖直居中对齐
	 * <h3>该方法会修改的参数列表：
	 *      <ol><li>Group的大小
	 *      <li>Group中各组件的坐标</ol>
	 * <h3>使用的注意事项：
	 *      <p><b>如果Group是嵌套在另一个Group中的，
	 *      那么内层的Group必须手动设置大小，
	 *      外层Group的高度最好手动设置</b>
	 * @param group 组
	 */
	public static void verticalCenter(Group group) {
		int size = group.size();
		int height = group.getHeight();
		if (size <= 0) return;

		int allWidth = 0;
		int allHeight = 0;
		for (IComponent component : group) {
			allWidth = Math.max(allWidth, component.getWidth());
			allHeight += component.getHeight();
		}
		height = Math.max(height, allHeight);
		group.setSize(Math.max(group.getWidth(), allWidth), height);

		int remain = height - allHeight;
		if (remain < 0) remain = 0;
		int interval = remain / (size + 1);
		interval = Math.max(interval, group.getMinDistance());
		interval = Math.min(interval, group.getMaxDistance());

		int past = (height - (allHeight + interval * (size + 1))) / 2;
		for (IComponent component : group) {
			component.setLocation((group.getWidth() - component.getWidth()) / 2 + group.getX(),
					past + interval + group.getY());
			past += interval + component.getHeight();
		}
	}

}
