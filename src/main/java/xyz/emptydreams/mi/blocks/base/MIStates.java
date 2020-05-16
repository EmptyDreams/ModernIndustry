package xyz.emptydreams.mi.blocks.base;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public final class MIStates {
	
	/** 状态：方向 */
	public static final PropertyDirection FACING =
			PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	/** 状态：是否正在工作 */
	public static final PropertyBool WORKING = PropertyBool.create("working");
	/** 状态：是否为空 */
	public static final PropertyBool EMPTY = PropertyBool.create("isempty");
	
}
