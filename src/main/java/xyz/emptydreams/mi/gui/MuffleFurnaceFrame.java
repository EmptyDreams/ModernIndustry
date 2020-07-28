package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.gui.IContainerCreater;
import xyz.emptydreams.mi.api.gui.IFrame;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.TitleModelEnum;
import xyz.emptydreams.mi.api.gui.client.MIStaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.MBackpack;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.group.Group;
import xyz.emptydreams.mi.api.gui.group.Panels;
import xyz.emptydreams.mi.blocks.te.user.MuffleFurnace;

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
			MIFrame frame = new MIFrame(ModernIndustry.MODID, NAME, 176, 166);
			init(frame, world, pos, player);
			return frame;
		}
		
		@Nonnull
		@Override
		public MIStaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, NAME, 176, 166);
			init(frame, world, pos, player);
			MIStaticFrameClient client = new MIStaticFrameClient(frame);
			init(client, world, pos, player);
			return client;
		}
		
		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			MuffleFurnace furnace = (MuffleFurnace) world.getTileEntity(pos);
			frame.init(world);
			frame.setTitle(LOCATION_NAME);
			frame.setTitleModel(TitleModelEnum.CENTRAL);
			frame.add(new MBackpack(7, 83), player);

			Group left = new Group(0, 0, 18, 70, Panels::verticalCenter);
			Group group = new Group(0, 10, frame.getWidth(), 0, Panels::horizontalCenter);
			left.setMaxDistance(5);
			left.adds(new MSlot(furnace.getUp()), furnace.getBurnProgress(), new MSlot(furnace.getDown()));
			group.adds(left, furnace.getWorkProgress(), new MSlot(furnace.getOut()));
			frame.add(group, player);
		}
		
	});
	
}
