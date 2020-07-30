package xyz.emptydreams.mi.items.tools;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyCapability;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyProvider;
import xyz.emptydreams.mi.api.tools.item.IToolMaterial;
import xyz.emptydreams.mi.api.tools.item.MIArmor;
import xyz.emptydreams.mi.api.tools.item.MIAxe;
import xyz.emptydreams.mi.api.tools.item.MIHoe;
import xyz.emptydreams.mi.api.tools.item.MIPickaxe;
import xyz.emptydreams.mi.api.tools.item.MISpade;
import xyz.emptydreams.mi.api.tools.item.MISword;
import xyz.emptydreams.mi.api.tools.property.IProperty;
import xyz.emptydreams.mi.api.tools.property.PoorQuality;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;
import xyz.emptydreams.mi.register.AutoManager;
import xyz.emptydreams.mi.register.item.RecipeRegister;

import java.util.LinkedList;
import java.util.List;

import static net.minecraft.inventory.EntityEquipmentSlot.*;
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
@Mod.EventBusSubscriber
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
	
	@SubscribeEvent
	public static void onPlayerCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting.hasCapability(PropertyCapability.PROPERTY, null)) {
			if (event.player.world.isRemote) {
				event.crafting.getCapability(PropertyCapability.PROPERTY, null)
						.addProperty(new PoorQuality().setLevel(-1));
			} else {
				event.crafting.getCapability(PropertyCapability.PROPERTY, null)
						.addProperty(PoorQuality.randProperty(1, 10));
			}
		}
	}
	
	@SubscribeEvent
	public static void addToolCap(AttachCapabilitiesEvent<ItemStack> event) {
		if (!event.getObject().hasCapability(PropertyCapability.PROPERTY, null) &&
				    (event.getObject().getItem() instanceof ItemTool ||
				     event.getObject().getItem() instanceof ItemArmor)){
			event.addCapability(new ResourceLocation(MODID, PropertyCapability.PROPERTY.getName()),
					new PropertyProvider(PropertyCapability.PROPERTY.getDefaultInstance()));
		}
	}
	
	@SubscribeEvent
	public static void addTooltip(ItemTooltipEvent event) {
		if (event.getItemStack() == null) return;
		PropertyManager manager = event.getItemStack().getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		List<String> list = new LinkedList<>();
		for (IProperty property : manager) {
			list.add(I18n.format(property.getName()) + ": " + property.getValue());
		}
		event.getToolTip().addAll(1, list);
	}
	
}
