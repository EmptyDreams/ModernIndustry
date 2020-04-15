package xyz.emptydreams.mi.gui;

import javax.annotation.Nonnull;

import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.gui.IContainerCreater;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.client.MIStaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.MBackpack;
import xyz.emptydreams.mi.api.gui.component.MInput;
import xyz.emptydreams.mi.api.gui.component.MOutput;
import xyz.emptydreams.mi.api.gui.info.TitleModelEnum;
import xyz.emptydreams.mi.blocks.te.user.EUCompressor;
import xyz.emptydreams.mi.register.AutoLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoLoader
public class CompressorFrame extends MIFrame {
	
	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new CompressorFrame();
			init(frame, world, pos, player);
			return frame;
		}
		
		@Nonnull
		@Override
		public MIStaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new CompressorFrame();
			init(frame, world, pos, player);
			MIStaticFrameClient client = new MIStaticFrameClient(frame);
			init(client, world, pos, player);
			return client;
		}
		
		private void init(Object o, World world, BlockPos pos, EntityPlayer player) {
			EUCompressor compressor = (EUCompressor) world.getTileEntity(pos);
			if (o instanceof MIFrame) {
				MIFrame frame = (MIFrame) o;
				frame.init(world);
				frame.setTitle("tile.compressor_tblock.name");
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(8, 84), player);
				frame.add(new MInput(compressor.getSolt(0)), player);
				frame.add(new MInput(compressor.getSolt(1)), player);
				frame.add(new MOutput(compressor.getSolt(2)), player);
				frame.add(compressor.getProgressBar(), player);
			} else {
				MIStaticFrameClient frame = (MIStaticFrameClient) o;
				frame.setTitle("tile.compressor_tblock.name");
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(8, 84));
				frame.add(new MInput(compressor.getSolt(0)));
				frame.add(new MInput(compressor.getSolt(1)));
				frame.add(new MOutput(compressor.getSolt(2)));
				frame.add(compressor.getProgressBar());
			}
		}
		
	});
	
	public CompressorFrame() {
		super(null, "compressor", 176, 166);
	}
	
}
