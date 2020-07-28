package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import xyz.emptydreams.mi.register.AutoRegister;

/**
 * 盔甲
 * @author EmptyDreams
 */
public class MIArmor extends ItemArmor implements IToolMaterial {

	public MIArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
	}

	public MIArmor setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(AutoRegister.getUnlocalizedName(modid, name));
		return this;
	}

	@Override
	public String toString() {
		return getRegistryName().toString();
	}
}
