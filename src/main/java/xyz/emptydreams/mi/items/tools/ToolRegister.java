package xyz.emptydreams.mi.items.tools;

import javax.annotation.Nullable;

import static net.minecraft.inventory.EntityEquipmentSlot.HEAD;
import static net.minecraft.inventory.EntityEquipmentSlot.FEET;
import static net.minecraft.inventory.EntityEquipmentSlot.CHEST;
import static net.minecraft.inventory.EntityEquipmentSlot.LEGS;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyCapability;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyProvider;
import xyz.emptydreams.mi.api.tools.item.MIAxe;
import xyz.emptydreams.mi.api.tools.item.MIHoe;
import xyz.emptydreams.mi.api.tools.item.MIPickaxe;
import xyz.emptydreams.mi.api.tools.item.MISpade;
import xyz.emptydreams.mi.api.tools.item.MISword;
import xyz.emptydreams.mi.api.tools.property.PoorQuality;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;
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
	public static final Item ITEM_COPPER_AXE = new MIAxe(MITool.COPPER, 8, -3.1F) {
		@Nullable
		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
			return createProvider();
		}
	}.setRegistry(NAME_COPPER_AXE);
	/** 铜镐 */
	public static final String NAME_COPPER_PICKAXE = "copper_pickaxe_tool";
	public static final Item ITEM_COPPER_PICKAXE = new MIPickaxe(MITool.COPPER) {
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
			return createProvider();
		}
	}.setRegistry(NAME_COPPER_PICKAXE);
	/** 铜剑 */
	public static final String NAME_COPPER_SWORD = "copper_sword_tool";
	public static final Item ITEM_COPPER_SWORD = new MISword(MITool.COPPER, 5, -2.5F) {
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
			return createProvider();
		}
	}.setRegistry(NAME_COPPER_SWORD);
	/** 铜铲 */
	public static final String NAME_COPPER_SHOVEL = "copper_shovel_tool";
	public static final Item ITEM_COPPER_SHOVEL = new MISpade(MITool.COPPER){
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
			return createProvider();
		}
	}.setRegistry(NAME_COPPER_SHOVEL);
	/** 铜锄 */
	public static final String NAME_COPPER_HOE = "copper_hoe_tool";
	public static final Item ITEM_COPPER_HOE = new MIHoe(MITool.COPPER){
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
			return createProvider();
		}
	}.setRegistry(NAME_COPPER_HOE);
	/** 铜头盔 */
	public static final String NAME_COPPER_HEAD = "copper_head";
	public static final Item ITEM_COPPER_HEAD =
			new ItemArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), HEAD) {
				{
					setRegistryName(ModernIndustry.MODID, NAME_COPPER_HEAD)
							 .setUnlocalizedName(NAME_COPPER_HEAD)
							 .setCreativeTab(ModernIndustry.TAB_TOOL);
				}
				@Nullable
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
					return createProvider();
				}
			};
	/** 铜胸甲 */
	public static final String NAME_COPPER_CHEST = "copper_chest";
	public static final Item ITEM_COPPER_CHEST =
			new ItemArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), CHEST) {
				{
					setRegistryName(ModernIndustry.MODID, NAME_COPPER_CHEST)
							 .setUnlocalizedName(NAME_COPPER_CHEST)
							 .setCreativeTab(ModernIndustry.TAB_TOOL);
				}
				@Nullable
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
					return createProvider();
				}
			};
	/** 铜护腿 */
	public static final String NAME_COPPER_LEG = "copper_leg";
	public static final Item ITEM_COPPER_LEG =
			new ItemArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), LEGS) {
				{
					setRegistryName(ModernIndustry.MODID, NAME_COPPER_LEG)
							 .setUnlocalizedName(NAME_COPPER_LEG)
							 .setCreativeTab(ModernIndustry.TAB_TOOL);
				}
				@Nullable
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
					return createProvider();
				}
			};
	/** 铜靴子 */
	public static final String NAME_COPPER_BOOT = "copper_boot";
	public static final Item ITEM_COPPER_BOOT =
			new ItemArmor(MITool.COPPER_ARMOR, MITool.COPPER_ARMOR.ordinal(), FEET) {
				{
					setRegistryName(ModernIndustry.MODID, NAME_COPPER_BOOT)
							 .setUnlocalizedName(NAME_COPPER_BOOT)
							 .setCreativeTab(ModernIndustry.TAB_TOOL);
				}
				@Nullable
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
					return createProvider();
				}
			};
	
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
	
	private static ICapabilityProvider createProvider() {
		PropertyManager manager = PropertyCapability.PROPERTY.getDefaultInstance();
		manager.addProperty(PoorQuality.randProperty(1, 9));
		return new PropertyProvider(manager);
	}
	
}
