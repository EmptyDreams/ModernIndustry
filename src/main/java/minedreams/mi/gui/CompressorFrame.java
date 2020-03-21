package minedreams.mi.gui;

import javax.annotation.Nonnull;

import minedreams.mi.api.gui.GuiLoader;
import minedreams.mi.api.gui.IContainerCreater;
import minedreams.mi.api.gui.MIFrame;
import minedreams.mi.api.gui.client.MIFrameClient;
import minedreams.mi.api.gui.info.TitleModelEnum;
import minedreams.mi.register.AutoLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoLoader
public class CompressorFrame extends MIFrame {
	
	public static final int ID;
	
	static {
		ID = GuiLoader.register(new IContainerCreater() {
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				return new CompressorFrame();
			}
			
			@Nonnull
			@Override
			public MIFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrameClient client = new MIFrameClient(new MIFrame(176, 166));
				client.setTitle("tile.compressor_tblock.name");
				client.setTitleModel(TitleModelEnum.CENTRAL);
				return client;
			}
		});
	}
	
	public CompressorFrame() {
		super(176, 166);
	}
}
