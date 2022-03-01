package xyz.emptydreams.mi.content.blocks.base.pipes;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.OreDicRegister;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.content.blocks.base.TEBlockBase;
import xyz.emptydreams.mi.content.items.base.ItemBlockExpand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("deprecation")
abstract public class Pipe extends TEBlockBase {
	
	protected final Item ITEM;
	
	public Pipe(String name, String... ores) {
		super(Material.IRON);
		setSoundType(SoundType.SNOW);
		setHardness(0.5F);
		setCreativeTab(ModernIndustry.TAB_WIRE);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(StringUtil.getUnlocalizedName(name));
		OreDicRegister.registry(this, ores);
		ITEM = new ItemBlockExpand(this);
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity fromEntity = world.getTileEntity(neighbor);
		Block block = fromEntity == null ? world.getBlockState(neighbor).getBlock() : fromEntity.getBlockType();
		if (block instanceof Pipe) return;
		FTTileEntity nowEntity = (FTTileEntity) world.getTileEntity(pos);
		EnumFacing facing = WorldUtil.whatFacing(pos, neighbor);
		if (fromEntity == null) nowEntity.unlink(facing);
		else nowEntity.link(facing);
		nowEntity.markDirty();
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
	                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
	                                  @Nullable Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes,
				new AxisAlignedBB(5/16d, 5/16d, 5/16d, 11/16d, 11/16d, 11/16d));
		FTTileEntity transport = (FTTileEntity) worldIn.getTileEntity(pos);
		for (EnumFacing facing : EnumFacing.values()) {
			if (transport.hasAperture(facing))
                addCollisionBoxToList(facing, pos, entityBox, collidingBoxes);
		}
	}
	
	protected void addCollisionBoxToList(EnumFacing facing, BlockPos pos,
	                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes) {
		switch (facing) {
			case DOWN:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 0, 5/16d, 11/16d, 5/16d, 11/16d));
				break;
			case UP:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 11/16d, 5/16d, 11/16d, 1, 11/16d));
				break;
			case NORTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 5/16d, 0, 11/16d, 11/16d, 5/16d));
				break;
			case SOUTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 5/16d, 11/16d, 11/16d, 11/16d, 1));
				break;
			case WEST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(0, 5/16d, 5/16d, 5/16d, 11/16d, 11/16d));
				break;
			case EAST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(11/16d, 5/16d, 5/16d, 1, 11/16d, 11/16d));
				break;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return new AxisAlignedBB(3 / 16d, 3 / 16d, 3 / 16d, 10 / 16d, 10 / 16d, 10 / 16d);
	}
	
	@Override
	abstract public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos);
	
	@Override
	abstract public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos);
	
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
	
}