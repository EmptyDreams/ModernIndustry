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
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.tileentity.user.EUPulverizer;
import xyz.emptydreams.mi.gui.PulverizerFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.EnumFacing.NORTH;
import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 粉碎机的Block
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "pulverizer")
public class PulverizerBlock extends MachineBlock {

	private final Item ITEM = new ItemBlock(this);

	public PulverizerBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState()
									.withProperty(FACING, NORTH)
									.withProperty(WORKING, false));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, PulverizerFrame.ID, worldIn, pos);
	}

	@Nullable
	@Override
	public List<ItemStack> getItemDrops(World world, BlockPos pos) {
		EUPulverizer pulverizer = (EUPulverizer) world.getTileEntity(pos);
		return Lists.newArrayList(pulverizer.getInSlot().getStack(), pulverizer.getOutSlot().getStack());
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
		return new EUPulverizer();
	}

	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
}
