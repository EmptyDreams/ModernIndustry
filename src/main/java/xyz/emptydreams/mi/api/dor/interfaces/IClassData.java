package xyz.emptydreams.mi.api.dor.interfaces;

import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.SignBytes;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.data.io.DataSerialize;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static xyz.emptydreams.mi.api.dor.SignBytes.State.ONE;
import static xyz.emptydreams.mi.api.dor.SignBytes.State.ZERO;

/**
 * 类读写
 * @author EmptyDreams
 */
public interface IClassData {
	
	/**
	 * 判断是否停止读写
	 * @param clazz 当前准备进行读写的class
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	default boolean suspend(Class<?> clazz) {
		return clazz == Object.class;
	}
	
	/**
	 * 判断是否需要进行读写操作
	 * @param field 目标field
	 */
	default boolean needOperate(Field field) {
		return !Modifier.isStatic(field.getModifiers());
	}
	
	/**
	 * <p>读取所有需要读取的数据，并存储到存储器中
	 * <p>方法内部调用{@link #needOperate(Field)}判断是否进行读写，
	 *      调用{@link #write(Field, IDataWriter, Object)}进行数据读写。
	 * <p><b>重写该方法时必须重写{@link #read(Field, IDataReader, Object)}</b>
	 * @param reader 读取器
	 * @param object 数据存储器
	 */
	default void readAll(IDataReader reader, Object object) {
		Class<?> clazz = object.getClass();
		while (!suspend(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			if (fields.length == 0) {
				clazz = clazz.getSuperclass();
				continue;
			}
			SignBytes indexTag = SignBytes.read(reader, fields.length);
			IDataReader data = reader.readData();
			int i = -1;
			for (SignBytes.State state : indexTag) {
				++i;
				if (state.isZero()) continue;
				try {
					read(fields[i], data, object);
				} catch (Exception e) {
					MISysInfo.err("读取信息时出现错误，跳过该项读写!\n"
							+ "\t详细信息：\n"
							+ "\t\tfield：" + fields[i]
							+ "\n\t\tclass：" + clazz.getName()
							+ "\n\t\t下标：" + indexTag.size()
							+ "\t\t处理：跳过该项", e);
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 * <p>写入所有需要写入的数据.
	 * <p>方法内部调用{@link #needOperate(Field)}判断是否进行写入，
	 *      调用{@link #write(Field, IDataWriter, Object)}进行数据写入.
	 * <p><b>重写该方法时务必重写{@link #readAll(IDataReader, Object)}</b>
	 * @param writer 写入器
	 */
	default void writeAll(IDataWriter writer, Object object) {
		Class<?> clazz = object.getClass();
		while (!suspend(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			if (fields.length == 0) {
				clazz = clazz.getSuperclass();
				continue;
			}
			IDataOperator data = new ByteDataOperator();
			SignBytes indexTag = new SignBytes(fields.length);
			for (Field field : fields) {
				if (!needOperate(field)) {
					indexTag.add(ZERO);
					continue;
				}
				try {
					if (write(field, data, object)) indexTag.add(ONE);
					else indexTag.add(ZERO);
				} catch (Exception e) {
					MISysInfo.err("读取信息时出现错误，跳过该项读写!\n"
							+ "\t详细信息：\n"
							+ "\t\tfield：" + field
							+ "\n\t\tclass：" + clazz.getName()
							+ "\n\t\t下标：" + indexTag.size()
							+ "\t\t处理：跳过该项", e);
				}
			}
			indexTag.writeTo(writer);
			writer.writeData(data);
			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 * <p>写入指定数据.
	 * <p><b>重写该方法时务必重写{@link #read(Field, IDataReader, Object)}</b>
	 * @param field 需要进行写入的数据
	 * @param writer 写入器
	 * @throws IllegalAccessException 如果反射过程出现异常
	 */
	default boolean write(Field field, IDataWriter writer, Object object) throws IllegalAccessException {
		if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
		Object data = field.get(object);
		if (data == null) return false;
		Class<?> cast = cast(field);
		if (cast != null) {
			data = DataTypeRegister.cast(data, cast);
		}
		DataSerialize.write(writer, data, field.getType());
		return true;
	}
	
	/**
	 * <p>读取指定数据.
	 * <p><b>重写该方法时务必重写{@link #write(Field, IDataWriter, Object)}</b>
	 * @param field 需要进行读取的数据
	 * @param reader 读取器
	 * @throws IllegalAccessException 如果反射过程出现异常
	 */
	default void read(Field field, IDataReader reader, Object object) throws IllegalAccessException {
		if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
		Class<?> cast = cast(field);
		Object data = DataSerialize.read(reader, cast == null ? field.getType() : cast, field.getType(), () -> {
			try {
				return field.get(object);
			} catch (IllegalAccessException e) {
				throw TransferException.instance(e);
			}
		});
		if (cast != null) data = DataTypeRegister.cast(data, field.getType());
		field.set(object, data);
	}
	
	/**
	 * 在读取时将读取到的值转化为指定类型以及在写入时将要写入的值转化为指定类型
	 * @param field 辅助判断的field
	 * @return 要转化的目标类型，返回null表示不需要转换
	 */
	@Nullable
	default Class<?> cast(Field field) {
		return null;
	}
	
}