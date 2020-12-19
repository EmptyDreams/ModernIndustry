package xyz.emptydreams.mi.api.utils.container;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author EmptyDreams
 */
public class WeakList<T> extends IndirectList<T, WeakReference<T>> {
	
	public WeakList() {
		super();
	}
	
	@Override
	public IndirectList<T, WeakReference<T>> subList(int fromIndex, int toIndex) {
		if (fromIndex < 0 || fromIndex > size())
			throw new IndexOutOfBoundsException("fromIndex[" + fromIndex + "]应该属于[0," + toIndex + "]");
		if (toIndex < 0 || toIndex > size() || toIndex < fromIndex)
			throw new IndexOutOfBoundsException("toIndex[" + fromIndex + "]应该属于["
													+ fromIndex + "," + size() + "]");
		int size = toIndex - fromIndex;
		if (size == 0) return new WeakList<>();
		WeakList<T> result = new WeakList<>();
		Iterator<T> it = iterator();
		for (int i = 0; i != fromIndex; ++i) it.next();
		for (int i = 0; i < size; ++i) {
			result.add(it.next());
		}
		return result;
	}
	
	public WeakList(Collection<T> collection) {
		super(collection);
	}
	
	@Override
	protected WeakReference<T> createReference(T t) {
		return new WeakReference<>(t);
	}
	
}
