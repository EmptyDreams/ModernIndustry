package minedreams.mi.blocks.machine.maker;

import javax.annotation.Nullable;
import java.util.Random;

import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.src.block.MachineBlock;
import minedreams.mi.api.electricity.src.info.IEleInfo;
import minedreams.mi.api.electricity.src.info.LinkInfo;
import minedreams.mi.register.block.AutoBlockRegister;
import minedreams.mi.blocks.te.maker.EMPerpetual;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoBlockRegister(registryName = "perpetual")
public class PerpetualBlock extends MachineBlock implements IEleInfo {
	
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
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EMPerpetual();
	}
	
	@Override
	public boolean canLink(LinkInfo info, boolean nowIsExist, boolean fromIsExist) {
		return true;
	}
}
