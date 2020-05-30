package xyz.emptydreams.mi.blocks.machine.user;

import java.util.Random;

import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.src.block.MachineBlock;
import xyz.emptydreams.mi.blocks.te.user.EUCompressor;
import xyz.emptydreams.mi.gui.CompressorFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static xyz.emptydreams.mi.blocks.base.MIProperty.EMPTY;
import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 压缩机
 * @author EmptyDremas
 * @version V1.0
 */
@SuppressWarnings("deprecation")
@AutoBlockRegister(registryName = CompressorBlock.NAME)
public class CompressorBlock extends MachineBlock {
	
	/** 方块内部名称 */
	public static final String NAME = "compressor_tblock";
	
	private final Item ITEM;
	
	public CompressorBlock() {
		super(Material.ROCK);
		setHarvestLevel("pickaxe", 1);
		setHardness(3.5F);
		setCreativeTab(ModernIndustry.TAB_BLOCK);
		ITEM = new ItemBlock(this).setRegistryName(ModernIndustry.MODID, NAME);
		
		setDefaultState(blockState.getBaseState().withProperty(
				FACING, EnumFacing.EAST).withProperty(WORKING, false).withProperty(EMPTY, false));
	}
	
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)) return false;
		if (!worldIn.isRemote) {
			playerIn.openGui(ModernIndustry.instance,
					CompressorFrame.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
		    IBlockState iblockstate = worldIn.getBlockState(pos.north());
		    IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
		    IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
		    IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
		    EnumFacing enumfacing = state.getValue(FACING);
		    if (enumfacing == EnumFacing.NORTH &&
				        iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
		        enumfacing = EnumFacing.SOUTH;
		    } else if (enumfacing == EnumFacing.SOUTH &&
				        iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
		        enumfacing = EnumFacing.NORTH;
		    } else if (enumfacing == EnumFacing.WEST &&
				        iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
		        enumfacing = EnumFacing.EAST;
		    } else if (enumfacing == EnumFacing.EAST &&
				        iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
		        enumfacing = EnumFacing.WEST;
		    }
		    
		    worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing)
		                                      .withProperty(WORKING, false)
		                                      .withProperty(EMPTY, false), 2);
		}
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world,
	                     BlockPos pos, IBlockState state, int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		EUCompressor nbt = (EUCompressor) world.getTileEntity(pos);
		ItemStack is = nbt.getSolt(0).getStack();
		ItemStack is2 = nbt.getSolt(1).getStack();
		ItemStack is3 = nbt.getSolt(2).getStack();
		drops.add(is);
		drops.add(is2);
		drops.add(is3);
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
			float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite())
				.withProperty(WORKING, (meta & 0b0100) == 0b0100).withProperty(EMPTY, (meta & 0b1000) == 0b1000);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, WORKING, EMPTY);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 0b0011);
	    boolean burning = (meta & 0b0100) == 0b0100;
	    boolean isEmtpy = (meta & 0b1000) == 0b1000;
	    if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
	        enumfacing = EnumFacing.NORTH;
	    }
	    return getDefaultState().withProperty(FACING, enumfacing)
	    		.withProperty(WORKING, burning).withProperty(EMPTY, isEmtpy);
	}
	
	/** 
	 * @return 返回一个int值，其中后两位存储方向数据<br>
	 * 			第二位存储是否正在工作，0表示没有工作<br>
	 * 			第一位存储内部是否有物品，1表示内部为空
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex()
				       | (state.getValue(WORKING) ? 0b0100 : 0b0000)
				       | (state.getValue(EMPTY) ? 0b1000 : 0b0000);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EUCompressor();
	}
	
}
