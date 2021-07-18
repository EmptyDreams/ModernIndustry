package xyz.emptydreams.mi.content.blocks.fluids;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import xyz.emptydreams.mi.content.blocks.properties.MIProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author EmptyDreams
 */
public enum FTStateEnum implements IStringSerializable {
	
	/** 直线 */
	STRAIGHT(facing -> {
				switch (facing) {
					case DOWN: case UP:
						return new AxisAlignedBB(1/4d, 0, 1/4d, 3/4d, 1, 3/4d);
					case NORTH: case SOUTH:
						return new AxisAlignedBB(1/4d, 1/4d, 0, 3/4d, 3/4d, 1);
					case WEST: case EAST:
						return new AxisAlignedBB(0, 1/4d, 1/4d, 1, 3/4d, 3/4d);
					default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
				}
			},
			state -> {
				ArrayList<AxisAlignedBB> result = new ArrayList<>();
				EnumFacing facing = state.getValue(MIProperty.ALL_FACING);
				switch (facing) {
					case DOWN: case UP:
						result.add(new AxisAlignedBB(5/16d, 0, 5/16d, 11/16d, 1, 11/16d));
						break;
					case NORTH: case SOUTH:
						result.add(new AxisAlignedBB(5/16d, 5/16d, 0, 11/16d, 11/16d, 1));
						break;
					case WEST: case EAST:
						result.add(new AxisAlignedBB(0, 5/16d, 5/16d, 1, 11/16d, 11/16d));
						break;
					default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
				}
				return result;
			}),
	/** 直角拐弯 */
	ANGLE(facing -> Block.FULL_BLOCK_AABB, state -> Lists.newArrayList(Block.FULL_BLOCK_AABB)),
	/** 四岔（十字） */
	SHUNT(facing -> Block.FULL_BLOCK_AABB, state -> Lists.newArrayList(Block.FULL_BLOCK_AABB));
	
	private final Function<EnumFacing, AxisAlignedBB> bounding;
	private final Function<IBlockState, Collection<AxisAlignedBB>> box;
	
	FTStateEnum(Function<EnumFacing, AxisAlignedBB> bounding, Function<IBlockState, Collection<AxisAlignedBB>> box) {
		this.bounding = bounding;
		this.box = box;
	}
	
	public void addCollisionBoxToList(IBlockState state, Consumer<AxisAlignedBB> adder) {
		Collection<AxisAlignedBB> result = box.apply(state);
		result.forEach(adder);
	}
	
	public AxisAlignedBB getBoundingBox(EnumFacing facing) {
		return bounding.apply(facing);
	}
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
}