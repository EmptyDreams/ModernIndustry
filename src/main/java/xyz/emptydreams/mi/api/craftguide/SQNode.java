package xyz.emptydreams.mi.api.craftguide;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface SQNode {
	
	/**
	 * 需要运行的内容
	 * @param x 当前X轴
	 * @param y 当前Y轴
	 * @param element 当前元素
	 * @return 是否继续遍历，返回false可以终止遍历
	 */
	boolean accept(int x, int y, ItemElement element);
	
}
