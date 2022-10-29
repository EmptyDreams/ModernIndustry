package top.kmar.mi.content.blocks.machine.user;

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
import top.kmar.mi.content.tileentity.user.EUElectronSynthesizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static top.kmar.mi.data.properties.MIProperty.getWORKING;

/**
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "electron_synthesizer", field = "INSTANCE")
public class ElectronSynthesizerBlock extends MachineBlock {
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static ElectronSynthesizerBlock INSTANCE;
	private final Item ITEM = new ItemBlock(this);
	
	public ElectronSynthesizerBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(getWORKING(), false));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, BlockGuiList.getSynthesizer(), pos);
	}
	
	@Nullable
	@Override
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		EUElectronSynthesizer synthesizer = (EUElectronSynthesizer) world.getTileEntity(pos);
		//noinspection ConstantConditions
		return synthesizer.getAllStacks();
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getWORKING());
	}
	
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(getWORKING()) ? 1 : 0;
	}
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(getWORKING(), meta == 1);
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EUElectronSynthesizer();
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	public static ElectronSynthesizerBlock instance() {
		return INSTANCE;
	}
	
}