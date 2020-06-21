package xyz.emptydreams.mi.blocks.machine.user;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.SoundType;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.blocks.base.TEBlockBase;
import xyz.emptydreams.mi.blocks.te.user.MuffleFurnace;
import xyz.emptydreams.mi.gui.MuffleFuranceFrame;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;

import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings({"deprecation", "unused"})
@AutoBlockRegister(registryName = "muffle_furnace")
public class MuffleFurnaceBlock extends TEBlockBase {
	
	private final Item ITEM = new ItemBlock(this).setRegistryName(ModernIndustry.MODID, "muffle_furnace");
	
	public MuffleFurnaceBlock() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState()
				                .withProperty(FACING, EnumFacing.NORTH)
				                .withProperty(WORKING, false));
		setCreativeTab(ModernIndustry.TAB_BLOCK);
		setSoundType(SoundType.STONE);
		
	}
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		//防止折叠
		return new ItemStack(this);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(ModernIndustry.instance,
					MuffleFuranceFrame.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta & 0b0011);
		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		return getDefaultState()
				       .withProperty(FACING, facing)
				       .withProperty(WORKING, (meta & 0b0100) == 0b0100);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
	                                        float hitX, float hitY, float hitZ, int meta,
	                                        EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(WORKING) ? 0b0100 : 0);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		//阻止折叠
		return new BlockStateContainer(this, FACING, WORKING);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MuffleFurnace();
	}
	
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
}
