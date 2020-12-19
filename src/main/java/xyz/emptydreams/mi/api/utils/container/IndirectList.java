package xyz.emptydreams.mi.api.utils.container;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author EmptyDreams
 */
public abstract class IndirectList<T, V extends Reference<T>> implements Iterable<T> {
	
	private final List<V> VALUES = new LinkedList<>();
	
	public IndirectList() {
	}
	
	public IndirectList(Collection<? extends T> collection) {
		collection.forEach(value -> VALUES.add(createReference(value)));
	}
	
	public boolean add(T t) {
		return VALUES.add(createReference(t));
	}
	
	public void add(int index, T t) {
		VALUES.add(index, createReference(t));
	}
	
	public void addAll(Collection<T> collection) {
		collection.forEach(this::add);
	}
	
	public void addAll(int index, Collection<T> collection) {
		IntWrapper now = new IntWrapper(index);
		collection.forEach(value -> add(now.getAndIncrement(), value));
	}
	
	public void addAll(T[] array) {
		for (T t : array) add(t);
	}
	
	public void addAll(int index, T[] array) {
		for (int i = 0; i < array.length; i++) {
			add(index + i, array[i]);
		}
	}
	
	public T remove(int index) {
		return VALUES.remove(index).get();
	}
	
	public boolean removeAll(Collection<T> collection) {
		return VALUES.removeIf(reference -> collection.contains(reference.get()));
	}
	
	public boolean removeIf(Predicate<? super T> predicate) {
		return VALUES.removeIf(value -> predicate.test(value.get()));
	}
	
	public void clear() {
		VALUES.clear();
	}
	
	public int size() {
		return VALUES.size();
	}
	
	public T get(int index) {
		return VALUES.get(index).get();
	}
	
	public boolean isEmpty() {
		return size() != 0;
	}
	
	public boolean contains(T value) {
		for (T t : this) {
			if (t.hashCode() == value.hashCode() && t.equals(value)) return true;
		}
		return false;
	}
	
	public boolean containsAll(Collection<? extends T> collection) {
		for (T o : collection) {
			if (!contains(o)) return false;
		}
		return true;
	}
	
	public void sort(Comparator<? super T> c) {
		VALUES.sort((r0, r1) -> c.compare(r0.get(), r1.get()));
	}
	
	public abstract IndirectList<T, V> subList(int fromIndex, int toIndex);
	
	public Object[] toArray() {
		return VALUES.stream().map(Reference::get).toArray();
	}
	
	public T[] toArray(T[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = get(i);
		}
		return array;
	}
	
	public Stream<T> stream() {
		return VALUES.stream().map(Reference::get);
	}
	
	public Stream<T> parallelStream() {
		return VALUES.parallelStream().map(Reference::get);
	}
	
	protected abstract V createReference(T t);
	
	@Override
	public ListIterator<T> iterator() {
		return new InnerIterator();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		IndirectList<?, ?> that = (IndirectList<?, ?>) o;
		
		return VALUES.equals(that.VALUES);
	}
	
	@Override
	public int hashCode() {
		return VALUES.hashCode();
	}
	
	@Override
	public String toString() {
		return VALUES.toString();
	}
	
	private final class InnerIterator implements ListIterator<T> {
		
		private final ListIterator<V> iterator = VALUES.listIterator();
		
		@Override
		public boolean hasNext() {
			T now;
			while (iterator.hasNext()) {
				now = iterator.next().get();
				if (now != null) {
					iterator.previous();
					return true;
				} else {
					remove();
				}
			}
			return false;
		}
		
		@Override
		public void remove() {
			iterator.remove();
		}
		
		@Override
		public void set(T t) {
			iterator.set(createReference(t));
		}
		
		@Override
		public void add(T t) {
			iterator.add(createReference(t));
		}
		
		@Override
		public T next() {
			T next = iterator.next().get();
			if (next == null) {
				iterator.remove();
				return next();
			}
			return next;
		}
		
		@Override
		public boolean hasPrevious() {
			T now;
			while (iterator.hasPrevious()) {
				now = iterator.previous().get();
				if (now != null) {
					iterator.next();
					return true;
				} else {
					remove();
				}
			}
			return false;
		}
		
		@Override
		public T previous() {
			T pre = iterator.previous().get();
			if (pre == null) {
				iterator.remove();
				return previous();
			}
			return pre;
		}
		
		@Override
		public int nextIndex() {
			T next = iterator.next().get();
			if (next == null) {
				iterator.remove();
				return nextIndex();
			}
			iterator.previous();
			return iterator.nextIndex();
		}
		
		@Override
		public int previousIndex() {
			T pre = iterator.previous().get();
			if (pre == null) {
				iterator.remove();
				return previousIndex();
			}
			iterator.next();
			return iterator.previousIndex();
		}
		
	}
	
}
