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
import xyz.emptydreams.mi.blocks.tileentity.user.EUFurnace;

import javax.annotation.Nonnull;

/**
 * 电炉的GUI
 * @author EmptyDreams
 */
public final class EleFurnaceFrame {

	public static final String NAME = "ele_furnace";
	public static final String LOCATION_NAME = "tile.mi.ele_furnace.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(LOCATION_NAME, 176, 156, player);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(LOCATION_NAME, 176, 156, player);
			init(frame, world, pos, player);
			return new StaticFrameClient(frame, LOCATION_NAME);
		}

		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EUFurnace furnace = (EUFurnace) world.getTileEntity(pos);
			frame.init(world);

			Group group = new Group(0, 30, frame.getWidth(), 0, Panels::horizontalCenter);
			group.setMaxDistance(15);
			//noinspection ConstantConditions
			group.add(new MSlot(furnace.getInSlot()));
			group.add(furnace.getProgressBar());
			group.add(new MSlot(furnace.getOutSlot()));
			
			frame.add(group, player);
		}

	});

}