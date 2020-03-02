package minedreams.mi.api.net;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface INetworkHelper {
	
	BlockPos getBlockPos();
	
	Iterable<EntityPlayerMP> getPlayers();
	
}
