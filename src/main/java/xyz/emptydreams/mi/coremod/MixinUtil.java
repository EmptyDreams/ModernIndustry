package xyz.emptydreams.mi.coremod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.OpenGuiHandler;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.content.net.ClassInfoViewerMessage;

import java.lang.reflect.Method;

/**
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public class MixinUtil {
	
	public static FMLMessage.OpenGui msg;
	public static OpenGuiHandler handler;
	
	@SubscribeEvent
	public static void openGui(TickEvent.ClientTickEvent event) {
		if ((!ClassInfoViewerMessage.isUpdate()) || msg == null || handler == null) return;
		try {
			Class<? extends OpenGuiHandler> clazz = handler.getClass();
			Method method = clazz.getDeclaredMethod("process", FMLMessage.OpenGui.class);
			method.setAccessible(true);
			method.invoke(handler, msg);
			handler = null;
			msg = null;
		} catch (Exception e) {
			throw TransferException.instance(e);
		}
	}
	
}