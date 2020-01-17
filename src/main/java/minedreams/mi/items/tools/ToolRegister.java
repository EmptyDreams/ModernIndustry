package minedreams.mi.items.tools;

import static net.minecraft.inventory.EntityEquipmentSlot.HEAD;
import static net.minecraft.inventory.EntityEquipmentSlot.FEET;
import static minedreams.mi.ModernIndustry.MODID;
import static minedreams.mi.ModernIndustry.TAB_TOOL;
import static minedreams.mi.items.tools.MI_Tool.COPPER;
import static minedreams.mi.items.tools.MI_Tool.COPPER_ARMOR;
import static net.minecraft.inventory.EntityEquipmentSlot.CHEST;
import static net.minecraft.inventory.EntityEquipmentSlot.LEGS;

import minedreams.mi.items.register.ItemRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public class ToolRegister {
	
	/** 铜斧 */
	public static final String NAME_COPPER_AXE = "copper_axe_tool";
	public static final Item ITEM_COPPER_AXE = new MI_Tool.MI_Axe(COPPER, 8, -3.1F).setRegistry(NAME_COPPER_AXE);
	/** 铜镐 */
	public static final String NAME_COPPER_PICKAXE = "copper_pickaxe_tool";
	public static final Item ITEM_COPPER_PICKAXE = new MI_Tool.MI_Pickaxe(COPPER).setRegistry(NAME_COPPER_PICKAXE);
	/** 铜剑 */
	public static final String NAME_COPPER_SWORD = "copper_sword_tool";
	public static final Item ITEM_COPPER_SWORD = new MI_Tool.MI_Sword(COPPER, 5, -2.5F).setRegistry(NAME_COPPER_SWORD);
	/** 铜铲 */
	public static final String NAME_COPPER_SHOVEL = "copper_shovel_tool";
	public static final Item ITEM_COPPER_SHOVEL = new MI_Tool.MI_Shovel(COPPER).setRegistry(NAME_COPPER_SHOVEL);
	/** 铜锄 */
	public static final String NAME_COPPER_HOE = "copper_hoe_tool";
	public static final Item ITEM_COPPER_HOE = new MI_Tool.MI_Hoe(COPPER).setRegistry(NAME_COPPER_HOE);
	/** 铜头盔 */
	public static final String NAME_COPPER_HEAD = "copper_head";
	public static final Item ITEM_COPPER_HEAD = new ItemArmor(COPPER_ARMOR, COPPER_ARMOR.ordinal(), HEAD)
											.setRegistryName(MODID, NAME_COPPER_HEAD)
											.setUnlocalizedName(NAME_COPPER_HEAD)
											.setCreativeTab(TAB_TOOL);
	/** 铜胸甲 */
	public static final String NAME_COPPER_CHEST = "copper_chest";
	public static final Item ITEM_COPPER_CHEST = new ItemArmor(COPPER_ARMOR, COPPER_ARMOR.ordinal(), CHEST)
					.setRegistryName(MODID, NAME_COPPER_CHEST)
					.setUnlocalizedName(NAME_COPPER_CHEST)
					.setCreativeTab(TAB_TOOL);
	/** 铜护腿 */
	public static final String NAME_COPPER_LEG = "copper_leg";
	public static final Item ITEM_COPPER_LEG = new ItemArmor(COPPER_ARMOR, COPPER_ARMOR.ordinal(), LEGS)
					.setRegistryName(MODID, NAME_COPPER_LEG)
					.setUnlocalizedName(NAME_COPPER_LEG)
					.setCreativeTab(TAB_TOOL);
	/** 铜靴子 */
	public static final String NAME_COPPER_BOOT = "copper_boot";
	public static final Item ITEM_COPPER_BOOT = new ItemArmor(COPPER_ARMOR, COPPER_ARMOR.ordinal(), FEET)
					.setRegistryName(MODID, NAME_COPPER_BOOT)
					.setUnlocalizedName(NAME_COPPER_BOOT)
					.setCreativeTab(TAB_TOOL);
	
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
