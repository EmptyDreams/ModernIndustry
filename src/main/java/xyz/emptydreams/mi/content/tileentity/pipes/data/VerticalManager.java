package xyz.emptydreams.mi.content.tileentity.pipes.data;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.ListIterator;

/**
 * 竖直方向的数据存储器
 * @author EmptyDreams
 */
public class VerticalManager extends SrcDataManager {
	
	/**
	 * @param facing 正方向
	 * @param max 最大容量
	 */
	public VerticalManager(EnumFacing facing, int max) {
		super(checkEnumFacing(facing), max);
	}
	
	private VerticalManager(VerticalManager manager, EnumFacing facing) {
		super(manager, facing);
	}
	
	private static EnumFacing checkEnumFacing(EnumFacing facing) {
		if (facing.getAxis().isHorizontal())
			throw new IllegalArgumentException("输入的方向不在竖直方向上：" + facing);
		return facing;
	}
	
	@Nonnull
	@Override
	public DataManager rotate(EnumFacing facing) {
		if (facing.getAxis().isVertical()) {
			this.facing = facing;
			return this;
		} else {
			DataManager result = new HorizontalManager(facing, getMax());
			ListIterator<FluidData> it = content.listIterator(content.size());
			while (it.hasPrevious()) {
				result.insert(it.previous(), true, false);
			}
			return result;
		}
	}
	
	@Override
	public boolean isHorizontal() {
		return false;
	}
	
	@Override
	public boolean isVertical() {
		return true;
	}
	
	@Nonnull
	@Override
	public VerticalManager copy() {
		return new VerticalManager(this, facing);
	}
	
}