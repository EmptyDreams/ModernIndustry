package xyz.emptydreams.mi.blocks.machine.user;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.register.block.AutoBlockRegister;
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.tileentity.user.EUElectronSynthesizer;
import xyz.emptydreams.mi.gui.ElectronSynthesizerFrame;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

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
		setDefaultState(blockState.getBaseState().withProperty(WORKING, false));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		return CommonUtil.openGui(playerIn, ElectronSynthesizerFrame.NAME, worldIn, pos);
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, WORKING);
	}
	
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(WORKING) ? 1 : 0;
	}
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(WORKING, meta == 1);
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