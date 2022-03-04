package top.kmar.mi.api.utils.properties;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author EmptyDreams
 */
public class PropertyAxis extends PropertyEnum<EnumFacing.Axis> {
	
	/** 创建一个包含所有方向的PropertyAxis */
	@Nonnull
	public static PropertyAxis createAll(String name) {
		return new PropertyAxis(name, Arrays.asList(EnumFacing.Axis.values()));
	}
	
	private PropertyAxis(String name, Collection<EnumFacing.Axis> allowedValues) {
		super(name, EnumFacing.Axis.class, allowedValues);
	}
	
}