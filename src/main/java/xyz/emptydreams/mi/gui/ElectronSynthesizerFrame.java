package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.gui.IContainerCreater;
import xyz.emptydreams.mi.api.gui.IFrame;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;
import xyz.emptydreams.mi.blocks.tileentity.user.EUElectronSynthesizer;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public class ElectronSynthesizerFrame {
	
	public static String NAME = "electron_synthesizer";
	public static String LOCATION_NAME = "tile.mi.electron_synthesizer.name";
	
	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(230, 210, player);
			init(frame, world, pos, player);
			return frame;
		}
		
		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(230, 210, player);
			init(frame, world, pos, player);
			StaticFrameClient client = new StaticFrameClient(frame, LOCATION_NAME);
			init(client, world, pos, player);
			return client;
		}
		
		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EUElectronSynthesizer synthesizer = (EUElectronSynthesizer) world.getTileEntity(pos);
			frame.init(world);
			
			Group backpack = new Group(0, 125, 230, 0, Panels::horizontalCenter);
			Group group = new Group(0, 18, 230, 0, Panels::horizontalCenter);
			group.adds(synthesizer.getInput(), synthesizer.getProgress(), synthesizer.getOutput());
			
			frame.add(backpack, player);
			frame.add(group, player);
		}
		
	});
	
}
