package xyz.emptydreams.mi.items.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.utils.list.IntegerList;
import xyz.emptydreams.mi.capabilities.nonburn.NonBurnProvider;
import xyz.emptydreams.mi.register.AutoManager;
import xyz.emptydreams.mi.register.OreDicRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 放置一些普通物品
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
@AutoManager(item = true)
public final class CommonItems {

	//--------------------金属锭--------------------//

	/**
	 * 铜锭
	 */
	public static final String NAME_COPPER = "copper_item";
	public static final Item ITEM_COPPER = new MItem(NAME_COPPER, "ingotCopper");
	/**
	 * 锡锭
	 */
	public static final String NAME_TIN = "tin_item";
	public static final Item ITEM_TIN = new MItem(NAME_TIN, "ingotTin");

	//--------------------粉--------------------//

	/**
	 * 石粉
	 */
	public static final String NAME_STONE_POWDER = "stone_powder_item";
	public static final Item ITEM_STONE_POWDER = new MItem(NAME_STONE_POWDER, "dustStone", "powderStone");
	/**
	 * 燃烧剩余的煤粉
	 */
	public static final String NAME_COAL_BURN_POWDER = "coal_burn_powder";
	public static final Item ITEM_COAL_BURN_POWDER = new MItem(NAME_COAL_BURN_POWDER,
													"dustCoalBurn", "powderCoalBurn")
													.registryFuel(100).addCapability(CommonItems::getNonBurn);
	/**
	 * 铜粉
	 */
	public static final String NAME_COPPER_POWDER = "copper_powder_item";
	public static final Item ITEM_COPPER_POWDER = new MItem(NAME_COPPER_POWDER, "dustCopper", "powderCopper");
	/**
	 * 锡粉
	 */
	public static final String NAME_TIN_POWDER = "tin_powder_item";
	public static final Item ITEM_TIN_POWER = new MItem(NAME_TIN_POWDER, "dustTin", "powderCopper");

	//--------------------粉碎矿石--------------------//

	/**
	 * 粉碎铜矿石
	 */
	public static final String NAME_COPPER_CRUSH = "copper_crush_item";
	public static final Item ITEM_COPPER_CRUSH = new MItem(NAME_COPPER_CRUSH, "crushedCopper");
	/**
	 * 粉碎锡矿石
	 */
	public static final String NAME_TIN_CRUSH = "tin_crush_item";
	public static final Item ITEM_TIN_CRUSH = new MItem(NAME_TIN_CRUSH, "crushedTin");

	//--------------------其他--------------------//

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
			if (oreDic != null) OreDicRegister.registry(this, oreDic);
		}

		public MItem registryFuel(int burnTime) {
			keyList.add(this);
			valueList.add(burnTime);
			return this;
		}

		public MItem addCapability(Supplier<ICapabilityProvider> supplier) {
			CAPS.put(this, supplier);
			return this;
		}

	}

	//---------------用于添加能力---------------//

	private static final Map<Item, Supplier<ICapabilityProvider>> CAPS = new HashMap<>();
	private static final ResourceLocation NAME = new ResourceLocation(ModernIndustry.MODID, "NonBurn");

	@SubscribeEvent
	public static void addCapabilityToItem(AttachCapabilitiesEvent<ItemStack> event) {
		Supplier<ICapabilityProvider> supplier = CAPS.getOrDefault(event.getObject().getItem(), null);
		if (supplier == null) return;
		event.addCapability(NAME, supplier.get());
	}

	//---------------用于修改燃料---------------//

	private static IntegerList valueList = new IntegerList();
	private static List<Item> keyList = new ArrayList<>();
	private static int size = 0;

	@SubscribeEvent
	public static void getVanillaFurnaceFuelValue(FurnaceFuelBurnTimeEvent event) {
		Item item = event.getItemStack().getItem();
		int index = keyList.indexOf(item);
		if (index == -1) return;
		event.setBurnTime(valueList.get(index));

		//如果所有燃料都设置完毕，清除连个引用以节省内存
		if (++size >= keyList.size()) {
			keyList = null;
			valueList = null;
		}
	}

	//---------------工具方法---------------//

	private static NonBurnProvider getNonBurn() { return NonBurnProvider.SRC; }

}