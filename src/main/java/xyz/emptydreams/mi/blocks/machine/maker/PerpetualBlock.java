package xyz.emptydreams.mi.blocks.machine.maker;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.te.maker.EMPerpetual;
import xyz.emptydreams.mi.register.block.AutoBlockRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoBlockRegister(registryName = "perpetual")
public class PerpetualBlock extends MachineBlock {
	
	private final Item ITEM = new ItemBlock(this).setRegistryName("perpetual");
	
	public PerpetualBlock() {
		super(Material.IRON);
		setHarvestLevel("pickaxe", 1);
		setHardness(3.5F);
		setCreativeTab(ModernIndustry.TAB_BLOCK);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EMPerpetual();
	}
	
}
