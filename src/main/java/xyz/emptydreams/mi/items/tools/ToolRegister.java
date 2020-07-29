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
import xyz.emptydreams.mi.ModernIndustry;
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
import static xyz.emptydreams.mi.ModernIndustry.MODID;

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
	public static final Item ITEM_COPPER_AXE = new MIAxe(MITool.COPPER, 8, -3.1F)
			                                           .setRegistry(MODID, NAME_COPPER_AXE);
	/** 铜镐 */
	public static final String NAME_COPPER_PICKAXE = "copper_pickaxe";
	public static final Item ITEM_COPPER_PICKAXE = new MIPickaxe(MITool.COPPER)
														.setRegistry(MODID, NAME_COPPER_PICKAXE);
	/** 铜剑 */
	public static final String NAME_COPPER_SWORD = "copper_sword";
	public static final Item ITEM_COPPER_SWORD = new MISword(MITool.COPPER, 5, -2.5F)
			                                             .setRegistry(MODID, NAME_COPPER_SWORD);
	/** 铜铲 */
	public static final String NAME_COPPER_SHOVEL = "copper_shovel";
	public static final Item ITEM_COPPER_SHOVEL = new MISpade(MITool.COPPER)
			                                              .setRegistry(MODID, NAME_COPPER_SHOVEL);
	/** 铜锄 */
	public static final String NAME_COPPER_HOE = "copper_hoe";
	public static final Item ITEM_COPPER_HOE = new MIHoe(MITool.COPPER)
			                                           .setRegistry(MODID, NAME_COPPER_HOE);
	/** 铜头盔 */
	public static final String NAME_COPPER_HEAD = "copper_head";
	public static final Item ITEM_COPPER_HEAD =
			new MIArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), HEAD)
					.setRegistry(MODID, NAME_COPPER_HEAD)
					.setCreativeTab(ModernIndustry.TAB_TOOL);
	/** 铜胸甲 */
	public static final String NAME_COPPER_CHEST = "copper_chest";
	public static final Item ITEM_COPPER_CHEST =
			new MIArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), CHEST)
					.setRegistry(MODID, NAME_COPPER_CHEST)
					.setCreativeTab(ModernIndustry.TAB_TOOL);
	/** 铜护腿 */
	public static final String NAME_COPPER_LEG = "copper_leg";
	public static final Item ITEM_COPPER_LEG =
			new MIArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), LEGS)
					.setRegistry(MODID, NAME_COPPER_LEG)
					.setCreativeTab(ModernIndustry.TAB_TOOL);
	/** 铜靴子 */
	public static final String NAME_COPPER_BOOT = "copper_boot";
	public static final Item ITEM_COPPER_BOOT =
			new MIArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), FEET)
					.setRegistry(MODID, NAME_COPPER_BOOT)
					.setCreativeTab(ModernIndustry.TAB_TOOL);

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
