package xyz.emptydreams.mi.api.utils;

import net.minecraftforge.common.util.INBTSerializable;

/**
 * 表示可以写入到NBT中的数据类型
 * @author EmptyDreams
 * @version V1.0
 */
public enum  DataType {
	
	BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, STRING,
	ARRAY_BYTE, ARRAY_INT, UNIQUE_ID, TAG, ENUM,
	/** 表示实现了{@link INBTSerializable}的类对象 */
	OTHER,
	/** 表示自动判断 */
	AUTO,
	/** 表示坐标({@link net.minecraft.util.math.BlockPos}) */
	POS,
	/**
	 * 表示{@link java.util.Collection}.
	 * 该类中存储的所有数据都必须支持写入到NBT中。
	 * 若存储类型为{@link #OTHER}，则存储的类必须包含默认构造函数
	 */
	COLLECTION,
	/**
	 * 表示{@link java.util.Map}.
	 * Map中的Key和Value都必须支持写入到NBT中。
	 * 若存储类型为{@link #OTHER}，则存储的类必须包含默认构造函数
	 */
	MAP
	
}
