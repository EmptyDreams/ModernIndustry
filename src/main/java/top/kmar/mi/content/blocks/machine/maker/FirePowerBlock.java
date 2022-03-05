package top.kmar.mi.content.blocks.machine.maker;

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
import top.kmar.mi.api.utils.properties.MIProperty;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.gui.FirePowerFrame;
import top.kmar.mi.api.register.block.AutoBlockRegister;
import top.kmar.mi.content.blocks.base.MachineBlock;
import top.kmar.mi.content.blocks.common.CommonBlocks;
import top.kmar.mi.content.tileentity.maker.EMFirePower;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 火力发电机
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "fire_power", field = "INSTANCE")
public class FirePowerBlock extends MachineBlock {
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static FirePowerBlock INSTANCE;
	private final Item ITEM;
	
	public static FirePowerBlock instance() {
		return INSTANCE;
	}
	
	public FirePowerBlock() {
		super(Material.IRON);
		setHarvestLevel(CommonBlocks.TC_PICKAXE, 2);
		setDefaultState(blockState.getBaseState()
				                .withProperty(MIProperty.getHORIZONTAL(), EnumFacing.NORTH)
				.withProperty(MIProperty.getWORKING(), false));
		ITEM = new ItemBlock(this).setRegistryName(ModernIndustry.MODID, "fire_power");
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
	                                EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, FirePowerFrame.NAME, worldIn, pos);
	}
	
	@Nullable
	@Override
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		EMFirePower power = (EMFirePower) world.getTileEntity(pos);
		//noinspection ConstantConditions
		return Lists.newArrayList(power.getInSlot().getStack(), power.getOutSlot().getStack());
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return CommonUtil.createBlockState(this);
	}
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return CommonUtil.getStateFromMeta(this, meta);
	}
	
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return CommonUtil.getMetaFromState(state);
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EMFirePower();
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
}