package xyz.emptydreams.mi.blocks.common;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.blocks.base.BlockBase;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public class OreBlock extends BlockBase {

	/** 存储方块列表 */
	public static final Map<OreBlock, Item> LIST = new HashMap<>(10);
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

	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
}
