package xyz.emptydreams.mi.api.utils.data.auto;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 存储相关信息
 * @author EmptyDreams
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class DataInfo<T> implements Consumer<T> {

	private final Object OBJ;
	private final Field FIELD;
	private final Consumer OTHER;
	private Object def;

	public DataInfo(Object obj, Field field) {
		OBJ = obj;
		FIELD = field;
		OTHER = null;
	}

	public DataInfo(Consumer other) {
		OBJ = null;
		FIELD = null;
		OTHER = other;
	}

	/**
	 * 设置默认值
	 * @param obj 为空表示不显式提供默认值，由MI自动计算默认值
	 */
	public DataInfo<T> setDefault(Object obj) {
		def = obj;
		return this;
	}

	/** 获取默认值 */
	public T getDefault() {
		try {
			if (def != null) return (T) def;
			if (FIELD == null) return null;
			return (T) FIELD.get(OBJ);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/** 赋值 */
	@Override
	public void accept(T t) {
		if (FIELD == null) OTHER.accept(t);
		else DataOperator.writeToField(OBJ, FIELD, t);
	}

}
