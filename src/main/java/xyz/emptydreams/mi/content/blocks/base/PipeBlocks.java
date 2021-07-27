package xyz.emptydreams.mi.content.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.fluid.FTStateEnum;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.FluidTransferCapability;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.IFluidTransfer;
import xyz.emptydreams.mi.api.register.OreDicRegister;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.content.items.base.ItemBlockExpand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static xyz.emptydreams.mi.content.blocks.properties.MIProperty.ALL_FACING;

/**
 * 流体管道的方块
 * @author EmptyDreams
 */
@SuppressWarnings("deprecation")
public final class PipeBlocks {
	
	public static class StraightPipe extends Pipe {
		
		/** 前方是否有管塞 */
		public static final PropertyBool BEFORE = PropertyBool.create("before");
		/** 后方是否有管塞 */
		public static final PropertyBool AFTER = PropertyBool.create("after");
		
		public StraightPipe(String name, String... ores) {
			super(name, FTStateEnum.STRAIGHT, ores);
			setDefaultState(blockState.getBaseState().withProperty(ALL_FACING, EnumFacing.NORTH)
					.withProperty(BEFORE, false).withProperty(AFTER, false));
		}
		
		@Nullable
		@Override
		public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
			return new AxisAlignedBB(3/16d, 3/16d, 3/16d, 10/16d, 10/16d, 10/16d);
		}
		
		@Override
		public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
			FTTileEntity te = (FTTileEntity) worldIn.getTileEntity(pos);
			@SuppressWarnings("ConstantConditions")
			FTTileEntity.FluidCapability cap = te.getFTCapability();
			EnumFacing facing = cap.getFacing();
			return state.withProperty(BEFORE, cap.hasPlug(facing))
						.withProperty(AFTER, cap.hasPlug(facing.getOpposite()))
						.withProperty(ALL_FACING, facing);
		}
		
		@Override
		public TileEntity createTileEntity(ItemStack stack, EntityPlayer player,
		                                   World world, BlockPos pos, EnumFacing side,
		                                   float hitX, float hitY, float hitZ) {
			FTTileEntity te = (FTTileEntity) world.getTileEntity(pos);
			EnumFacing facing = side.getOpposite();
			@SuppressWarnings("ConstantConditions")
			IFluidTransfer cap = te.getFTCapability();
			cap.setFluid(new FluidStack(FluidRegistry.WATER, 1000));
			if (link(world, pos, cap, facing)) {
				link(world, pos, cap, side);
				te.markDirty();
				return null;
			}
			if (link(world, pos, cap, side)) {
				te.markDirty();
				return null;
			}
			for (EnumFacing value : EnumFacing.values()) {
				if (link(world, pos, cap, value)) {
					link(world, pos, cap, value.getOpposite());
					te.markDirty();
					return null;
				}
			}
			cap.setFacing(MathUtil.getPlayerFacing(player, pos));
			te.markDirty();
			return null;
		}
		
		@SuppressWarnings("ConstantConditions")
		private static boolean link(World world, BlockPos pos, IFluidTransfer cap, EnumFacing facing) {
			if (!cap.link(facing)) return false;
			TileEntity thatTE = world.getTileEntity(pos.offset(facing));
			IFluidTransfer that =thatTE.getCapability(FluidTransferCapability.TRANSFER, null);
			cap.setFacing(facing);
			EnumFacing side = facing.getOpposite();
			EnumFacing old = that.getFacing();
			that.setFacing(side);
			if (!that.link(side)) {
				cap.unlink(facing);
				that.setFacing(old);
				thatTE.markDirty();
				return false;
			}
			return true;
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
			FTTileEntity te = (FTTileEntity) source.getTileEntity(pos);
			if (te == null) return FULL_BLOCK_AABB;
			EnumFacing facing = te.getFTCapability().getFacing();
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
		public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
		                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
		                                  @Nullable Entity entityIn, boolean isActualState) {
			EnumFacing facing = state.getValue(ALL_FACING);
			switch (facing) {
				case DOWN: case UP:
					addCollisionBoxToList(pos, entityBox, collidingBoxes,
							new AxisAlignedBB(5/16d, 0, 5/16d, 11/16d, 1, 11/16d));
					break;
				case NORTH: case SOUTH:
					addCollisionBoxToList(pos, entityBox, collidingBoxes,
							new AxisAlignedBB(5/16d, 5/16d, 0, 11/16d, 11/16d, 1));
					break;
				case WEST: case EAST:
					addCollisionBoxToList(pos, entityBox, collidingBoxes,
							new AxisAlignedBB(0, 5/16d, 5/16d, 1, 11/16d, 11/16d));
					break;
				default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
			}
		}
		
		@Nonnull
		@Override
		protected BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, ALL_FACING, BEFORE, AFTER);
		}
		
	}
	
	abstract public static class Pipe extends TEBlockBase {
		
		protected final Item ITEM;
		protected final FTStateEnum state;
		
		public Pipe(String name, FTStateEnum stateEnum, String... ores) {
			super(Material.IRON);
			state = stateEnum;
			setSoundType(SoundType.SNOW);
			setHardness(0.5F);
			setCreativeTab(ModernIndustry.TAB_WIRE);
			setRegistryName(ModernIndustry.MODID, name);
			setUnlocalizedName(StringUtil.getUnlocalizedName(name));
			OreDicRegister.registry(this, ores);
			ITEM = new ItemBlockExpand(this);
		}
		
		@Override
		public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
			TileEntity fromEntity = worldIn.getTileEntity(fromPos);
			Block block = fromEntity == null ? worldIn.getBlockState(fromPos).getBlock() : fromEntity.getBlockType();
			FTTileEntity nowEntity = (FTTileEntity) worldIn.getTileEntity(pos);
			@SuppressWarnings("ConstantConditions") FTTileEntity.FluidCapability cap = nowEntity.getFTCapability();
			EnumFacing facing = WorldUtil.whatFacing(pos, fromPos);
			if (block == Blocks.AIR || fromEntity == null) {
				cap.unlink(facing);
			} else {
				cap.link(facing);
			}
		}
		
		@Override
		abstract public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos);
		
		@Override
		abstract public TileEntity createTileEntity(ItemStack stack, EntityPlayer player,
		                                            World world, BlockPos pos,
		                                            EnumFacing side, float hitX, float hitY, float hitZ);
		
		@Override
		abstract public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos);
		
		@Override
		abstract public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
		                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
		                                  @Nullable Entity entityIn, boolean isActualState);
		
		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}
		
		@Override
		public boolean isFullCube(IBlockState state) {
			return false;
		}
		
		@Nonnull
		@Override
		public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
			return new ItemStack(ITEM);
		}
		
		@Override
		public int quantityDropped(@Nonnull Random random) {
			return 1;
		}
		
		@Override
		public int getMetaFromState(@Nonnull IBlockState state) {
			return 0;
		}
		
		@Nonnull
		@Override
		public IBlockState getStateFromMeta(int meta) {
			return getDefaultState();
		}
		
		@Nonnull
		@Override
		public Item getBlockItem() {
			return ITEM;
		}
		
		@Nullable
		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new FTTileEntity(this.state);
		}
		
	}
	
}