package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;
import xyz.emptydreams.mi.blocks.tileentity.user.EUMFurnace;

import javax.annotation.Nonnull;

/**
 * 高温电炉的GUI
 * @author EmptyDreams
 */
public final class EleMFurnaceFrame {

	public static final String NAME = "ele_mfurnace";
	public static final String LOCATION_NAME = "tile.mi.ele_mfurnace.name";
	public static final int WIDTH = 176;
	public static final int HEIGHT = 156;

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(WIDTH, HEIGHT, player);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		@SideOnly(Side.CLIENT)
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(WIDTH, HEIGHT, player);
			init(frame, world, pos, player);
			StaticFrameClient client = new StaticFrameClient(frame, LOCATION_NAME);
			init(client, world, pos, player);
			return client;
		}

		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EUMFurnace firepower = (EUMFurnace) world.getTileEntity(pos);
			frame.init(world);

			Group group = new Group(0, 30, frame.getWidth(), 0, Panels::horizontalCenter);
			group.setMaxDistance(10);
			group.adds(new MSlot(firepower.getInSlot()),
							firepower.getProgressBar(), new MSlot(firepower.getOutSlot()));
			frame.add(group, player);
		}

	});

}
