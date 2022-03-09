package top.kmar.mi.api.utils;

import net.minecraft.tileentity.TileEntity;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.net.handler.MessageSender;
import top.kmar.mi.api.net.message.block.BlockAddition;
import top.kmar.mi.api.net.message.block.BlockMessage;
import top.kmar.mi.api.utils.data.math.Point3D;
import top.kmar.mi.api.utils.data.math.Range3D;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author EmptyDreams
 */
public final class IOUtil {
	
	public static void sendBlockMessageIfNotUpdate(
			TileEntity te, Collection<UUID> players, int r, Supplier<IDataReader> readerSupplier) {
		Range3D netRange = new Range3D(te.getPos(), r);
		MessageSender.sendToClientIf(te.getWorld(),  player -> {
			if (players.contains(player.getUniqueID()) || !netRange.isIn(new Point3D(player))) return false;
			players.add(player.getUniqueID());
			return true;
		}, () ->
				BlockMessage.instance().create(readerSupplier.get(), new BlockAddition(te)));
	}

}