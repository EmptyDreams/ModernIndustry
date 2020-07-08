package xyz.emptydreams.mi.items.common;

import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.register.AutoManager;

/**
 * 放置一些普通物品
 * @author EmptyDreams
 */
@AutoManager(item = true, block = false)
public final class CommonItems {

	//--------------------金属锭--------------------//

	/** 铜锭 */
	public static final String NAME_COPPER = "copper_item";
	public static final Item ITEM_COPPER = new MItem(NAME_COPPER, "ingotCopper");
	/** 锡锭 */
	public static final String NAME_TIN = "tin_item";
	public static final Item ITEM_TIN = new MItem(NAME_TIN, "ingotTin");

	//--------------------金属粉--------------------//

	/** 铜粉 */
	public static final String NAME_COPPER_POWDER = "copper_powder_item";
	public static final Item ITEM_COPPER_POWDER = new MItem(NAME_COPPER_POWDER, "dustCopper");
	/** 锡粉 */
	public static final String NAME_TIN_POWDER = "tin_powder_item";
	public static final Item ITEM_TIN_POWER = new MItem(NAME_TIN_POWDER, "dustTin");

	//--------------------粉碎矿石--------------------//

	/** 粉碎铜矿石 */
	public static final String NAME_COPPER_CRUSH = "copper_crush_item";
	public static final Item ITEM_COPPER_CRUSH = new MItem(NAME_COPPER_CRUSH, "crushedCopper");
	/** 粉碎锡矿石 */
	public static final String NAME_TIN_CRUSH = "tin_crush_item";
	public static final Item ITEM_TIN_CRUSH = new MItem(NAME_TIN_CRUSH, "crushedTin");

	private static final class MItem extends Item {

		public MItem(String registryName) {
			setRegistryName(registryName);
			setUnlocalizedName(registryName);
			setNoRepair();
			setMaxDamage(0);
			setCreativeTab(ModernIndustry.TAB_ITEM);
		}

		public MItem(String registryName, String... oreDic) {
			this(registryName);
			if (oreDic != null)
				for (String ore : oreDic)
					OreDictionary.registerOre(ore, this);
		}

	}

}
