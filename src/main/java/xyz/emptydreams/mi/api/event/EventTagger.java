package xyz.emptydreams.mi.api.event;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 事件触发器
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class EventTagger {
	
	@SubscribeEvent
	public static void tagger(RegistryEvent.Register<IRecipe> event) {
		MinecraftForge.EVENT_BUS.post(new NetWorkRegistryEvent());
		MinecraftForge.EVENT_BUS.post(new CraftGuideRegistryEvent());
	}
	
}
