package xyz.emptydreams.mi.blocks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.register.AutoManager;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoManager(block = true, item = false)
public final class CommonBlocks {

	/** 稿子 */
	public static final String TC_PICKAXE = "pickaxe";
	
	/** 机器外壳 */
	public static final String NAME_MACHINE_SHELL = "machine_shell";
	public static final Block MACHINE_SHELL = new BlockHelper(Material.IRON, TC_PICKAXE, 1)
			                                          .setRegistryName(NAME_MACHINE_SHELL)
			                                          .setUnlocalizedName(NAME_MACHINE_SHELL)
				                                      .setCreativeTab(ModernIndustry.TAB_BLOCK)
				                                      .setHardness(2.5F)
					                                  .setResistance(15);
	
	private static final class BlockHelper extends Block {
		
		BlockHelper(Material material, String toolClass, int level) {
			super(material);
			setHarvestLevel(toolClass, level);
		}
		
	}
	
}
