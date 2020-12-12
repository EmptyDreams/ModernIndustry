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
import xyz.emptydreams.mi.blocks.tileentity.maker.EMRedStoneConverter;

import javax.annotation.Nonnull;

/**
 * 红石能转换器的GUI
 * @author EmptyDreams
 */
public class RedStoneConverterFrame {

	public static final String NAME = "red_stone_converter";
	public static final String LOCATION_NAME = "tile.mi.red_stone_converter.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(LOCATION_NAME, 176, 161, player);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(LOCATION_NAME, 176, 161, player);
			init(frame, world, pos, player);
			return new StaticFrameClient(frame, LOCATION_NAME);
		}

		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EMRedStoneConverter converter = (EMRedStoneConverter) world.getTileEntity(pos);
			frame.init(world);

			Group group = new Group(0, 12, frame.getWidth(), 70, Panels::verticalCenter);
			group.adds(new MSlot(converter.getInput()), converter.getBurnPro(), converter.getEnergyPro());
			group.setMaxDistance(2);
			frame.add(group, player);
		}

	});

}
