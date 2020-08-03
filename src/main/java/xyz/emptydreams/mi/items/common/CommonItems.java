package xyz.emptydreams.mi.items.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Mod;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.capabilities.nonburn.NonBurnProvider;
import xyz.emptydreams.mi.register.AutoManager;
import xyz.emptydreams.mi.register.AutoRegister;
import xyz.emptydreams.mi.register.OreDicRegister;

import java.util.function.Supplier;

import static xyz.emptydreams.mi.items.common.CommonItemHelper.CAPS;

/**
 * 放置一些普通物品
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber
@AutoManager(item = true)
public final class CommonItems {

	//--------------------金属锭--------------------//
	/** 铜锭 */
	public static final String NAME_COPPER = "copper_ingot";
	public static final Item ITEM_COPPER = new MItem(NAME_COPPER, "ingotCopper");
	/** 锡锭 */
	public static final String NAME_TIN = "tin_ingot";
	public static final Item ITEM_TIN = new MItem(NAME_TIN, "ingotTin");
	/** 青铜锭 */
	public static final String NAME_BRONZE = "bronze_ingot";
	public static final Item ITEM_BRONZE = new MItem(NAME_BRONZE, "ingotBronze");

	//--------------------粉--------------------//
	/** 石粉 */
	public static final String NAME_STONE_POWDER = "stone_powder";
	public static final Item ITEM_STONE_POWDER = new MItem(NAME_STONE_POWDER,
											"dustStone", "powderStone");
	/** 燃烧剩余的煤粉 */
	public static final String NAME_COAL_BURN_POWDER = "coal_burn_powder";
	public static final Item ITEM_COAL_BURN_POWDER = new MItem(NAME_COAL_BURN_POWDER,
											"dustCoalBurn", "powderCoalBurn")
											.registryFuel(100).addCapability(CommonItems::getNonBurn);
	/** 煤粉 */
	public static final String NAME_COAL_POWDER = "coal_powder";
	public static final Item ITEM_COAL_POWDER = new MItem(NAME_COAL_POWDER,
											"dustCoal", "powderCoal").registryFuel(800);
	/** 铜粉 */
	public static final String NAME_COPPER_POWDER = "copper_powder";
	public static final Item ITEM_COPPER_POWDER = new MItem(NAME_COPPER_POWDER,
											"dustCopper", "powderCopper");
	/** 青铜粉 */
	public static final String NAME_BRONZE_POWDER = "bronze_powder";
	public static final Item ITEM_BRONZE_POWDER = new MItem(NAME_BRONZE_POWDER,
											"dustBronze", "powderBronze");
	/** 锡粉 */
	public static final String NAME_TIN_POWDER = "tin_powder";
	public static final Item ITEM_TIN_POWER = new MItem(NAME_TIN_POWDER, "dustTin", "powderCopper");
	/** 铁粉 */
	public static final String NAME_IRON_POWDER = "iron_powder";
	public static final Item ITEM_IRON_POWDER = new MItem(NAME_IRON_POWDER, "dustIron", "powderIron");
	/** 金粉 */
	public static final String NAME_GOLD_POWDER = "gold_powder";
	public static final Item ITEM_GOLD_POWDER = new MItem(NAME_GOLD_POWDER, "dustGold", "powderGold");
	/** 钻石粉 */
	public static final String NAME_DIAMOND_POWDER = "diamond_powder";
	public static final Item ITEM_DIAMOND_POWDER = new MItem(NAME_DIAMOND_POWDER,
												"dustDiamond", "powderDiamond");

	//--------------------粉碎矿石--------------------//
	/** 粉碎铜矿石 */
	public static final String NAME_COPPER_CRUSH = "copper_crush";
	public static final Item ITEM_COPPER_CRUSH = new MItem(NAME_COPPER_CRUSH, "crushedCopper");
	/** 粉碎锡矿石 */
	public static final String NAME_TIN_CRUSH = "tin_crush";
	public static final Item ITEM_TIN_CRUSH = new MItem(NAME_TIN_CRUSH, "crushedTin");
	/** 粉碎铁矿石 */
	public static final String NAME_IRON_CRUSH = "iron_crush";
	public static final Item ITEM_IRON_CRUSH = new MItem(NAME_IRON_CRUSH, "crushedIron");
	/** 粉碎金矿石 */
	public static final String NAME_GOLD_CRUSH = "gold_crush";
	public static final Item ITEM_GOLD_CRUSH = new MItem(NAME_GOLD_CRUSH, "crushedGold");
	/** 粉碎钻石矿石 */
	public static final String NAME_DIAMOND_CRUSH = "diamond_crush";
	public static final Item ITEM_DIAMOND_CRUSH = new MItem(NAME_DIAMOND_CRUSH, "crushedDiamond");
	/** 粉碎煤矿石 */
	public static final String NAME_COAL_CRUSH = "coal_crush";
	public static final Item ITEM_COAL_CRUSH = new MItem(NAME_COAL_CRUSH, "crushedCoal");
	
	//--------------------电子元件--------------------//
	/** 电动机 */
	public static final String NAME_MOTOR = "motor";
	public static final Item ITEM_MOTOR = new MItem(NAME_MOTOR, "mi_motor");
	/** 电路板 */
	public static final String NAME_CIRCUIT = "circuit";
	public static final Item ITEM_CIRCUIT = new MItem(NAME_CIRCUIT, "circuitBasic");
	/** 线圈 */
	public static final String NAME_COIL = "coil";
	public static final Item ITEM_COIL = new MItem(NAME_COIL, "mi_coil");
	/** 电磁铁 */
	public static final String NAME_ELECTROMAGNET = "electromagnet";
	public static final Item ITEM_ELECTROMAGNET = new MItem(NAME_ELECTROMAGNET, "mi_electromagnet");
	
	
	//--------------------机械元件--------------------//
	/** 铁棍 */
	public static final String NAME_IRON_ROD = "iron_rod";
	public static final Item ITEM_IRON_ROD = new MItem(NAME_IRON_ROD, "mi_ironRod");
	/** 铜棍 */
	public static final String NAME_COPPER_ROD = "copper_rod";
	public static final Item ITEM_COPPER_ROD = new MItem(NAME_COPPER_ROD, "mi_copperRod");
	/** 青铜棍 */
	public static final String NAME_BRONZE_ROD = "bronze_rod";
	public static final Item ITEM_BRONZE_ROD = new MItem(NAME_BRONZE_ROD, "mi_bronzeRod");
	/** 机械刀片 */
	public static final String NAME_BLADE = "blade";
	public static final Item ITEM_BLADE = new MItem(NAME_BLADE, "mi_blade");
	
	//--------------------板--------------------//
	/** 铁板 */
	public static final String NAME_IRON_PLATE = "iron_plate";
	public static final Item ITEM_IRON_PLATE = new MItem(NAME_IRON_PLATE, "plateIron");
	/** 铜板 */
	public static final String NAME_COPPER_PLATE = "copper_plate";
	public static final Item ITEM_COPPER_PLATE = new MItem(NAME_COPPER_PLATE, "plateCopper");
	/** 锡板 */
	public static final String NAME_TIN_PLATE = "tin_plate";
	public static final Item ITEM_TIN_PLATE = new MItem(NAME_TIN_PLATE, "plateTin");
	/** 青铜板 */
	public static final String NAME_BRONZE_PLATE = "bronze_plate";
	public static final Item ITEM_BRONZE_PLATE = new MItem(NAME_BRONZE_PLATE, "plateBronze");
	/** 金板 */
	public static final String NAME_GOLD_PLATE = "gold_plate";
	public static final Item ITEM_GOLD_PLATE = new MItem(NAME_GOLD_PLATE, "plateGold");
	
	//--------------------其他--------------------//

	/** 普通物品的父类 */
	private static final class MItem extends Item {

		public MItem(String registryName) {
			setRegistryName(ModernIndustry.MODID, registryName);
			setUnlocalizedName(AutoRegister.getUnlocalizedName(registryName));
			setNoRepair();
			setMaxDamage(0);
			setCreativeTab(ModernIndustry.TAB_ITEM);
		}

		public MItem(String registryName, String... oreDic) {
			this(registryName);
			if (oreDic != null) OreDicRegister.registry(this, oreDic);
		}

		private int fuel = -1;

		public MItem registryFuel(int burnTime) {
			fuel = burnTime;
			return this;
		}

		@Override
		public int getItemBurnTime(ItemStack itemStack) {
			return fuel;
		}

		public MItem addCapability(Supplier<ICapabilityProvider> supplier) {
			CAPS.put(this, supplier);
			return this;
		}
		
		@Override
		public String toString() {
			return getRegistryName().toString();
		}
	}

	//---------------工具方法---------------//

	private static NonBurnProvider getNonBurn() { return NonBurnProvider.SRC; }

}