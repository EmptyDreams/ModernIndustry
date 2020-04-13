package xyz.emptydreams.mi.items.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import xyz.emptydreams.mi.ModernIndustry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.util.EnumHelper;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public class MI_Tool {

	/** 铜制工具 */
	public static final Item.ToolMaterial COPPER = EnumHelper.addToolMaterial("COPPER", 2, 240, 5.0F, 2.0F, 9);
	/** 铜制盔甲 */
	public static final ItemArmor.ArmorMaterial COPPER_ARMOR = EnumHelper.addArmorMaterial(
				"COPPER", ModernIndustry.MODID + ":" + "copper", 240,
				new int[] { 2, 5, 5, 2 }, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
	
	public static class MI_Axe extends ItemAxe {

		public MI_Axe(Item.ToolMaterial material, float damage, float speed) {
			super(material, damage, speed);
			setCreativeTab(ModernIndustry.TAB_TOOL);
		}
		
		public Item setRegistry(String name) {
			setRegistryName(name).setUnlocalizedName(name);
			return this;
		}
		
		@Override
		public String toString() {
			return getRegistryName().toString();
		}
		
	}
	
	public static class MI_Pickaxe extends ItemPickaxe {

		public MI_Pickaxe(ToolMaterial material) {
			super(material);
			setCreativeTab(ModernIndustry.TAB_TOOL);
		}
		
		public Item setRegistry(String name) {
			setRegistryName(name).setUnlocalizedName(name);
			return this;
		}
		
		@Override
		public String toString() {
			return getRegistryName().toString();
		}
		
	}
	
	public static class MI_Hoe extends ItemHoe {

		public MI_Hoe(ToolMaterial material) {
			super(material);
			setCreativeTab(ModernIndustry.TAB_TOOL);
		}
		
		public Item setRegistry(String name) {
			setRegistryName(name).setUnlocalizedName(name);
			return this;
		}
		
		@Override
		public String toString() {
			return getRegistryName().toString();
		}
		
	}
	
	public static class MI_Shovel extends ItemSpade {

		public MI_Shovel(ToolMaterial material) {
			super(material);
			setCreativeTab(ModernIndustry.TAB_TOOL);
		}
		
		public Item setRegistry(String name) {
			setRegistryName(name).setUnlocalizedName(name);
			return this;
		}
		
		@Override
		public String toString() {
			return getRegistryName().toString();
		}
		
	}
	
	public static class MI_Sword extends ItemSword {
		
		final double SPEED;
		final double DAMAGE;
		
		protected MI_Sword(ToolMaterial materialIn, float damage, float speed) {
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
			Multimap<String, AttributeModifier> multimap = HashMultimap.create();

			if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", DAMAGE, 0));
	            		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", SPEED, 0));
			}

			return multimap;
		}
		
		public Item setRegistry(String name) {
			setRegistryName(name).setUnlocalizedName(name);
			return this;
		}
		
	}
	
}
