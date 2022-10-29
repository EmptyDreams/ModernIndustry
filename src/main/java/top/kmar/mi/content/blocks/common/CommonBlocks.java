package top.kmar.mi.content.blocks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.regedits.OreDicRegister;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.regedits.others.AutoManager;
import top.kmar.mi.api.regedits.block.annotations.OreCreate;
import top.kmar.mi.content.items.common.CommonItems;

import static top.kmar.mi.api.utils.StringUtil.getUnlocalizedName;

/**
 * 普通方块
 * @author EmptyDreams
 */
@AutoManager(block = true)
public final class CommonBlocks {

	/** 稿子 */
	public static final String TC_PICKAXE = "pickaxe";

	/** 铜矿石 */
	@OreCreate(yRange = 76 - 16, count = 11, name = OreBlock.NAME_COPPER)
	public static final OreBlock ORE_COPPER = new OreBlock(OreBlock.NAME_COPPER, CommonItems.ITEM_COPPER);
	/** 锡矿石 */
	@OreCreate(yRange = 70 - 16, count = 7, time = 3, name = OreBlock.NAME_TIN)
	public static final OreBlock ORE_TIN = new OreBlock(OreBlock.NAME_TIN, CommonItems.ITEM_TIN);

	/** 机器外壳 */
	public static final String NAME_MACHINE_SHELL = "machine_shell";
	public static final Block MACHINE_SHELL = new BlockHelper(Material.IRON, TC_PICKAXE, 1)
							.setOreDic("machineBlock")
							.setRegistryName(ModernIndustry.MODID, NAME_MACHINE_SHELL)
							.setUnlocalizedName(StringUtil.getUnlocalizedName(NAME_MACHINE_SHELL))
							.setCreativeTab(ModernIndustry.TAB_BLOCK)
							.setHardness(2.5F)
							.setResistance(15);
	
	private static final class BlockHelper extends Block {
		
		BlockHelper(Material material, String toolClass, int level) {
			super(material);
			setHarvestLevel(toolClass, level);
		}
		
		public BlockHelper setOreDic(String... ores) {
			OreDicRegister.registry(this, ores);
			return this;
		}
		
	}
	
}