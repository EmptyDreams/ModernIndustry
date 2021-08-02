package xyz.emptydreams.mi.content.blocks.base.pipes;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.OreDicRegister;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.content.blocks.base.TEBlockBase;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.FTStateEnum;
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
	
	public static final PropertyDirection PLUG = PropertyDirection.create("plug");
	
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
		@SuppressWarnings("ConstantConditions") IFluid cap = nowEntity.getFTCapability();
		EnumFacing facing = WorldUtil.whatFacing(pos, fromPos);
		if (block == Blocks.AIR || fromEntity == null) {
			cap.unlink(facing);
		} else {
			cap.link(facing);
		}
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing.getOpposite())) {
			TileEntity te = worldIn.getTileEntity(pos);
			IFluid cap = te.getCapability(FluidCapability.TRANSFER, facing);
			cap.setSource(facing);
			return true;
		}
		return false;
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