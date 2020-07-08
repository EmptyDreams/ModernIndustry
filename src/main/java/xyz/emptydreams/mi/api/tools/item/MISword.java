package xyz.emptydreams.mi.api.tools.item;

import javax.annotation.Nullable;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MISword extends ItemSword implements IToolMaterial {
	
	private final double SPEED;
	private final double DAMAGE;
	
	public MISword(ToolMaterial materialIn, float damage, float speed) {
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
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", DAMAGE, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", SPEED, 0));
		}
		
		return multimap;
	}
	
	public Item setRegistry(String name) {
		setRegistryName(name).setUnlocalizedName(name);
		return this;
	}

}
