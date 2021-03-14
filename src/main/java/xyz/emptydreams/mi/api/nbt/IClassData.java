package xyz.emptydreams.mi.api.nbt;

import xyz.emptydreams.mi.api.exception.IntransitException;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * @author EmptyDreams
 */
public interface IClassData {
	
	default boolean needOperate(Field field) {
		return true;
	}
	
	default void forEachField(Consumer<Field> consumer) {
		for (Field field : getClass().getFields()) {
			if (needOperate(field)) {
				consumer.accept(field);
			}
		}
	}
	
	default void readAll() {
	
	}
	
	default void writeAll() {
		forEachField(field -> {
		
		});
	}
	
	default void write(Field field, IDataWriter writer) throws IllegalAccessException {
		Object data = field.get(this);
		DataTypeRegister.write(writer, data);
	}
	
	default void read(Field field, IDataReader reader) throws IllegalAccessException {
		Object data = DataTypeRegister.read(reader, field.getType(), () -> {
			try {
				return field.get(this);
			} catch (IllegalAccessException e) {
				throw new IntransitException(e);
			}
		});
		field.set(this, data);
	}
	
}