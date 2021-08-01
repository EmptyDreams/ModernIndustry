package xyz.emptydreams.mi.content.blocks.base.pipes.enums;

import net.minecraft.util.IStringSerializable;

/**
 * 管道的几种状态
 * @author EmptyDreams
 */
public enum FTStateEnum implements IStringSerializable {
	
	/** 直线 */
	STRAIGHT,
	/** 直角拐弯 */
	ANGLE,
	/** 四岔（十字） */
	SHUNT;
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
}