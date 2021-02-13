package xyz.emptydreams.mi.api.craftguide.sol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;
import xyz.emptydreams.mi.api.craftguide.ItemElement;

import java.util.Iterator;

/**
 * 有序物品列表
 * @author EmptyDreams
 */
public class ItemList implements ItemSol, Iterable<ItemList.Node> {
	
	/** 列表的长度和宽度 */
	private final int width, height;
	/** 列表的内容 */
	private final ItemElement[][] elements;
	
	/**
	 * 创建指定大小的物品列表
	 * @param width 宽度
	 * @param height 高度
	 */
	public ItemList(int width, int height) {
		this.width = width;
		this.height = height;
		elements = new ItemElement[height][width];
	}
	
	/** 根据已有的物品列表创建一份拷贝 */
	public ItemList(ItemList sol) {
		width = sol.getWidth();
		height = sol.getHeight();
		elements = sol.elements.clone();
	}
	
	/**
	 * 设置列表中指定位置的内容
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @param element 要设置为的内容
	 * @throws IndexOutOfBoundsException 如果x>={@link #getWidth()}或y>={@link #getHeight()}
	 */
	public void set(int x, int y, ItemElement element) {
		elements[y][x] = element;
	}
	
	/**
	 * 读取列表中指定位置的内容
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @throws IndexOutOfBoundsException 如果x>={@link #getWidth()}或y>={@link #getHeight()}
	 */
	public ItemElement get(int x, int y) {
		return elements[y][x];
	}
	
	@Override
	public int size() {
		return getWidth() * getHeight();
	}
	
	/** 获取列表的宽度 */
	public int getWidth() {
		return width;
	}
	
	/** 获取列表的高度 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * 判断指定位置是否为空
	 * @param x X轴坐标
	 * @param y Y轴坐标
	 * @throws IndexOutOfBoundsException 如果x>={@link #getWidth()}或y>={@link #getHeight()}
	 */
	public boolean isEmpty(int x, int y) {
		ItemElement element = get(x, y);
		return element == null || element.isEmpty();
	}
	
	@Override
	public boolean hasElement(ItemElement element) {
		for (Node node : this) {
			if (node.getElement().contain(element)) return true;
		}
		return false;
	}
	
	@Override
	public boolean hasItem(ItemStack stack) {
		for (Node node : this) {
			if (node.getElement().contain(stack)) return true;
		}
		return false;
	}
	
	@Override
	public boolean apply(ItemSol sol) {
		if (!(sol instanceof ItemList)) return false;
		ItemList that = (ItemList) sol;
		if (that.getWidth() != getWidth() || that.getHeight() != getHeight()) return false;
		for (Node node : this) {
			ItemElement gt = that.get(node.getX(), node.getY());
			if (gt == null) {
				if (node.getElement() != null) return false;
			} else if (!gt.contain(node.getElement())) return false;
		}
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return width == 0 || height == 0;
	}
	
	@Override
	public boolean fill(ItemList sol) {
		if (sol.getHeight() <= getHeight() && sol.getWidth() <= getWidth()) {
			for (Node node : sol) {
				sol.set(node.getX(), node.getY(), get(node.getX(), node.getY()));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public ItemList copy() {
		return new ItemList(this);
	}
	
	@Override
	public ItemList offset() {
		int realWidth = getHeight(), realHeight = getWidth();
		int startX = 0, endX = width - 1;
		int startY = 0, endY = height - 1;
		//从上向下扫描
		o : for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				if (!isEmpty(x, y)) break o;
			}
			--realHeight;
			++startY;
		}
		if (realHeight == 0) return new ItemList(0, 0);
		//从下向上扫描
		o : for (int y = endY; y >= 0; --y) {
			for (int x = 0; x < width; ++x) {
				if (!isEmpty(x, y)) break o;
			}
			--realHeight;
			--endY;
		}
		//从左向右扫描
		o : for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (!isEmpty(x, y)) break o;
			}
			--realWidth;
			++startX;
		}
		//从右向左扫描
		o : for (int x = endX; x >= 0; --x) {
			for (int y = 0; y < height; ++y) {
				if (!isEmpty(x, y)) break o;
			}
			--realWidth;
			--endX;
		}
		ItemList copy = new ItemList(realWidth, realHeight);
		for (int x = startX; x <= endX; ++x) {
			for (int y = startY; y <= endY; ++y) {
				copy.set(x - startX, y - startY, get(x, y));
			}
		}
		return copy;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return apply((ItemSol) o);
	}
	
	@Override
	public int hashCode() {
		return getWidth() * 31 + getHeight() * 31;
	}
	
	@Override
	public String toString() {
		return "[width,height]=[" + getWidth() + "," +getHeight() +
				"],elements=" + Arrays.toString(elements);
	}
	
	/** 解析JSON */
	public static ItemList parse(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		JsonArray pattern = json.getAsJsonArray("pattern");                     //合成表列表
		String[] sols = new String[pattern.size()];                                         //存储原料列表
		//解析原料列表
		for (int i = 0; i < pattern.size(); i++) {
			sols[i] = pattern.get(i).getAsString();
		}
		
		//创建ItemList
		int width = sols[0].length();
		int height = sols.length;
		ItemList list = new ItemList(width, height);
		//填充
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				list.set(x, y, keyMap.get(sols[y].charAt(x)));
			}
		}
		return list;
	}
	
	@Override
	public Iterator<Node> iterator() {
		return new ItemIterator();
	}
	
	public static final class Node {
		
		private final int x, y;
		private final ItemElement element;
		
		private Node(int x, int y, ItemElement element) {
			this.x = x;
			this.y = y;
			this.element = element;
		}
		
		public int getX() { return x; }
		public int getY() { return y; }
		public ItemElement getElement() { return element; }
	}
	
	public final class ItemIterator implements Iterator<Node> {
		
		int x = 0, y = 0;
		
		public void set(ItemElement element) {
			ItemList.this.set(x, y, element);
		}
		
		@Override
		public void remove() {
			ItemList.this.set(x, y, ItemElement.empty());
		}
		
		@Override
		public boolean hasNext() {
			return x < getWidth() && y < getHeight();
		}
		
		@Override
		public Node next() {
			int tx = x, ty = y;
			if (++x == getWidth()) {
				x = 0;
				++y;
			}
			return new Node(tx, ty, get(tx, ty));
		}
		
	}
	
}