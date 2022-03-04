package top.kmar.mi.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * 当一个物品损耗耐久度时触发.（即调用{@link ItemStack#damageItem(int, EntityLivingBase)}时）<br>
 * 关于方法约定：<br>
 *    在该事件中若想要损耗物品耐久度，
 *    不能直接调用{@link ItemStack#damageItem(int, EntityLivingBase)}，
 *    应该调用{@link #damageItem(int)}，
 *    否则会导致无限递归！
 * @author EmptyDreams
 */
public class ItemDamageEvent extends Event {

	public final ItemStack stack;
	public final int damage;
	public final EntityLivingBase entityLiving;
	
	/**
	 * @param stack 损耗的物品
	 */
	public ItemDamageEvent(ItemStack stack, int damage, EntityLivingBase entityLiving) {
		this.stack = stack;
		this.damage = damage;
		this.entityLiving = entityLiving;
	}

	public void damageItem(int count) {
		stack.damageItem(-count, entityLiving);
	}
	
}