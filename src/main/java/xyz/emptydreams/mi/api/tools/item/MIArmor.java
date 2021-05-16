package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.utils.StringUtil;

/**
 * 盔甲
 * @author EmptyDreams
 */
public class MIArmor extends ItemArmor implements IToolMaterial {

	public MIArmor(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, materialIn.ordinal(), equipmentSlotIn);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}

	public MIArmor setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(StringUtil.getUnlocalizedName(modid, name));
		return this;
	}

	@Override
	public String toString() {
		return getRegistryName().toString();
	}
}