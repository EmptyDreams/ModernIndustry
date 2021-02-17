package xyz.emptydreams.mi.api.event;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;

/**
 * Gui注册事件
 * @author EmptyDreams
 */
public class GuiRegistryEvent extends Event {
	
	@SuppressWarnings("deprecation")
	public ResourceLocation registry(ResourceLocation key, IContainerCreater creator) {
		return GuiLoader.registry(key, creator);
	}
	
}