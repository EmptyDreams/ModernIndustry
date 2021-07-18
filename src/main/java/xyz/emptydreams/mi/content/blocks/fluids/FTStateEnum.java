package xyz.emptydreams.mi.content.blocks.fluids;

import net.minecraft.util.IStringSerializable;

/**
 * @author EmptyDreams
 */
public enum FTStateEnum implements IStringSerializable {
	
	/** 直线 */
	STRAIGHT(),
	/** 直角拐弯 */
	ANGLE(),
	/** 四岔（十字） */
	SHUNT();
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
}