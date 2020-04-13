package xyz.emptydreams.mi.items.tools;

import static net.minecraft.inventory.EntityEquipmentSlot.HEAD;
import static net.minecraft.inventory.EntityEquipmentSlot.FEET;
import static net.minecraft.inventory.EntityEquipmentSlot.CHEST;
import static net.minecraft.inventory.EntityEquipmentSlot.LEGS;

import xyz.emptydreams.mi.register.item.ItemRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public class ToolRegister {
	
	/** 铜斧 */
	public static final String NAME_COPPER_AXE = "copper_axe_tool";
	public static final Item ITEM_COPPER_AXE = new MI_Tool.MI_Axe(MI_Tool.COPPER, 8, -3.1F).setRegistry(NAME_COPPER_AXE);
	/** 铜镐 */
	public static final String NAME_COPPER_PICKAXE = "copper_pickaxe_tool";
	public static final Item ITEM_COPPER_PICKAXE = new MI_Tool.MI_Pickaxe(MI_Tool.COPPER).setRegistry(NAME_COPPER_PICKAXE);
	/** 铜剑 */
	public static final String NAME_COPPER_SWORD = "copper_sword_tool";
	public static final Item ITEM_COPPER_SWORD = new MI_Tool.MI_Sword(MI_Tool.COPPER, 5, -2.5F).setRegistry(NAME_COPPER_SWORD);
	/** 铜铲 */
	public static final String NAME_COPPER_SHOVEL = "copper_shovel_tool";
	public static final Item ITEM_COPPER_SHOVEL = new MI_Tool.MI_Shovel(MI_Tool.COPPER).setRegistry(NAME_COPPER_SHOVEL);
	/** 铜锄 */
	public static final String NAME_COPPER_HOE = "copper_hoe_tool";
	public static final Item ITEM_COPPER_HOE = new MI_Tool.MI_Hoe(MI_Tool.COPPER).setRegistry(NAME_COPPER_HOE);
	/** 铜头盔 */
	public static final String NAME_COPPER_HEAD = "copper_head";
	public static final Item ITEM_COPPER_HEAD = new ItemArmor(MI_Tool.COPPER_ARMOR, MI_Tool.COPPER_ARMOR.ordinal(), HEAD)
											.setRegistryName(ModernIndustry.MODID, NAME_COPPER_HEAD)
											.setUnlocalizedName(NAME_COPPER_HEAD)
											.setCreativeTab(ModernIndustry.TAB_TOOL);
	/** 铜胸甲 */
	public static final String NAME_COPPER_CHEST = "copper_chest";
	public static final Item ITEM_COPPER_CHEST = new ItemArmor(MI_Tool.COPPER_ARMOR, MI_Tool.COPPER_ARMOR.ordinal(), CHEST)
					.setRegistryName(ModernIndustry.MODID, NAME_COPPER_CHEST)
					.setUnlocalizedName(NAME_COPPER_CHEST)
					.setCreativeTab(ModernIndustry.TAB_TOOL);
	/** 铜护腿 */
	public static final String NAME_COPPER_LEG = "copper_leg";
	public static final Item ITEM_COPPER_LEG = new ItemArmor(MI_Tool.COPPER_ARMOR, MI_Tool.COPPER_ARMOR.ordinal(), LEGS)
					.setRegistryName(ModernIndustry.MODID, NAME_COPPER_LEG)
					.setUnlocalizedName(NAME_COPPER_LEG)
					.setCreativeTab(ModernIndustry.TAB_TOOL);
	/** 铜靴子 */
	public static final String NAME_COPPER_BOOT = "copper_boot";
	public static final Item ITEM_COPPER_BOOT = new ItemArmor(MI_Tool.COPPER_ARMOR, MI_Tool.COPPER_ARMOR.ordinal(), FEET)
					.setRegistryName(ModernIndustry.MODID, NAME_COPPER_BOOT)
					.setUnlocalizedName(NAME_COPPER_BOOT)
					.setCreativeTab(ModernIndustry.TAB_TOOL);
	
	public static void registerRecipe() {
		//铜镐
		ItemRegister.registerRecipe(ITEM_COPPER_PICKAXE,
				new Object[] {
					"###", " * ", " * ",
					'#', ItemRegister.ITEM_COPPER,
					'*', Items.STICK
				}
		);
		//铜斧
		ItemRegister.registerRecipe(ITEM_COPPER_AXE,
				new Object[] {
					"## ", "#* ", " * ",
					'#', ItemRegister.ITEM_COPPER,
					'*', Items.STICK
				}
		);
		//铜剑
		ItemRegister.registerRecipe(ITEM_COPPER_SWORD,
				new Object[] {
					" # ", " # ", " * ",
					'#', ItemRegister.ITEM_COPPER,
					'*', Items.STICK
				}
		);
		//铜铲
		ItemRegister.registerRecipe(ITEM_COPPER_SHOVEL,
				new Object[] {
					" * ", " # ", " # ",
					'#', ItemRegister.ITEM_COPPER,
					'*', Items.STICK
				}
		);
		//铜锄
		ItemRegister.registerRecipe(ITEM_COPPER_HOE,
				new Object[] {
					"## ", " * ", " * ",
					'#', ItemRegister.ITEM_COPPER,
					'*', Items.STICK
				}
		);
		//铜头盔
		ItemRegister.registerRecipe(ITEM_COPPER_HEAD,
				new Object[] {
					"###", "# #", "   ",
					'#', ItemRegister.ITEM_COPPER
				}
		);
		ItemRegister.registerRecipe(ITEM_COPPER_HEAD,
				new Object[] {
					"   ", "###", "# #",
					'#', ItemRegister.ITEM_COPPER
				}
		);
		//铜盔甲
		ItemRegister.registerRecipe(ITEM_COPPER_CHEST,
				new Object[] {
					"# #", "###", "###",
					'#', ItemRegister.ITEM_COPPER
				}
		);
		//铜护腿
		ItemRegister.registerRecipe(ITEM_COPPER_LEG,
				new Object[] {
					"###", "# #", "# #",
					'#', ItemRegister.ITEM_COPPER
				}
		);
		//铜靴子
		ItemRegister.registerRecipe(ITEM_COPPER_BOOT,
				new Object[] {
					"   ", "# #", "# #",
					'#', ItemRegister.ITEM_COPPER
				}
		);
		ItemRegister.registerRecipe(ITEM_COPPER_BOOT,
				new Object[] {
					"# #", "# #", "   ",
					'#', ItemRegister.ITEM_COPPER
				}
		);
	}
	
}
