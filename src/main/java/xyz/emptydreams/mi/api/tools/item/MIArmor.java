package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

/**
 * 盔甲
 * @author EmptyDreams
 */
public class MIArmor extends ItemArmor implements IToolMaterial {

	public MIArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
	}

	public MIArmor setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(name);
		return this;
	}

}
