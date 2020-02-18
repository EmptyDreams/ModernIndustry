package minedreams.mi.blocks.machine.maker;

import javax.annotation.Nullable;
import java.util.Random;

import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.info.LinkInfo;
import minedreams.mi.register.block.BlockAutoRegister;
import minedreams.mi.blocks.register.BlockBaseT;
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
@BlockAutoRegister(name = 10, registryName = "perpetual")
public class PerpetualBlock extends BlockBaseT implements IEleInfo {
	
	private final Item ITEM = new ItemBlock(this).setRegistryName("perpetual");
	
	public PerpetualBlock() {
		super(Material.IRON);
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
