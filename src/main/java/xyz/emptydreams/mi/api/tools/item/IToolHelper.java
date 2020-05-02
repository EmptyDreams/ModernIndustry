package xyz.emptydreams.mi.api.tools.item;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyCapability;
import xyz.emptydreams.mi.api.tools.capabilities.PropertyProvider;
import xyz.emptydreams.mi.api.tools.property.IProperty;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface IToolHelper {
	
	default void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		for (IProperty property : manager)
			tooltip.add(I18n.format(property.getName()) + ": " + property.getValue());
	}
	
	default boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = false;
		for (IProperty property : manager)
			b |= property.hitEntity(stack, target, attacker);
		return b;
	}
	
	default boolean onBlockDestroyed(ItemStack stack, World worldIn,
	                                 IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = false;
		for (IProperty property : manager)
			b |= property.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		return b;
	}
	
	default EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                   EnumHand hand, EnumFacing facing,
	                                   float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return EnumActionResult.PASS;
		EnumActionResult result = EnumActionResult.PASS;
		EnumActionResult temp;
		for (IProperty property : manager) {
			temp = property.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			if (result != EnumActionResult.SUCCESS) {
				if (temp == EnumActionResult.SUCCESS) result = EnumActionResult.SUCCESS;
				else if (temp == EnumActionResult.FAIL) result = EnumActionResult.FAIL;
			}
		}
		return result;
	}
	
	default boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn,
	                                 EntityLivingBase target, EnumHand hand) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = false;
		for (IProperty property : manager)
			b |= property.itemInteractionForEntity(stack, playerIn, target, hand);
		return b;
	}
	
	default void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		for (IProperty property : manager) property.onCreated(stack, worldIn, playerIn);
	}
	
	default void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		for (IProperty property : manager) property.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}
	
	default boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		PropertyManager manager = item.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = true;
		for (IProperty property : manager)
			b &= property.onDroppedByPlayer(item, player);
		return b;
	}
	
	default EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
	                                        EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return EnumActionResult.PASS;
		EnumActionResult result = EnumActionResult.PASS;
		EnumActionResult temp;
		for (IProperty property : manager) {
			temp = property.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
			if (result != EnumActionResult.SUCCESS) {
				if (temp == EnumActionResult.SUCCESS) result = EnumActionResult.SUCCESS;
				else if (temp == EnumActionResult.FAIL) result = EnumActionResult.FAIL;
			}
		}
		return result;
	}
	
	default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		PropertyManager manager = itemstack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = false;
		for (IProperty property : manager)
			b |= property.onBlockStartBreak(itemstack, pos, player);
		return b;
	}
	
	default void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		for (IProperty property : manager) property.onUsingTick(stack, player, count);
	}
	
	default boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		PropertyManager manager = stack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = false;
		for (IProperty property : manager)
			b |= property.onLeftClickEntity(stack, player, entity);
		return b;
	}
	
	default boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem) {
		PropertyManager manager = entityItem.getItem().getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return false;
		boolean b = false;
		for (IProperty property : manager)
			b |= property.onEntityItemUpdate(entityItem);
		return b;
	}
	
	default void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		PropertyManager manager = itemStack.getCapability(PropertyCapability.PROPERTY, null);
		if (manager == null) return;
		for (IProperty property : manager) property.onArmorTick(world, player, itemStack);
	}
	
	default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new PropertyProvider(PropertyCapability.PROPERTY.getDefaultInstance());
	}
	
}
