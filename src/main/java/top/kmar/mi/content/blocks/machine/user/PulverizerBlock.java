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
import top.kmar.mi.api.regedits.block.annotations.AutoBlockRegister;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.blocks.base.MachineBlock;
import top.kmar.mi.content.tileentity.user.EUPulverizer;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.EnumFacing.NORTH;

/**
 * 粉碎机的Block
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "pulverizer", field = "INSTANCE")
public class PulverizerBlock extends MachineBlock {
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static PulverizerBlock INSTANCE;
	
	private final Item ITEM = new ItemBlock(this);
	
	public PulverizerBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState()
									.withProperty(MIProperty.getHORIZONTAL(), NORTH)
									.withProperty(MIProperty.getWORKING(), false));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, BlockGuiList.getPulverizer(), pos);
	}

	@Nullable
	@Override
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		EUPulverizer pulverizer = (EUPulverizer) world.getTileEntity(pos);
		//noinspection ConstantConditions
		return Lists.newArrayList(pulverizer.getInputStack(), pulverizer.getOutputStack());
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

	public static PulverizerBlock instance() {
		return INSTANCE;
	}
	
}