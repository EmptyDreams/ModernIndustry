package xyz.emptydreams.mi.data.json.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.content.blocks.properties.PropertyFluidTransfer;

import java.util.Collection;
import java.util.function.Predicate;

import static xyz.emptydreams.mi.data.json.block.BlockJsonBuilder.HOR;
import static xyz.emptydreams.mi.data.json.block.BlockJsonBuilder.VER;

/**
 * 方块类型
 * @author EmptyDreams
 */
public enum BlockPropertyType {
	
	/** bool类型 */
	BOOL("bool", it -> it instanceof PropertyBool),
	/** 方向 */
	DIR("dir", it -> it instanceof PropertyDirection),
	/** 水平方向 */
	DIR_H("dir_h", it -> {
		if (it instanceof PropertyDirection) {
			Collection<EnumFacing> values = ((PropertyDirection) it).getAllowedValues();
			if (HOR.size() != values.size()) return false;
			return HOR.containsAll(values);
		}
		return false;
	}),
	/** 竖直方向 */
	DIR_V("dir_v", it -> {
		if (it instanceof PropertyDirection) {
			Collection<EnumFacing> values = ((PropertyDirection) it).getAllowedValues();
			if (VER.size() != values.size()) return false;
			return VER.containsAll(values);
		}
		return false;
	}),
	FLUID("fluid", it -> it instanceof PropertyFluidTransfer),
	/** 所有类型 */
	ALL("*", it -> true);
	
	private final String name;
	private final Predicate<IProperty<?>> test;
	
	BlockPropertyType(String name, Predicate<IProperty<?>> test) {
		this.name = name;
		this.test = test;
	}
	
	public boolean match(IProperty<?> property) {
		return test.test(property);
	}
	
	public static BlockPropertyType from(String name) {
		for (BlockPropertyType value : BlockPropertyType.values()) {
			if (value.name.equals(name)) return value;
		}
		throw new IllegalArgumentException("输入的名称不存在：" + name);
	}
	
}