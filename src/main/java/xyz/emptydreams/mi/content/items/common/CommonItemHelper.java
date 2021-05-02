package xyz.emptydreams.mi.content.items.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
final class CommonItemHelper {

	//---------------用于添加能力---------------//

	static final Map<Item, Supplier<ICapabilityProvider>> CAPS = new HashMap<>();

	@SubscribeEvent
	public static void addCapabilityToItem(AttachCapabilitiesEvent<ItemStack> event) {
		Item item = event.getObject().getItem();
		Supplier<ICapabilityProvider> supplier = CAPS.getOrDefault(item, null);
		if (supplier == null) return;
		ICapabilityProvider provider = supplier.get();
		event.addCapability(StringUtil.revampAddToRL(item.getRegistryName(),
				provider.getClass().getSimpleName()), provider);
	}

}