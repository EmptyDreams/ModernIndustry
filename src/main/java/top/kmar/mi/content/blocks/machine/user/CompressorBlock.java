package top.kmar.mi.content.blocks.machine.user;

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
import top.kmar.mi.api.utils.properties.MIProperty;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.gui.CompressorFrame;
import top.kmar.mi.api.register.block.AutoBlockRegister;
import top.kmar.mi.content.blocks.base.MachineBlock;
import top.kmar.mi.content.tileentity.user.EUCompressor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static top.kmar.mi.api.utils.properties.MIProperty.*;

/**
 * 压缩机
 * @author EmptyDremas
 */
@AutoBlockRegister(registryName = CompressorBlock.NAME, field = "INSTANCE")
public class CompressorBlock extends MachineBlock {
	
	/** 方块内部名称 */
	public static final String NAME = "compressor";
	@SuppressWarnings("unused")
	private static CompressorBlock INSTANCE;
	
	private final Item ITEM = new ItemBlock(this);
	
	public CompressorBlock() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(
				MIProperty.getHORIZONTAL(), EnumFacing.EAST)
				.withProperty(getWORKING(), false)
				.withProperty(getEMPTY(), true));
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, CompressorFrame.NAME, worldIn, pos);
	}
	
	@SuppressWarnings("ConstantConditions")
	@Nullable
	@Override
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		EUCompressor nbt = (EUCompressor) world.getTileEntity(pos);
		ItemStack is = nbt.getSlot(0).getStack();
		ItemStack is2 = nbt.getSlot(1).getStack();
		ItemStack is3 = nbt.getSlot(2).getStack();
		return Lists.newArrayList(is, is2, is3);
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getHORIZONTAL(), getWORKING(), getEMPTY());
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
	    return getDefaultState().withProperty(getHORIZONTAL(), enumfacing)
	    		.withProperty(getWORKING(), burning)
			    .withProperty(getEMPTY(), isEmpty);
	}
	
	/** 
	 * @return 返回一个int值，其中后两位存储方向数据<br>
	 * 			第二位存储是否正在工作，0表示没有工作<br>
	 * 			第一位存储内部是否有物品，1表示内部为空
	 */
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(getHORIZONTAL()).getHorizontalIndex()
				       | (state.getValue(getWORKING()) ? 0b0100 : 0b0000)
				       | (state.getValue(getEMPTY()) ? 0b1000 : 0b0000);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EUCompressor();
	}
	
	public static CompressorBlock instance() {
		return INSTANCE;
	}
	
}