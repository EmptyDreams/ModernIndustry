package xyz.emptydreams.mi.coremod.other;

import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.OpenGuiHandler;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.register.others.AutoLoader;
import xyz.emptydreams.mi.api.utils.TickHelper;
import xyz.emptydreams.mi.content.net.ClassInfoViewerMessage;

import java.lang.reflect.Method;

/**
 * @author EmptyDreams
 */
@AutoLoader
public class MixinUtil {
	
	public static FMLMessage.OpenGui msg;
	public static OpenGuiHandler handler;
	
	static {
		TickHelper.addClientTask(() -> {
			if ((!ClassInfoViewerMessage.isUpdate()) || msg == null || handler == null) return false;
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
			return false;
		});
	}
	
}