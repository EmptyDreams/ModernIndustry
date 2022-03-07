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
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.data.info.MIProperty;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.gui.EleFurnaceFrame;
import top.kmar.mi.api.register.block.AutoBlockRegister;
import top.kmar.mi.content.blocks.base.MachineBlock;
import top.kmar.mi.content.tileentity.user.EUFurnace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.EnumFacing.NORTH;

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
				.withProperty(MIProperty.getHORIZONTAL(), NORTH)
				.withProperty(MIProperty.getWORKING(), false));
		setHardness(3.5F);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, EleFurnaceFrame.NAME, worldIn, pos);
	}

	@Nullable
	@Override
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		EUFurnace furnace = (EUFurnace) world.getTileEntity(pos);
		//noinspection ConstantConditions
		return Lists.newArrayList(furnace.getInSlot().getStack(), furnace.getOutSlot().getStack());
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
}