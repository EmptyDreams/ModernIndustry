package xyz.emptydreams.mi.items.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.list.IntegerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class CommonItemHelper {

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

	//---------------用于修改燃料---------------//

	static IntegerList valueList = new IntegerList();
	static List<Item> keyList = new ArrayList<>();
	static int size = 0;

	@SubscribeEvent
	public static void getVanillaFurnaceFuelValue(FurnaceFuelBurnTimeEvent event) {
		Item item = event.getItemStack().getItem();
		int index = keyList.indexOf(item);
		if (index == -1) return;
		event.setBurnTime(valueList.get(index));

		//如果所有燃料都设置完毕，清除连个引用以节省内存
		if (++size >= keyList.size()) {
			keyList = null;
			valueList = null;
		}
	}

}
