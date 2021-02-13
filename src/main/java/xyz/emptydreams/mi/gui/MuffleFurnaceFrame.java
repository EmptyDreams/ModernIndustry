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
import xyz.emptydreams.mi.blocks.tileentity.user.MuffleFurnace;

import javax.annotation.Nonnull;

/**
 * 高温熔炉的GUI
 * @author EmptyDreams
 */
public class MuffleFurnaceFrame {

	public static final String NAME = "muffleFurnace";
	public static final String LOCATION_NAME = "tile.mi.muffle_furnace.name";

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
			MuffleFurnace furnace = (MuffleFurnace) world.getTileEntity(pos);
			frame.init(world);

			Group left = new Group(0, 0, 18, 70, Panels::verticalCenter);
			Group group = new Group(0, 10, frame.getWidth(), 0, Panels::horizontalCenter);
			left.setMaxDistance(5);
			//noinspection ConstantConditions
			left.adds(new MSlot(furnace.getUp()), furnace.getBurnProgress(), new MSlot(furnace.getDown()));
			group.adds(left, furnace.getWorkProgress(), new MSlot(furnace.getOut()));
			frame.add(group, player);
		}
		
	});
	
}