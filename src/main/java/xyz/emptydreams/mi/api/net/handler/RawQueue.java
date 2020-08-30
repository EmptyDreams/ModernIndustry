package xyz.emptydreams.mi.api.newnet.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public final class RawQueue {
	
	private static final List<NBTTagCompound> queue = new LinkedList<>();
	
	public static void add(NBTTagCompound data) {
		queue.add(StringUtil.checkNull(data, "data"));
	}
	
	@SubscribeEvent
	public static void tryToCleanQueue(TickEvent.ClientTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if (world == null || queue.isEmpty()) return;
		for (NBTTagCompound data : queue) {
			boolean sup = MessageRegister.parseClient(data);
			if (!sup) MISysInfo.err("有一个信息没有成功被处理：" + data);
		}
		queue.clear();
	}
	
}
