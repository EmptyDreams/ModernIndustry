package xyz.emptydreams.mi.api.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * @author EmptyDreams
 */
public enum FTStateEnum implements IStringSerializable {
	
	/** 直线 */
	STRAIGHT {
		@Override
		public boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing) {
			return ftFacing == plugFacing || ftFacing.getOpposite() == plugFacing;
		}
	},
	/** 直角拐弯 */
	ANGLE{
		@Override
		public boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing) {
			return false;
		}
	},
	/** 四岔（十字） */
	SHUNT {
		@Override
		public boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing) {
			return false;
		}
	};
	
	/**
	 * 判断指定方向是否可以设置管塞
	 * @param ftFacing 管道方向
	 * @param plugFacing 管塞方向
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public abstract boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing);
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
}