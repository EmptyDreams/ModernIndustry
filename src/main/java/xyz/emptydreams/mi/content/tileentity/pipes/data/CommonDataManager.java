package xyz.emptydreams.mi.content.tileentity.pipes.data;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * 允许所有方向进行输入输出的数据管理类
 * @author EmptyDreams
 */
public final class CommonDataManager extends SrcDataManager {
	
	public CommonDataManager(EnumFacing facing, int max) {
		super(facing, max);
	}
	
	private CommonDataManager(SrcDataManager manager, EnumFacing facing) {
		super(manager, facing);
	}
	
	@Nonnull
	@Override
	public CommonDataManager rotate(EnumFacing facing) {
		this.facing = facing;
		return this;
	}
	
	@Nonnull
	@Override
	public SrcDataManager copy() {
		return new CommonDataManager(this, facing);
	}
	
}