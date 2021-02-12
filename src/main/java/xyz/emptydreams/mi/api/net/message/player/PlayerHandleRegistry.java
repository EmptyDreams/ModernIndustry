package xyz.emptydreams.mi.api.net.message.player;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.Map;

/**
 * {@link PlayerHandle}的注册机，<b>类必须具有无参构造函数（可私有）</b>
 * @author EmptyDreams
 */
public final class PlayerHandleRegistry {
	
	private static final Map<ResourceLocation, PlayerHandle> INSTANCES = new Object2ObjectArrayMap<>();
	
	/**
	 * 注册一个Handle
	 * @param key 钥匙
	 * @param handle 处理类
	 */
	public static void registry(ResourceLocation key, PlayerHandle handle) {
		StringUtil.checkNull(key, "key");
		StringUtil.checkNull(handle, "handle");
		INSTANCES.put(key, handle);
	}
	
	/**
	 * 处理一个数据
	 * @param key 钥匙
	 * @param player 玩家
	 * @param data 数据
	 * @return 是否处理成功
	 */
	public static boolean apply(ResourceLocation key, EntityPlayer player, NBTTagCompound data) {
		PlayerHandle handle = INSTANCES.getOrDefault(key, null);
		if (handle == null) {
			return false;
		}
		handle.apply(player, data);
		return true;
	}
	
}