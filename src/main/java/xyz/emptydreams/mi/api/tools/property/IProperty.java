package xyz.emptydreams.mi.api.tools.property;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

/**
 * 所有属性需要实现的接口，注意：实现该接口的类必须带有默认构造函数
 * @author EmptyDreams
 */
public interface IProperty {
	
	Random RANDOM_PROPERTY = new Random();
	
	/** 获取名称 */
	String getName();
	/** 获取要显示的值 */
	String getValue();
	/** 获取原始名称 */
	String getOriginalName();
	/** 判断名称是否相等 */
	default boolean equalsName(String original) {
		return getOriginalName().equals(original);
	}
	
	void write(NBTTagCompound compound);
	void read(NBTTagCompound compound);
	@Override int hashCode();
	@Override boolean equals(Object o);
	
	static String createName(String name) {
		return createString(name, "name");
	}
	
	static String createString(String name, String suffix) {
		return "mi.property." + name + "." + suffix;
	}
	
}
