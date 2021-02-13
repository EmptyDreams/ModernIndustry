package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.blocks.tileentity.user.EUCompressor;

import javax.annotation.Nonnull;

/**
 * 压缩机的GUI
 * @author EmptyDreams
 */
public final class CompressorFrame {

	public static final String NAME = "compressor";
	public static final String LOCATION_NAME = "tile.mi.compressor.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(LOCATION_NAME, 176, 166, player);
			init(frame, world, pos, player);
			return frame;
		}
		
		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(LOCATION_NAME, 176, 166, player);
			init(frame, world, pos, player);
			return new StaticFrameClient(frame, LOCATION_NAME);
		}
		
		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EUCompressor compressor = (EUCompressor) world.getTileEntity(pos);
			frame.init(world);
			
			Group fir = new Group(0, 0, 18, 54, Panels::verticalCenter);
			Group group = new Group(0, 15, frame.getWidth(), 0, Panels::horizontalCenter);
			group.setMaxDistance(10);
			
			//noinspection ConstantConditions
			fir.adds(new MSlot(compressor.getSlot(0)), new MSlot(compressor.getSlot(1)));
			group.adds(fir, compressor.getProgressBar(), new MSlot(compressor.getSlot(2)));
			frame.add(group, player);
		}
		
	});
	
}