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
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;
import xyz.emptydreams.mi.blocks.tileentity.user.EUPulverizer;

import javax.annotation.Nonnull;

/**
 * 粉碎机的GUI
 * @author EmptyDreams
 */
public final class PulverizerFrame {

	public static final String NAME = "pulverizer";
	public static final String LOCATION_NAME = "tile.mi.pulverizer.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(176, 156, player);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(176, 156, player);
			init(frame, world, pos, player);
			return new StaticFrameClient(frame, LOCATION_NAME);
		}

		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EUPulverizer pulverizer = (EUPulverizer) world.getTileEntity(pos);
			frame.init(world);

			Group group = new Group(0, 5, frame.getWidth(), 70, Panels::horizontalCenter);
			group.adds(new MSlot(pulverizer.getInSlot()),
						pulverizer.getProgress(), new MSlot(pulverizer.getOutSlot()));
			frame.add(group, player);
		}

	});

}
