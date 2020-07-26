package xyz.emptydreams.mi.blocks.machine.user;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.te.user.EUCompressor;
import xyz.emptydreams.mi.gui.CompressorFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static xyz.emptydreams.mi.blocks.base.MIProperty.*;

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
		ITEM = new ItemBlock(this).setRegistryName(ModernIndustry.MODID, NAME);
		
		setDefaultState(blockState.getBaseState().withProperty(
				FACING, EnumFacing.EAST).withProperty(WORKING, false).withProperty(EMPTY, false));
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(ModernIndustry.instance,
					CompressorFrame.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Nullable
	@Override
	public List<ItemStack> getItemDrops(World world, BlockPos pos) {
		EUCompressor nbt = (EUCompressor) world.getTileEntity(pos);
		ItemStack is = nbt.getSlot(0).getStack();
		ItemStack is2 = nbt.getSlot(1).getStack();
		ItemStack is3 = nbt.getSlot(2).getStack();
		return Lists.newArrayList(is, is2, is3);
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, WORKING, EMPTY);
	}
	
	@Override
	public int quantityDropped(@Nonnull Random random) {
		return 0;
	}
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 0b0011);
	    boolean burning = (meta & 0b0100) == 0b0100;
	    boolean isEmpty = (meta & 0b1000) == 0b1000;
	    if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
	        enumfacing = EnumFacing.NORTH;
	    }
	    return getDefaultState().withProperty(FACING, enumfacing)
	    		.withProperty(WORKING, burning).withProperty(EMPTY, isEmpty);
	}
	
	/** 
	 * @return 返回一个int值，其中后两位存储方向数据<br>
	 * 			第二位存储是否正在工作，0表示没有工作<br>
	 * 			第一位存储内部是否有物品，1表示内部为空
	 */
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex()
				       | (state.getValue(WORKING) ? 0b0100 : 0b0000)
				       | (state.getValue(EMPTY) ? 0b1000 : 0b0000);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EUCompressor();
	}
	
}
