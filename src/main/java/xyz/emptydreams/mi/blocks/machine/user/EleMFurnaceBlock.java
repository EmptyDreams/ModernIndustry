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
import xyz.emptydreams.mi.api.register.block.AutoBlockRegister;
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.tileentity.user.EUMFurnace;
import xyz.emptydreams.mi.gui.EleMFurnaceFrame;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.EnumFacing.NORTH;
import static xyz.emptydreams.mi.blocks.base.MIProperty.HORIZONTAL;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 高温火炉的Block
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "ele_mfurnace")
public class EleMFurnaceBlock extends MachineBlock {

	private final Item ITEM = new ItemBlock(this);

	public EleMFurnaceBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState()
				.withProperty(HORIZONTAL, NORTH)
				.withProperty(WORKING, false));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, EleMFurnaceFrame.NAME, worldIn, pos);
	}

	@Nullable
	@Override
	public List<ItemStack> getItemDrops(World world, BlockPos pos) {
		EUMFurnace furnace = (EUMFurnace) world.getTileEntity(pos);
		//noinspection ConstantConditions
		return Lists.newArrayList(furnace.getInSlot().getStack(), furnace.getOutSlot().getStack());
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HORIZONTAL, WORKING);
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
		return new EUMFurnace();
	}

	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}

}