package xyz.emptydreams.mi.blocks.machine.user;

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
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.te.user.EUFurnace;
import xyz.emptydreams.mi.gui.EleFurnaceFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.NORTH;
import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 电炉的Block
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "ele_furnace")
public class EleFurnaceBlock extends MachineBlock {

	private final Item ITEM = new ItemBlock(this);

	public EleFurnaceBlock() {
		super(Material.IRON);
		setCreativeTab(ModernIndustry.TAB_BLOCK);
		setDefaultState(blockState.getBaseState()
				.withProperty(FACING, NORTH)
				.withProperty(WORKING, false));
		setHardness(3.5F);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ) &&
				CommonUtil.openGui(playerIn, EleFurnaceFrame.ID, worldIn, pos);
	}

	@Nullable
	@Override
	public NonNullList<ItemStack> getItemDrops(World world, BlockPos pos) {
		EUFurnace furnace = (EUFurnace) world.getTileEntity(pos);
		return NonNullList.from(furnace.getInSlot().getStack(),
				furnace.getOutSlot().getStack());
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EUFurnace();
	}

	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return CommonUtil.createWorkState(this);
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return CommonUtil.getMetaFromState(state);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		//return CommonUtil.getStateFromMeta(this, meta);
		EnumFacing facing = EnumFacing.getFront(meta & 0b0011);
		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		return getDefaultState()
				.withProperty(FACING, facing)
				.withProperty(WORKING, (meta & 0b0100) == 0b0100);
	}
}
