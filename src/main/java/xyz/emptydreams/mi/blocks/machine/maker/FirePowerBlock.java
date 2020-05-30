package xyz.emptydreams.mi.blocks.machine.maker;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.src.block.MachineBlock;
import xyz.emptydreams.mi.blocks.common.CommonBlocks;
import xyz.emptydreams.mi.blocks.te.maker.EMFirePower;
import xyz.emptydreams.mi.gui.FirePowerFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;
import xyz.emptydreams.mi.utils.BlockPosUtil;

import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 火力发电机
 * @author EmptyDreams
 * @version V1.0
 */
@AutoBlockRegister(registryName = "fire_power")
public class FirePowerBlock extends MachineBlock {
	
	private final Item ITEM;
	
	public FirePowerBlock() {
		super(Material.IRON);
		setCreativeTab(ModernIndustry.TAB_BLOCK);
		setHardness(5);
		setHarvestLevel(CommonBlocks.TC_PICKAXE, 2);
		setResistance(20);
		setDefaultState(blockState.getBaseState()
				                .withProperty(FACING, EnumFacing.NORTH).withProperty(WORKING, false));
		ITEM = new ItemBlock(this).setRegistryName(ModernIndustry.MODID, "fire_power");
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
	                            EntityLivingBase placer, ItemStack stack) {
		state = state.withProperty(FACING, BlockPosUtil.upsideDown(placer.getHorizontalFacing()));
		worldIn.setBlockState(pos, state);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
	                                EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)) return false;
		if (!worldIn.isRemote) {
			playerIn.openGui(ModernIndustry.instance,
					FirePowerFrame.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Nullable
	@Override
	public NonNullList<ItemStack> getItemDrops(World world, BlockPos pos) {
		EMFirePower power = (EMFirePower) world.getTileEntity(pos);
		return NonNullList.from(power.getInSlot().getStack(), power.getOutSlot().getStack());
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, WORKING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 0b0011);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return getDefaultState().withProperty(FACING, enumfacing)
				       .withProperty(WORKING, (meta & 0b0100) == 0 );
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() |
				       (state.getValue(WORKING) ? 0b0100 : 0b0000);
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EMFirePower();
	}
	
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
}
