package xyz.emptydreams.mi.items.tools;

import net.minecraft.item.Item;
import xyz.emptydreams.mi.api.register.AutoManager;
import xyz.emptydreams.mi.api.register.item.RecipeRegister;
import xyz.emptydreams.mi.api.tools.item.IToolMaterial;
import xyz.emptydreams.mi.api.tools.item.MIArmor;
import xyz.emptydreams.mi.api.tools.item.MIAxe;
import xyz.emptydreams.mi.api.tools.item.MIHoe;
import xyz.emptydreams.mi.api.tools.item.MIPickaxe;
import xyz.emptydreams.mi.api.tools.item.MISpade;
import xyz.emptydreams.mi.api.tools.item.MISword;

import static net.minecraft.inventory.EntityEquipmentSlot.CHEST;
import static net.minecraft.inventory.EntityEquipmentSlot.FEET;
import static net.minecraft.inventory.EntityEquipmentSlot.HEAD;
import static net.minecraft.inventory.EntityEquipmentSlot.LEGS;
import static net.minecraft.item.Item.ToolMaterial.IRON;
import static net.minecraft.item.ItemArmor.ArmorMaterial;
import static xyz.emptydreams.mi.ModernIndustry.MODID;
import static xyz.emptydreams.mi.items.tools.MITool.COPPER;
import static xyz.emptydreams.mi.items.tools.MITool.COPPER_ARMOR;

/**
 * 工具
 * @author EmptyDremas
 */
@SuppressWarnings("unused")
@AutoManager(item = true, itemCustom = true)
public class ToolRegister {
	
	/** 铜斧 */
	public static final String NAME_COPPER_AXE = "copper_axe";
	public static final Item ITEM_COPPER_AXE = new MIAxe(COPPER, 8, -3.1F)
			                                           .setRegistry(MODID, NAME_COPPER_AXE);
	/** 青铜斧 */
	public static final String NAME_BRONZE_AXE = "bronze_axe";
	public static final Item ITEM_BRONZE_AXE = new MIAxe(IRON, 9, -3.0F)
														.setRegistry(MODID, NAME_BRONZE_AXE);
	
	/** 铜镐 */
	public static final String NAME_COPPER_PICKAXE = "copper_pickaxe";
	public static final Item ITEM_COPPER_PICKAXE = new MIPickaxe(COPPER)
														.setRegistry(MODID, NAME_COPPER_PICKAXE);
	/** 青铜镐 */
	public static final String NAME_BRONZE_PICKAXE = "bronze_pickaxe";
	public static final Item ITEM_BRONZE_PICKAXE = new MIPickaxe(IRON)
														.setRegistry(MODID, NAME_BRONZE_PICKAXE);
			
	/** 铜剑 */
	public static final String NAME_COPPER_SWORD = "copper_sword";
	public static final Item ITEM_COPPER_SWORD = new MISword(COPPER, 5, -2.5D)
			                                             .setRegistry(MODID, NAME_COPPER_SWORD);
	public static final String NAME_BRONZE_SWORD = "bronze_sword";
	public static final Item ITEM_BRONZE_SWORD = new MISword(IRON, 6, -2.4000000953674316D)
														.setRegistry(MODID, NAME_BRONZE_SWORD);
	
	/** 铜铲 */
	public static final String NAME_COPPER_SHOVEL = "copper_shovel";
	public static final Item ITEM_COPPER_SHOVEL = new MISpade(COPPER).setRegistry(MODID, NAME_COPPER_SHOVEL);
	/** 青铜铲 */
	public static final String NAME_BRONZE_SHOVEL = "bronze_shovel";
	public static final Item ITEM_BRONZE_SHOVEL = new MISpade(IRON).setRegistry(MODID, NAME_BRONZE_SHOVEL);
	
	/** 铜锄 */
	public static final String NAME_COPPER_HOE = "copper_hoe";
	public static final Item ITEM_COPPER_HOE = new MIHoe(COPPER).setRegistry(MODID, NAME_COPPER_HOE);
	/** 青铜锄 */
	public static final String NAME_BRONZE_HOE = "bronze_hoe";
	public static final Item ITEM_BRONZE_HOE = new MIHoe(IRON).setRegistry(MODID, NAME_BRONZE_HOE);
	
	/** 铜头盔 */
	public static final String NAME_COPPER_HELMET = "copper_helmet";
	public static final Item ITEM_COPPER_HELMET = new MIArmor(COPPER_ARMOR, HEAD)
														.setRegistry(MODID, NAME_COPPER_HELMET);
	/** 青铜头盔 */
	public static final String NAME_BRONZE_HELMET = "bronze_helmet";
	public static final Item ITEM_BRONZE_HELMET = new MIArmor(ArmorMaterial.IRON, HEAD)
														.setRegistry(MODID, NAME_BRONZE_HELMET);
	
	/** 铜胸甲 */
	public static final String NAME_COPPER_CHEST = "copper_chestplate";
	public static final Item ITEM_COPPER_CHEST = new MIArmor(COPPER_ARMOR, CHEST)
														.setRegistry(MODID, NAME_COPPER_CHEST);
	/** 青铜胸甲 */
	public static final String NAME_BRONZE_CHEST = "bronze_chestplate";
	public static final Item ITEM_BRONZE_CHEST = new MIArmor(ArmorMaterial.IRON, CHEST)
														.setRegistry(MODID, NAME_BRONZE_CHEST);
	
	/** 铜护腿 */
	public static final String NAME_COPPER_LEG = "copper_leggings";
	public static final Item ITEM_COPPER_LEG = new MIArmor(COPPER_ARMOR, LEGS)
														.setRegistry(MODID, NAME_COPPER_LEG);
	/** 青铜护腿 */
	public static final String NAME_BRONZE_LEG = "bronze_leggings";
	public static final Item ITEM_BRONZE_LEG = new MIArmor(ArmorMaterial.IRON, LEGS)
														.setRegistry(MODID, NAME_BRONZE_LEG);
	
	/** 铜靴子 */
	public static final String NAME_COPPER_BOOT = "copper_boots";
	public static final Item ITEM_COPPER_BOOT = new MIArmor(COPPER_ARMOR, FEET)
														.setRegistry(MODID, NAME_COPPER_BOOT);
	/** 青铜靴子 */
	public static final String NAME_BRONZE_BOOT = "bronze_boots";
	public static final Item ITEM_BRONZE_BOOT = new MIArmor(ArmorMaterial.IRON, FEET)
														.setRegistry(MODID, NAME_BRONZE_BOOT);

	public static void itemCustom(Item item) {
		if (item instanceof IToolMaterial)
			RecipeRegister.registry(item, ((IToolMaterial) item).getMaterial());
	}
	
}