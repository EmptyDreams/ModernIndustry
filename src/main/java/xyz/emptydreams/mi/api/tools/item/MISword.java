package xyz.emptydreams.mi.api.tools.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSword;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.utils.StringUtil;

/**
 * 剑
 * @author EmptyDreams
 */
public class MISword extends ItemSword implements IToolMaterial {
	
	private final double SPEED;
	private final double DAMAGE;
	
	public MISword(ToolMaterial materialIn, float damage, double speed) {
		super(materialIn);
		DAMAGE = damage;
		SPEED = speed;
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}
	
	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multiMap = HashMultimap.create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multiMap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", DAMAGE, 0));
			multiMap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", SPEED, 0));
		}
		return multiMap;
	}

	public MISword setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(StringUtil.getUnlocalizedName(modid, name));
		return this;
	}

}