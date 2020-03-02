package minedreams.mi.api.net;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IAutoNetwork<T extends IMessage & INetworkHelper> {
	
	T send();
	
	void reveive(@Nonnull T message);
	
	World getWorld();
	
	BlockPos getPos();
	
	default void _reveive(@Nonnull IMessage message) {
		reveive((T) message);
	}
	
}
