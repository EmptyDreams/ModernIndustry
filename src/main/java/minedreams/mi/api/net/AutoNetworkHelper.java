package minedreams.mi.api.net;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public final class AutoNetworkHelper {
	
	/**
	 * 拆包，获取玩家名单
	 * @param world 所在世界
	 * @param compound 数据包
	 * @return 返回null表示没有玩家
	 */
	public static Set<EntityPlayerMP> getPlayer(World world, NBTTagCompound compound) {
		int amount = compound.getInteger("playerAmount");
		if (amount <= 0) return null;
		Set<EntityPlayerMP> players = new HashSet<>(amount);
		for (int i = 0; i < amount; ++i) {
			EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(
					compound.getString("player" + i));
			if (player == null) throw new ClassCastException("指定玩家不存在！[" +
					                                                 compound.getString("player" + i) + "]");
			players.add(player);
		}
		return players;
	}
	
}
