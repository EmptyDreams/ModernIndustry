package xyz.emptydreams.mi.api.tools.item;

import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MIHoe extends ItemHoe implements IToolHelper {
	
	public MIHoe(ToolMaterial material) {
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
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IToolHelper.super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return super.hitEntity(stack, target, attacker) & IToolHelper.super.hitEntity(stack, target, attacker);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state,
	                                BlockPos pos, EntityLivingBase entityLiving) {
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving) &
				       IToolHelper.super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		EnumActionResult temp = IToolHelper.super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		if (result != EnumActionResult.SUCCESS) return temp;
		else return result;
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn,
	                                        EntityLivingBase target, EnumHand hand) {
		return super.itemInteractionForEntity(stack, playerIn, target, hand) |
				       IToolHelper.super.itemInteractionForEntity(stack, playerIn, target, hand);
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		IToolHelper.super.onCreated(stack, worldIn, playerIn);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
		IToolHelper.super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return super.onDroppedByPlayer(item, player) & IToolHelper.super.onDroppedByPlayer(item, player);
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
	                                       EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		EnumActionResult result = super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
		EnumActionResult temp = IToolHelper.super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
		if (result != EnumActionResult.SUCCESS) return temp;
		else return result;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		return super.onBlockStartBreak(itemstack, pos, player) |
				       IToolHelper.super.onBlockStartBreak(itemstack, pos, player);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		super.onUsingTick(stack, player, count);
		IToolHelper.super.onUsingTick(stack, player, count);
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return super.onLeftClickEntity(stack, player, entity) |
				       IToolHelper.super.onLeftClickEntity(stack, player, entity);
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		return super.onEntityItemUpdate(entityItem) | IToolHelper.super.onEntityItemUpdate(entityItem);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		super.onArmorTick(world, player, itemStack);
		IToolHelper.super.onArmorTick(world, player, itemStack);
	}
	
}
