package xyz.emptydreams.mi.api.gui.craft.handle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.data.Size2D;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

/**
 * @author EmptyDreams
 */
class HandleUtils {
	
	static CraftHandle.Node createGroup(Size2D rawSize, Size2D proSize,
	                                    BiFunction<SlotGroup, SlotGroup, CraftHandle.Node> creater) {
		SlotGroup raw = new SlotGroup(rawSize.getWidth(), rawSize.getHeight(), 18, 0);
		SlotGroup pro = new SlotGroup(proSize.getWidth(), proSize.getHeight(), 18, 0);
		ItemStackHandler rawHandler = new ItemStackHandler(raw.getXSize() * raw.getYSize());
		ItemStackHandler proHandler = new ItemStackHandler(pro.getXSize() * pro.getYSize());
		raw.writeFrom(0, index -> new SlotItemHandler(rawHandler, index, 0, 0) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return false;
			}
			
			@Override
			public boolean canTakeStack(EntityPlayer playerIn) {
				return false;
			}
		});
		pro.writeFrom(0, index -> new SlotItemHandler(proHandler, index, 0, 0) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return false;
			}
			
			@Override
			public boolean canTakeStack(EntityPlayer playerIn) {
				return false;
			}
		});
		return creater.apply(raw, pro);
	}
	
}