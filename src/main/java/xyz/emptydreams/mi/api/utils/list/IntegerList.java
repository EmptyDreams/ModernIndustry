package xyz.emptydreams.mi.api.utils.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import xyz.emptydreams.mi.api.net.WaitList;

/**
 * 存储int类型的集合，内部使用int[]存储数据而不是Object[]，可以节省一部分空间.
 * 实现方式与{@link ArrayList}类似
 *
 * @author EmptyDreams
 * @version V1.0
 */
public class IntegerList implements List<Integer>, RandomAccess {
	
	private int[] values;
	private int size = 0;
	private static final int[] NON = new int[0];
	
	public IntegerList(int size) {
		if (size > 0) {
			values = new int[size];
		} else if (size == 0) {
			values = NON;
		} else {
			throw new IllegalArgumentException("Illegal Capacity: "+ size);
		}
	}
	
	public IntegerList() {
		this(4);
	}
	
	public IntegerList(Collection<Integer> ints) {
		size = ints.size();
		values = new int[size];
		int index = 0;
		for (int i : ints) {
			values[index] = i;
			++index;
		}
	}
	
	public IntegerList(int[] values, int fromIndex, int toIndex) {
		this.values = Arrays.copyOfRange(values, fromIndex, toIndex);
		size = this.values.length;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(Object o) {
		if (o.getClass() == Integer.class) {
			int i = (int) o;
			for (int k : values) {
				if (k == i) return true;
			}
		}
		return false;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new IntIterator();
	}
	
	@Override
	public Object[] toArray() {
		return Arrays.stream(values, 0, size).boxed().toArray(Integer[]::new);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (!(a.getClass().isAssignableFrom(Integer[].class)))
			throw new ClassCastException("a的类型无法与Integer兼容");
		Integer[] os = (Integer[]) toArray();
		if (a.length < size())
			return (T[]) Arrays.copyOf(os, size(), a.getClass());
		System.arraycopy(os, 0, a, 0, size());
		if (a.length > size())
			a[size()] = null;
		return a;
	}
	
	@Override
	public boolean add(Integer integer) {
		capacity(size + 1);
		values[size++] = integer;
		return true;
	}
	
	@Override
	public boolean remove(Object o) {
		if (o.getClass() != Integer.class) throw new ClassCastException("该方法应传入Integer类型");
		if (o == null) return false;
		int remove = (int) o;
		for (int i = 0; i < size; ++i) {
			if (values[i] == remove) {
				fastRemove(i);
				return true;
			}
		}
		return false;
	}
	
	private void fastRemove(int index) {
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(values, index+1, values, index, numMoved);
		--size;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (o.getClass() == Integer.class) {
				if (!contains(o)) return false;
			} else {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		capacity(size + c.size());
		for (int i : c) {
			values[size++] = i;
		}
		return true;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		move(index, c.size());
		int i = index;
		for (int v : c) {
			values[i] = v;
			++i;
		}
		return true;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		WaitList.checkNull(c, "c");
		return batchRemove(c, false);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		WaitList.checkNull(c, "c");
		return batchRemove(c, true);
	}
	
	private boolean batchRemove(Collection<?> c, boolean complement) {
		final int[] elementData = this.values;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++)
				if (c.contains(elementData[r]) == complement)
					elementData[w++] = elementData[r];
		} finally {
			// Preserve behavioral compatibility with AbstractCollection,
			// even if c.contains() throws.
			if (r != size) {
				System.arraycopy(elementData, r, elementData, w, size - r);
				w += size - r;
			}
			if (w != size) {
				size = w;
				modified = true;
			}
		}
		return modified;
	}
	
	@Override
	public void clear() {
		size = 0;
	}
	
	/**
	 * 向指定方向移动指定长度
	 * @param index 移动起点
	 * @param x 向右移动距离
	 */
	private void move(int index, int x) {
		capacity(size + x);
		System.arraycopy(values, index, values, index + x, size - index);
		size += x;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		IntegerList integers = (IntegerList) o;
		
		if (size != integers.size) return false;
		return Arrays.equals(values, integers.values);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}
	
	@Override
	public Integer get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException("index[" + index + "] > " + (size - 1));
		return values[index];
	}
	
	@Override
	public Integer set(int index, Integer element) {
		if (index >= size) throw new IndexOutOfBoundsException("index[" + index + "] > " + (size - 1));
		int i = values[index];
		values[index] = element;
		return i;
	}
	
	@Override
	public void add(int index, Integer element) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException("index[" + index + "]超出范围[0, " + size + ")");
		move(index, 1);
		values[index] = element;
	}
	
	@Override
	public Integer remove(int index) {
		int i = values[index];
		move(index + 1, -1);
		return i;
	}
	
	@Override
	public int indexOf(Object o) {
		if (o.getClass() != Integer.class) return -1;
		int v = (int) o;
		for (int i = 0; i < size; ++i) {
			if (values[i] == v) return i;
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o) {
		if (o.getClass() != Integer.class) return -1;
		int v = (int) o;
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i] == v) return i;
		}
		return -1;
	}
	
	@Override
	public ListIterator<Integer> listIterator() {
		return new IntIterator();
	}
	
	@Override
	public ListIterator<Integer> listIterator(int index) {
		IntIterator it = new IntIterator();
		it.index = index;
		return it;
	}
	
	@Override
	public List<Integer> subList(int fromIndex, int toIndex) {
		return new IntegerList(values, fromIndex, toIndex);
	}
	
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	/**
	 * 扩容
	 * @param minSize 扩容后的最小容量
	 *
	 * @throws OutOfMemoryError 如果数组无法继续扩容
	 */
	public void capacity(int minSize) {
		if (minSize <= values.length) return;
		int newSize = size + (size >> 1);
		if (newSize < minSize) newSize = minSize;
		if (minSize > MAX_ARRAY_SIZE) throw new OutOfMemoryError();
		if (newSize > MAX_ARRAY_SIZE) newSize = minSize;
		values = Arrays.copyOf(values, newSize);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("IntegerList{ size=");
		sb.append(size).append(", values=[");
		int max = size - 1;
		for (int i = 0; i < size; ++i) {
			sb.append(values[i]);
			if (i == max) break;
			sb.append(", ");
		}
		sb.append("] }");
		return sb.toString();
	}
	
	private final class IntIterator implements ListIterator<Integer> {
		
		int index = -1;
		
		@Override
		public void remove() {
			IntegerList.this.remove(index--);
		}
		
		@Override
		public void set(Integer integer) {
			IntegerList.this.set(index, integer);
		}
		
		@Override
		public void add(Integer integer) {
			IntegerList.this.add(index, integer);
		}
		
		@Override
		public boolean hasNext() {
			return index + 1 < size;
		}
		
		@Override
		public Integer next() {
			try {
				return values[++index];
			} catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException("没有下一元素");
			}
		}
		
		@Override
		public boolean hasPrevious() {
			return index != 0;
		}
		
		@Override
		public Integer previous() {
			try {
				return values[--index];
			} catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException("没有上一元素");
			}
		}
		
		@Override
		public int nextIndex() {
			return ++index;
		}
		
		@Override
		public int previousIndex() {
			return --index;
		}
		
	}
	
}
