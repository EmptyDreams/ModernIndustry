package xyz.emptydreams.mi.blocks.machine.maker;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.te.maker.EMRedStoneConverter;
import xyz.emptydreams.mi.gui.RedStoneConverterFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 红石能转换器
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "red_stone_converter")
public class RedStoneConverterBlock extends MachineBlock {

	private final Item ITEM = new ItemBlock(this);

	public RedStoneConverterBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState()
									.withProperty(FACING, EnumFacing.NORTH)
									.withProperty(WORKING, false));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		if (!super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)) return false;
		return CommonUtil.openGui(playerIn, RedStoneConverterFrame.ID,worldIn, pos);
	}

	@Nullable
	@Override
	public NonNullList<ItemStack> getItemDrops(World world, BlockPos pos) {
		EMRedStoneConverter converter = (EMRedStoneConverter) world.getTileEntity(pos);
		return NonNullList.from(converter.getInput().getStack());
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return CommonUtil.createBlockState(this);
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return CommonUtil.getMetaFromState(state);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return CommonUtil.getStateFromMeta(this, meta);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EMRedStoneConverter();
	}

	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}

}
