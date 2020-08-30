package xyz.emptydreams.mi.register.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.emptydreams.mi.api.event.CraftGuideRegistryEvent;

import java.util.HashMap;
import java.util.Map;

import static xyz.emptydreams.mi.api.utils.StringUtil.checkNull;


/**
 * 自动注册工具的合成表
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class RecipeRegister {

	/** 需要注册的锄头 */
	private static final Map<ItemHoe, String> HOES = new HashMap<>();
	/** 需要注册的斧子 */
	private static final Map<ItemAxe, String> AXES = new HashMap<>();
	/** 需要注册的镐子 */
	private static final Map<ItemPickaxe, String> PICKAXES = new HashMap<>();
	/** 需要注册的铲子 */
	private static final Map<ItemSpade, String> SPADES = new HashMap<>();
	/** 需要注册的剑 */
	private static final Map<ItemSword, String> SWORDS = new HashMap<>();
	/** 需要注册的装备 */
	private static final Map<ItemArmor, String> ARMORS = new HashMap<>();

	/**
	 * 注册一个锄头
	 * @param material 材质
	 */
	public static void registry(ItemHoe hoe, String material) {
		HOES.put(checkNull(hoe, "hoe"), checkNull(material, "material"));
	}

	/**
	 * 注册一个斧子
	 * @param material 材质
	 */
	public static void registry(ItemAxe axe, String material) {
		AXES.put(checkNull(axe, "axe"), checkNull(material, "material"));
	}

	/**
	 * 注册一个镐子
	 * @param material 材质
	 */
	public static void registry(ItemPickaxe pickaxe, String material) {
		PICKAXES.put(checkNull(pickaxe, "pickaxe"), checkNull(material, "material"));
	}

	/**
	 * 注册一个铲子
	 * @param material 材质
	 */
	public static void registry(ItemSpade spade, String material) {
		SPADES.put(checkNull(spade, "spade"), checkNull(material, "material"));
	}

	/**
	 * 注册一个剑
	 * @param material 材质
	 */
	public static void registry(ItemSword sword, String material) {
		SWORDS.put(checkNull(sword, "sword"), checkNull(material, "material"));
	}

	/**
	 * 注册一个装备
	 * @param material 材质
	 */
	public static void registry(ItemArmor armor, String material) {
		ARMORS.put(checkNull(armor, "armor"), checkNull(material, "material"));
	}

	/**
	 * 注册一个合成表
	 * @param item 必须是铲子、稿子、斧子、锄头、剑中的一种
	 * @param material 材质
	 * @throws IllegalArgumentException 若不支持传入的item
	 */
	public static void registry(Item item, String material) {
		if (item instanceof ItemAxe) registry((ItemAxe) item, material);
		else if (item instanceof ItemHoe) registry((ItemHoe) item, material);
		else if (item instanceof ItemPickaxe) registry((ItemPickaxe) item, material);
		else if (item instanceof ItemSpade) registry((ItemSpade) item, material);
		else if (item instanceof ItemSword) registry((ItemSword) item, material);
		else if (item instanceof ItemArmor) registry(((ItemArmor) item), material);
		else throw new IllegalArgumentException("不支持该item的注册：" + item.getRegistryName());
	}

	public static final String STICK = "stickWood";

	@SubscribeEvent
	public static void registerRecipe(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistry<IRecipe> registry = event.getRegistry();
		HOES.forEach((hoe, material) -> registry.register(getRecipe(hoe, new Object[] { "##", " |", " |",
															'#', material, '|', STICK })));
		AXES.forEach((axe, material) -> registry.register(getRecipe(axe, new Object[] { "##", "#|", " |",
															'#', material, '|', STICK })));
		PICKAXES.forEach((pick, material) -> registry.register(getRecipe(pick, new Object[] { "###", " | ", " | ",
															'#', material, '|', STICK })));
		SPADES.forEach((spade, material) -> registry.register(getRecipe(spade, new Object[] { "#", "|", "|",
															'#', material, '|', STICK })));
		SWORDS.forEach((sword, material) -> registry.register(getRecipe(sword, new Object[] { "#", "#", "|",
															'#', material, '|', STICK })));
		ARMORS.forEach((armor, material) -> registryArmor(armor, material, registry));
		MinecraftForge.EVENT_BUS.post(new CraftGuideRegistryEvent());
	}

	private static void registryArmor(ItemArmor armor, String material, IForgeRegistry<IRecipe> registry) {
		ResourceLocation registryName = armor.getRegistryName();
		switch (armor.armorType) {
			case FEET:
				registry.register(getRecipe(armor, new Object[]{ "# #", "# #",
						'#', material}));
				break;
			case LEGS:
				registry.register(getRecipe(armor, new Object[]{ "###", "# #", "# #",
						'#', material}));
				break;
			case CHEST:
				registry.register(getRecipe(armor, new Object[]{ "# #", "###", "###",
						'#', material }));
				break;
			case HEAD:
				registry.register(getRecipe(armor, new Object[]{ "###", "# #",
						'#', material }));
				break;
			default: throw new IllegalArgumentException("不支持该元素[" +
					registryName + "]:" + armor.getEquipmentSlot());
		}
	}

	private static ShapedOreRecipe getRecipe(Item output, Object[] params) {
		return getRecipe(output, output.getRegistryName(), params);
	}

	private static ShapedOreRecipe getRecipe(Item output, ResourceLocation name, Object[] params) {
		ShapedOreRecipe recipe = new ShapedOreRecipe(output.getRegistryName(), output, params);
		recipe.setRegistryName(name);
		return recipe;
	}

}
