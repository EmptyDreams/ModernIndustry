package xyz.emptydreams.mi.api.tools.property;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
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

/**
 * 所有属性需要实现的接口，注意：实现该接口的类必须带有默认构造函数
 * @author EmptyDreams
 * @version V1.0
 */
public interface IProperty {
	
	Random RANDOM_PROPERTY = new Random();
	
	/** 获取名称 */
	String getName();
	/** 获取要显示的值 */
	String getValue();
	/** 获取原始名称 */
	String getOriginalName();
	/** 判断名称是否相等 */
	default boolean equalsName(String original) {
		return getOriginalName().equals(original);
	}
	
	void write(NBTTagCompound compound);
	void read(NBTTagCompound compound);
	@Override int hashCode();
	@Override boolean equals(Object o);
	
	default boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return false;
	}
	default boolean onBlockDestroyed(ItemStack stack, World worldIn,
	                                 IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		return false;
	}
	default EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                   EnumHand hand, EnumFacing facing,
	                                   float hitX, float hitY, float hitZ) { return EnumActionResult.PASS; }
	default boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn,
	                                         EntityLivingBase target, EnumHand hand) { return false; }
	default void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) { }
	default void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) { }
	default boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) { return true; }
	default EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
	                                        EnumFacing side, float hitX, float hitY, float hitZ,
	                                        EnumHand hand) { return EnumActionResult.PASS; }
	default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
	{
		return false;
	}
	default void onUsingTick(ItemStack stack, EntityLivingBase player, int count) { }
	default boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		return false;
	}
	default boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem) {
		return false;
	}
	default void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) { }
	
	static String createName(String name) {
		return createString(name, "name");
	}
	
	static String createString(String name, String suffix) {
		return "mi.property." + name + "." + suffix;
	}
	
}
