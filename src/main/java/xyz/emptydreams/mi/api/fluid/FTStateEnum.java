package xyz.emptydreams.mi.api.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.IFluidTransfer;
import xyz.emptydreams.mi.content.blocks.base.FluidTransferBlock;
import xyz.emptydreams.mi.content.blocks.properties.MIProperty;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author EmptyDreams
 */
public enum FTStateEnum implements IStringSerializable {
	
	/** 直线 */
	STRAIGHT {
		@Override
		public void addCollisionBoxToList(IBlockState state, Consumer<AxisAlignedBB> adder) {
			AxisAlignedBB result;
			EnumFacing facing = state.getValue(MIProperty.ALL_FACING);
			switch (facing) {
				case DOWN: case UP:
					result = new AxisAlignedBB(5/16d, 0, 5/16d, 11/16d, 1, 11/16d);
					break;
				case NORTH: case SOUTH:
					result = new AxisAlignedBB(5/16d, 5/16d, 0, 11/16d, 11/16d, 1);
					break;
				case WEST: case EAST:
					result = new AxisAlignedBB(0, 5/16d, 5/16d, 1, 11/16d, 11/16d);
					break;
				default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
			}
			adder.accept(result);
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(EnumFacing facing) {
			switch (facing) {
				case DOWN: case UP:
					return new AxisAlignedBB(1/4d, 0, 1/4d, 3/4d, 1, 3/4d);
				case NORTH: case SOUTH:
					return new AxisAlignedBB(1/4d, 1/4d, 0, 3/4d, 3/4d, 1);
				case WEST: case EAST:
					return new AxisAlignedBB(0, 1/4d, 1/4d, 1, 3/4d, 3/4d);
				default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
			}
		}
		
		@Override
		public void createTileEntity(World world, BlockPos pos, FTTileEntity te, EnumFacing side) {
			EnumFacing facing = side.getOpposite();
			IFluidTransfer cap = te.getFTCapability();
			if (cap.link(facing)) {
				cap.link(side);
			} else {
				if (cap.link(side)) return;
				for (EnumFacing value : EnumFacing.values()) {
					if (cap.link(value)) {
						cap.link(value.getOpposite());
						return;
					}
				}
			}
		}
		
		@Override
		public boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing) {
			return ftFacing == plugFacing || ftFacing.getOpposite() == plugFacing;
		}
	},
	/** 直角拐弯 */
	ANGLE{
		@Override
		public void addCollisionBoxToList(IBlockState state, Consumer<AxisAlignedBB> adder) {
		
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(EnumFacing facing) {
			return Block.FULL_BLOCK_AABB;
		}
		
		@Override
		public void createTileEntity(World world, BlockPos pos, FTTileEntity te, EnumFacing side) {
		
		}
		
		@Override
		public boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing) {
			return false;
		}
	},
	/** 四岔（十字） */
	SHUNT {
		@Override
		public void addCollisionBoxToList(IBlockState state, Consumer<AxisAlignedBB> adder) {
		
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(EnumFacing facing) {
			return Block.FULL_BLOCK_AABB;
		}
		
		@Override
		public void createTileEntity(World world, BlockPos pos, FTTileEntity te, EnumFacing side) {
		
		}
		
		@Override
		public boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing) {
			return false;
		}
	};
	
	/**
	 * 获取碰撞箱
	 * @param state 方块状态
	 * @param adder 用于调用{@link Block#addCollisionBoxToList(
	 *                  BlockPos, AxisAlignedBB, List, AxisAlignedBB)}方法，
	 *                  该方法传入的是最后一个参数。具体使用方法可以参见
	 *                  {@link FluidTransferBlock#addCollisionBoxToList(
	 *                      BlockPos, AxisAlignedBB, List, AxisAlignedBB)}
	 */
	public abstract void addCollisionBoxToList(IBlockState state, Consumer<AxisAlignedBB> adder);
	
	/** 获取选取框  */
	public abstract AxisAlignedBB getBoundingBox(EnumFacing facing);
	
	/**
	 * <p>用于在方块放置时创建合适的TileEntity。
	 * <p>此时IBlockState以及默认的TileEntity已经放入世界中
	 * @param world 当前世界
	 * @param pos 当前方块坐标
	 * @param te 当前已存在的TileEntity
	 * @param side 被鼠标点击
	 */
	public abstract void createTileEntity(World world, BlockPos pos, FTTileEntity te, EnumFacing side);
	
	/**
	 * 判断指定方向是否可以设置管塞
	 * @param ftFacing 管道方向
	 * @param plugFacing 管塞方向
	 */
	public abstract boolean canSetPlug(EnumFacing ftFacing, EnumFacing plugFacing);
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
}