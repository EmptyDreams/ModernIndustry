package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.gui.IContainerCreater;
import xyz.emptydreams.mi.api.gui.IFrame;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;
import xyz.emptydreams.mi.blocks.tileentity.maker.EMFirePower;

import javax.annotation.Nonnull;

/**
 * 火力发电机的GUI
 * @author EmptyDreams
 */
public final class FirePowerFrame {

	public static final String NAME = "firepower";
	public static final String LOCATION_NAME = "tile.mi.fire_power.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(176, 156, player, 6, 72);
			init(frame, world, pos, player);
			return frame;
		}
		
		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(176, 156, player, 6, 72);
			init(frame, world, pos, player);
			StaticFrameClient client = new StaticFrameClient(frame, LOCATION_NAME);
			init(client, world, pos, player);
			return client;
		}
		
		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EMFirePower firepower = (EMFirePower) world.getTileEntity(pos);
			frame.init(world);

			Group up = new Group(0, 0, frame.getWidth(), 18, Panels::horizontalCenter);
			Group group = new Group(0, 12, 0, 70, Panels::verticalCenter);
			up.setMaxDistance(10);
			group.setMaxDistance(8);

			up.adds(new MSlot(firepower.getInSlot()), firepower.getProgressBar(), new MSlot(firepower.getOutSlot()));
			group.adds(up, firepower.getEnergyProBar());
			frame.add(group, player);
		}
		
	});
	
}
