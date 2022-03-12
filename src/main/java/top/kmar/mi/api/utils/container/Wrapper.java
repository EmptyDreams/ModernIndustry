package top.kmar.mi.api.utils.container;

import top.kmar.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 用于盛放对象引用的容器
 * @author EmptyDreams
 */
public final class Wrapper<T> {
	
	private T object;
	
	/**
	 * 创建一个包含指定对象的容器
	 * @param o 指定对象
	 */
	public Wrapper(T o) {
		object = o;
	}
	
	/**
	 * 创建一个包含null的容器
	 */
	public Wrapper() { this(null); }
	
	@Nullable
	public T getNullable() { return object; }
	
	@Nonnull
	public T getNonnull() {
		return StringUtil.checkNull(object, "object");
	}
	
	public void set(T o) { object = o; }
	
	public boolean isNull() {
		return object == null;
	}
	
	public boolean notNull() {
		return object != null;
	}
	
	@Override
	public String toString() {
		return String.valueOf(getNullable());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Wrapper<?> wrapper = (Wrapper<?>) o;
		return Objects.equals(object, wrapper.object);
	}
	
	@Override
	public int hashCode() {
		return object != null ? object.hashCode() : 0;
	}
	
}