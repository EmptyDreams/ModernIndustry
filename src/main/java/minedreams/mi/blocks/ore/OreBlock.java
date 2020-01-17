package minedreams.mi.blocks.ore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import minedreams.mi.ModernIndustry;
import minedreams.mi.blocks.register.BlockBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public class OreBlock extends BlockBase {

	public static final Map<OreBlock, Item> LIST = new HashMap<>(2);
	
	public static final String NAME_COPPER = "copper_ore_block";
	public static final String NAME_TIN = "tin_ore_block";
	
	private final Item ITEM;
	
	public OreBlock(String name, Item output) {
		super(Material.ROCK);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(name);
		setCreativeTab(ModernIndustry.TAB_BLOCK);
		setSoundType(SoundType.STONE);
		setHardness(2.5F);
		setHarvestLevel("pickaxe", 1);
		ITEM = new ItemBlock(this).setRegistryName(name);
		LIST.put(this, output);
	}
	
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
}
