package xyz.emptydreams.mi.api.net.handler;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.IDataReader;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.ParseResultEnum;

import java.util.Map;

/**
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public final class RawQueue {
	
	private static final Map<IDataReader, String> queue = new Object2ObjectOpenHashMap<>();
	
	/** 将一个任务添加到队列中，方法内部自动解析Key值 */
	public static void add(IDataReader data, String key) {
		queue.put(data, key);
	}
	
	@SubscribeEvent
	public static void tryToCleanQueue(TickEvent.ClientTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if (world == null || queue.isEmpty()) return;
		Map<IDataReader, String> cache = new Object2ObjectOpenHashMap<>();
		for (Map.Entry<IDataReader, String> entry : queue.entrySet()) {
			ParseResultEnum result = MessageRegister.parseClient(entry.getKey(), entry.getValue());
			if (result.isRetry()) {
				cache.put(entry.getKey(), entry.getValue());
			}
		}
		queue.clear();
		queue.putAll(cache);
	}
	
}