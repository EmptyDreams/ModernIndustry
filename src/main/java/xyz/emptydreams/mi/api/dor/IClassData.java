package xyz.emptydreams.mi.api.dor;

import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import xyz.emptydreams.mi.api.exception.IntransitException;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static xyz.emptydreams.mi.api.dor.SignBytes.State.ONE;
import static xyz.emptydreams.mi.api.dor.SignBytes.State.ZERO;

/**
 * @author EmptyDreams
 */
public interface IClassData {
	
	/**
	 * 判断是否需要进行读写操作
	 * @param field 目标field
	 */
	default boolean needOperate(Field field) {
		int mod = field.getModifiers();
		//不对静态及终态的field进行读写
		return !(Modifier.isStatic(mod) || Modifier.isFinal(mod));
	}
	
	/**
	 * <p>读取所有需要读取的数据.
	 * <p>方法内部调用{@link #needOperate(Field)}判断是否进行读写，
	 *      调用{@link #write(Field, IDataWriter)}进行数据读写。
	 * <p><b>重写该方法时必须重写{@link #read(Field, IDataReader)}</b>
	 * @param reader 读取器
	 */
	default void readAll(IDataReader reader) {
		Class<?> clazz = getClass();
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			SignBytes indexTag = SignBytes.read(reader);
			int i = 0;
			ByteListIterator iterator = indexTag.iterator();
			//noinspection WhileLoopReplaceableByForEach
			while (iterator.hasNext()) {
				byte tag = iterator.next();
				if (tag == 0) continue;
				try {
					read(fields[i], reader);
					++i;
				} catch (Exception e) {
					MISysInfo.err("读取信息时出现错误，跳过该项读写!\n"
							+ "\t详细信息：\n"
							+ "\t\t下标：" + indexTag.size()
							+ "\t\t名称：" + fields[i].getName()
							+ "\t\t处理：跳过该项", e);
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 * <p>写入所有需要写入的数据.
	 * <p>方法内部调用{@link #needOperate(Field)}判断是否进行写入，
	 *      调用{@link #write(Field, IDataWriter)}进行数据写入.
	 * <p><b>重写该方法时务必重写{@link #readAll(IDataReader)}</b>
	 * @param writer 写入器
	 */
	default void writeAll(IDataWriter writer) {
		Class<?> clazz = getClass();
		while (clazz != null) {
			int start = writer.nowWriteIndex();
			Field[] fields = clazz.getDeclaredFields();
			SignBytes indexTag = new SignBytes(fields.length);
			for (Field field : fields) {
				if (!needOperate(field)) {
					indexTag.add(ZERO);
					continue;
				}
				try {
					if (write(field, writer)) indexTag.add(ONE);
					else indexTag.add(ZERO);
				} catch (Exception e) {
					MISysInfo.err("写入信息时出现错误，跳过该项读写!\n"
								+ "\t详细信息：\n"
								+ "\t\t下标：" + indexTag.size()
								+ "\t\t名称：" + field.getName()
								+ "\t\t处理：跳过该项", e);
				}
			}
			indexTag.writeTo(start, writer);
			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 * <p>写入指定数据.
	 * <p><b>重写该方法时务必重写{@link #read(Field, IDataReader)}</b>
	 * @param field 需要进行写入的数据
	 * @param writer 写入器
	 * @throws IllegalAccessException 如果反射过程出现异常
	 */
	default boolean write(Field field, IDataWriter writer) throws IllegalAccessException {
		if (Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers())) {
			field.setAccessible(true);
		}
		Object data = field.get(this);
		if (data == null) return false;
		DataTypeRegister.write(writer, data);
		return true;
	}
	
	/**
	 * <p>读取指定数据.
	 * <p><b>重写该方法时务必重写{@link #write(Field, IDataWriter)}</b>
	 * @param field 需要进行读取的数据
	 * @param reader 读取器
	 * @throws IllegalAccessException 如果反射过程出现异常
	 */
	default void read(Field field, IDataReader reader) throws IllegalAccessException {
		if (Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers())) {
			field.setAccessible(true);
		}
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