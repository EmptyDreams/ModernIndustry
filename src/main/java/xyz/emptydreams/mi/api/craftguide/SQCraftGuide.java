package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 有序合成表. <b>产物是无序的</b>
 * @author EmptyDreams
 */
public class SQCraftGuide implements ICraftGuide, Iterable<ItemElement> {
	
	/** 原料 */
	private final ItemElement[][] elements;
	/** 产品 */
	private final HashSet<ItemElement> outs = new HashSet<ItemElement>() {
		@Override
		public boolean add(ItemElement o) {
			for (ItemElement element : this) {
				if (element.merge(o)) return true;
			}
			return super.add(o);
		}
	};

	public SQCraftGuide(int xSize, int ySize) {
		if (xSize <= 0) throw new IllegalArgumentException("xSize[" + xSize + "]应当大于0");
		if (ySize <= 0) throw new IllegalArgumentException("ySize[" + ySize + "]应当大于0");
		elements = new ItemElement[ySize][xSize];
	}
	
	public SQCraftGuide() {
		this(3, 3);
	}
	
	/**
	 * 设置指定位置的元素
	 * @param element 元素
	 * @param x X轴坐标，从0开始（包括0）
	 * @param y Y轴坐标，从0开始（包括0）
	 * @throws NullPointerException 若 element == null
	 * @throws IndexOutOfBoundsException 若x或y超出范围
	 */
	public void setElement(@Nonnull ItemElement element, int x, int y) {
		WaitList.checkNull(element, "element");
		try {
			elements[y][x] = element;
		} catch (IndexOutOfBoundsException e) {
			checkIndex(x, y);
		}
	}
	
	/**
	 * 向列表添加一个产物
	 * @param element 产物
	 * @throws NullPointerException 如果 element == null
	 */
	public void addOutElement(ItemElement element) {
		WaitList.checkNull(element, "element");
		outs.add(element);
	}
	
	/**
	 * 从合成表中删除一个产物
	 * @param item 指定的物品
	 * @return 是否删除成功
	 */
	public boolean removeOutItem(Item item) {
		ItemElement element;
		Iterator<ItemElement> it = outs.iterator();
		while (it.hasNext()) {
			element = it.next();
			if (element.contrastWith(item)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 从合成表中删除一个产物
	 * @param element 指定的产物
	 * @return 是否删除成功
	 */
	public boolean removeOutElement(ItemElement element) {
		ItemElement itemElement;
		Iterator<ItemElement> it = outs.iterator();
		while (it.hasNext()) {
			itemElement = it.next();
			if (itemElement.contrastWith(element)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取指定位置的元素.
	 * <b>该方法可能会触发补白操作</b>
	 * @param x X轴坐标，从0开始（包括0）
	 * @param y Y轴坐标，从0开始（包括0）
	 * @throws IndexOutOfBoundsException 如果x或y超出了范围
	 */
	@Nonnull
	public ItemElement get(int x, int y) {
		checkIndex(x, y);
		return elements[y][x];
	}
	
	@Override
	public boolean apply(Object craft) {
		if (craft == this) return true;
		if (!(craft instanceof SQCraftGuide)) return false;
		SQCraftGuide sq = (SQCraftGuide) craft;
		if (sq.xSize() != xSize() || sq.ySize() != ySize()) return false;
		AtomicBoolean result = new AtomicBoolean(true);
		forEach((x, y, element) -> {
			if (element.equals(sq.get(x, y))) return true;
			result.set(false);
			return false;
		});
		return result.get();
	}
	
	@Override
	public boolean apply(ItemStack... stacks) {
		if (stacks.length != xSize() * ySize()) return false;
		int i = 0;
		for (ItemElement element : this) {
			if (!element.contrastWith(ItemElement.instance(stacks[i++]))) return false;
		}
		return true;
	}
	
	@Override
	public boolean apply(Iterable<ItemStack> stacks) {
		int x = 0, y = 0;
		ItemElement element;
		for (ItemStack stack : stacks) {
			element = get(x, y);
			if (!element.contrastWith(ItemElement.instance(stack))) return false;
			if (++x >= xSize()) {
				x = 0;
				++y;
			}
		}
		return true;
	}
	
	@Override
	public boolean hasItem(Item item) {
		for (ItemElement element : this) {
			if (element.contrastWith(item)) return true;
		}
		return false;
	}
	
	@Nonnull
	@Override
	public List<ItemElement> getOuts() {
		return new ArrayList<>(outs);
	}

	@Override
	public ItemElement getFirstOut() {
		return outs.iterator().next();
	}

	/** 空白填充，用于将合成表中null部分填充为{@link ItemElement#empty()} */
	public void fillBlank() {
		for (int y = 0; y < elements.length; y++) {
			for (int x = 0; x < elements[y].length; x++) {
				if (elements[y][x] == null) elements[y][x] = ItemElement.empty();
			}
		}
	}
	
	public int xSize() { return elements[0].length; }
	public int ySize() { return elements.length; }
	public int outSize() { return outs.size(); }
	/**
	 * 检查下标是否正确，与虚拟机相比该方法能提供更准确的错误信息
	 */
	private void checkIndex(int x, int y) {
		if (y >= elements.length)
			throw new IndexOutOfBoundsException("y[" + y + "]值超出了极限{[0, " + elements.length + ")}");
		if (x >= elements[0].length)
			throw new IndexOutOfBoundsException("x[" + x + "]值超出了极限{[0, " + elements[0].length + ")}");
	}
	
	/**
	 * 迭代顺序：从上到下，从左到右，不会遍历产物
	 */
	@Override
	@Nonnull
	public Iterator<ItemElement> iterator() {
		return new Iterator<ItemElement>() {
			
			int nowX = 0, nowY = 0;
			
			@Override
			public boolean hasNext() {
				return nowX != xSize() - 1 || nowY != ySize() - 1;
			}
			
			@Override
			public ItemElement next() {
				if (nowX == xSize() - 1) {
					if (nowY == ySize() - 1)
						throw new NoSuchElementException("now index = [" + nowX + ", " + nowY + "]");
					++nowY;
					nowX = 0;
					return get(nowX, nowY);
				}
				return get(++nowX, nowY);
			}
			
			@Override
			public void remove() {
				setElement(ItemElement.empty(), nowX, nowY);
			}
		};
	}
	
	/**
	 * 遍历所有元素
	 */
	public void forEach(SQNode node) {
		for (int y = 0; y < ySize(); ++y) {
			for (int x = 0; x < xSize(); ++x) {
				if (!node.accept(x, y, get(x, y))) return;
			}
		}
	}
	
}