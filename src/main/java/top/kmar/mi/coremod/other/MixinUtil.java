package top.kmar.mi.coremod.other;

import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.OpenGuiHandler;
import top.kmar.mi.api.exception.TransferException;
import top.kmar.mi.api.register.others.AutoLoader;
import top.kmar.mi.api.utils.TickHelper;
import top.kmar.mi.content.net.ClassInfoViewerMessage;

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