package xyz.emptydreams.mi.coremod.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.emptydreams.mi.api.event.ItemDamageEvent;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	
	@Shadow int itemDamage;
	@Final @Shadow private Item item;
	
	/**
	 * 在amount为负数时不触发事件，在amount为整数时触发事件
	 * @author EmptyDreams
	 */
	@Overwrite
	public void damageItem(int amount, EntityLivingBase entityIn) {
		if (amount == 0) return;
		ItemStack stack = (ItemStack) (Object) this;
		if (amount < 0) {
			amount = -amount;
		} else {
			MinecraftForge.EVENT_BUS.post(new ItemDamageEvent(stack, amount, entityIn));
		}
		if (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode)
		{
			if (stack.isItemStackDamageable())
			{
				if (stack.attemptDamageItem(amount, entityIn.getRNG(),
						entityIn instanceof EntityPlayerMP ? (EntityPlayerMP)entityIn : null))
				{
					entityIn.renderBrokenItemStack(stack);
					stack.shrink(1);
					
					if (entityIn instanceof EntityPlayer)
					{
						EntityPlayer entityplayer = (EntityPlayer)entityIn;
						entityplayer.addStat(StatList.getObjectBreakStats(item));
					}
					
					itemDamage = 0;
				}
			}
		}
	}
	
}
