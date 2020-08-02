package xyz.emptydreams.mi.blocks.base;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

/**
 * 放置常用的Property
 * @author EmptyDreams
 * @version V1.0
 */
public final class MIProperty {
	
	/** 状态：方向 */
	public static final PropertyDirection HORIZONTAL =
			PropertyDirection.create("horizontal", EnumFacing.Plane.HORIZONTAL);
	/** 状态：是否正在工作 */
	public static final PropertyBool WORKING = PropertyBool.create("working");
	/** 状态：是否为空 */
	public static final PropertyBool EMPTY = PropertyBool.create("empty");
	/** 状态：所有方向 */
	public static final PropertyDirection ALL_FACING = PropertyDirection.create("all_facing");
	/** 状态：竖直方向 */
	public static final PropertyDirection VERTICAL =
					PropertyDirection.create("vertical", EnumFacing.Plane.VERTICAL);
	
}
