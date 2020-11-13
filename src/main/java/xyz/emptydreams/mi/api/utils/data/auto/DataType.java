package xyz.emptydreams.mi.api.utils.data.auto;

import io.netty.handler.codec.UnsupportedMessageTypeException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.interfaces.ThConsumer;
import xyz.emptydreams.mi.api.utils.container.Wrapper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 表示可以写入到NBT中的数据类型
 * @author EmptyDreams
 */
@SuppressWarnings("rawtypes")
public enum  DataType {

	/** byte and {@link Byte} */
	BYTE(DataOperator::writeByte, DataOperator::readByte),
	/** short and {@link Short} */
	SHORT(DataOperator::writeShort, DataOperator::readShort),
	/** int and {@link Integer} */
	INT(DataOperator::writeInt, DataOperator::readInt),
	/** long and {@link Long} */
	LONG(DataOperator::writeLong, DataOperator::readLong),
	/** float and {@link Float} */
	FLOAT(DataOperator::writeFloat, DataOperator::readFloat),
	/** double and {@link Double} */
	DOUBLE(DataOperator::writeDouble, DataOperator::readDouble),
	/** boolean and {@link Boolean} */
	BOOLEAN(DataOperator::writeBoolean, DataOperator::readBoolean),
	/** {@link String} */
	STRING(DataOperator::writeString, DataOperator::readString),
	/** {@link StringBuilder} */
	STRING_BUILDER(DataOperator::writeStringBuilder, DataOperator::readStringBuilder),
	/** {@link StringBuffer} */
	STRING_BUFFER(DataOperator::writeStringBuffer, DataOperator::readStringBuffer),
	/** 表示byte数组 */
	ARRAY_BYTE(DataOperator::writeByteArray, DataOperator::readByteArray),
	/** 表示int数组 */
	ARRAY_INT(DataOperator::writeIntArray, DataOperator::readIntArray),
	/** 表示{@link Byte}数组 */
	ARRAY_PACK_BYTE(DataOperator::writePackByteArray, DataOperator::readPackByteArray),
	/** 表示{@link Integer}数组 */
	ARRAY_PACK_INT(DataOperator::writePackIntArray, DataOperator::readPackIntArray),
	/** {@link java.util.UUID} */
	UNIQUE_ID(DataOperator::writeUniqueID, DataOperator::readUniqueID),
	/** {@link net.minecraft.nbt.NBTBase} */
	TAG(DataOperator::writeTag, DataOperator::readTag),
	/** {@link Enum} */
	ENUM(DataOperator::writeEnum, DataOperator::readEnum),
	/** 表示实现了{@link INBTSerializable}的类对象 */
	SERIALIZABLE(DataOperator::writeSerializable, DataOperator::readSerializable),
	/** 表示坐标({@link net.minecraft.util.math.BlockPos}) */
	POS(DataOperator::writePos, DataOperator::readPos),
	/**
	 * 表示{@link java.util.Collection}.
	 * 该类中存储的所有数据都必须支持写入到NBT中。
	 * 若存储类型为{@link #SERIALIZABLE}，则存储的类必须包含默认构造函数
	 */
	COLLECTION(DataOperator::writeCollection, DataOperator::readCollection),
	/**
	 * 表示{@link java.util.Map}.
	 * Map中的Key和Value都必须支持写入到NBT中。
	 * 若存储类型为{@link #SERIALIZABLE}，则存储的类必须包含默认构造函数
	 */
	MAP(DataOperator::writeMap, DataOperator::readMap),
	/** 表示{@link xyz.emptydreams.mi.api.electricity.interfaces.IVoltage} */
	VOLTAGE(DataOperator::writeVoltage, DataOperator::readVoltage),
	/** 表示{@link Class} */
	CLASS(DataOperator::writeClass, DataOperator::readClass),
	/** 表示{@link ItemElement} */
	ELEMENT(DataOperator::writeElement, DataOperator::readElement),
	/** 表示自动判断 */
	AUTO(DataOperator::writeAuto, null);

	private final ThConsumer writer;
	private final ThConsumer<NBTTagCompound, String, ? super DataInfo> reader;

	<T> DataType(ThConsumer<NBTTagCompound, String, T> writer,
	             ThConsumer<NBTTagCompound, String, ? super DataInfo> reader) {
		this.writer = writer;
		this.reader = reader;
	}

	/**
	 * 向NBT内写入数据
	 * @param nbt 被写入的NBT
	 * @param name 数据名称
	 * @param o 数据
	 */
	public void write(NBTTagCompound nbt, String name, Object o) {
		//noinspection unchecked
		writer.accept(nbt, name, o);
	}

	/**
	 * 从NBT内读取数据
	 * @param nbt 被读取的NBT
	 * @param name 数据名称
	 * @param dataType 数据类型，若DataType不为Auto，该项可以为null
	 * @param <T> 数据类型
	 * @return 没有读取到数据时返回null
	 * @throws ClassCastException 若数据不能转换为T
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T read(NBTTagCompound nbt, String name, Class<T> dataType) {
		if (reader == null) return DataOperator.readAuto(nbt, name, dataType);
		else {
			Wrapper result = new Wrapper();
			reader.accept(nbt, name, new DataInfo(result::set));
			return (T) result.get();
		}
	}

	public void read(NBTTagCompound nbt, String name, Object obj, Field field) {
		if (reader == null) DataOperator.readAuto(nbt, name, obj, field);
		else reader.accept(nbt, name, new DataInfo(obj, field));
	}

	public static DataType from(Class<?> type) {
		if (type == int.class || type == Integer.class) return INT;
		else if (type == boolean.class || type == Boolean.class) return BOOLEAN;
		else if (NBTBase.class.isAssignableFrom(type)) return TAG;
		else if (INBTSerializable.class.isAssignableFrom(type)) return SERIALIZABLE;
		else if (type == ItemElement.class) return ELEMENT;
		else if (type == BlockPos.class) return POS;
		else if (IVoltage.class.isAssignableFrom(type)) return VOLTAGE;
		else if (Enum.class.isAssignableFrom(type)) return ENUM;
		else if (Collection.class.isAssignableFrom(type)) return COLLECTION;
		else if (Map.class.isAssignableFrom(type)) return MAP;
		else if (type == byte.class || type == Byte.class) return BYTE;
		else if (type == byte[].class) return ARRAY_BYTE;
		else if (type == int[].class) return ARRAY_INT;
		else if (type == long.class) return LONG;
		else if (type == double.class) return DOUBLE;
		else if (type == short.class) return SHORT;
		else if (type == float.class) return FLOAT;
		else if (type == String.class) return STRING;
		else if (type == UUID.class) return UNIQUE_ID;
		else if (type == Class.class) return CLASS;
		else if (type == Byte[].class) return ARRAY_PACK_BYTE;
		else if (type == Integer[].class) return ARRAY_PACK_INT;
		else if (type == StringBuilder.class) return STRING_BUILDER;
		else if (type == StringBuffer.class) return STRING_BUFFER;
		else throw new UnsupportedMessageTypeException(type.getName());
	}
	
}