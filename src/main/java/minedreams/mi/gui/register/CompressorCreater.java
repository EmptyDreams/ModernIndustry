package minedreams.mi.gui.register;

import javax.annotation.Nonnull;

import minedreams.mi.api.gui.IContainerCreater;
import minedreams.mi.api.gui.MIFrame;
import minedreams.mi.api.gui.client.MIFrameClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class CompressorCreater implements IContainerCreater {
	
	@Override
	@Nonnull
	public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
		return new MIFrame(176, 166);
	}
	
	@Override
	@Nonnull
	public MIFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
		return new MIFrameClient(new MIFrame(176, 166));
	}
	
}
