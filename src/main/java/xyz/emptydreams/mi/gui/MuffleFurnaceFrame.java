package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.gui.IContainerCreater;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.TitleModelEnum;
import xyz.emptydreams.mi.api.gui.client.MIStaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.MBackpack;
import xyz.emptydreams.mi.api.gui.component.MInput;
import xyz.emptydreams.mi.blocks.te.user.MuffleFurnace;

import javax.annotation.Nonnull;

/**
 * 高温熔炉的GUI
 * @author EmptyDreams
 */
public class MuffleFurnaceFrame {

	public static final String NAME = "muffleFurnace";
	public static final String LOCATION_NAME = "tile.muffle_furnace.name";

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
		
		private void init(Object o, World world, BlockPos pos, EntityPlayer player) {
			MuffleFurnace furnace = (MuffleFurnace) world.getTileEntity(pos);
			if (o instanceof MIFrame) {
				MIFrame frame = (MIFrame) o;
				frame.init(world);
				frame.setTitle(LOCATION_NAME);
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(7, 83), player);
				frame.add(new MInput(furnace.getUp()), player);
				frame.add(new MInput(furnace.getDown()), player);
				frame.add(new MInput(furnace.getOut()), player);
				frame.add(furnace.getWorkProgress(), player);
				frame.add(furnace.getBurnProgress(), player);
			} else {
				MIStaticFrameClient frame = (MIStaticFrameClient) o;
				frame.setTitle(LOCATION_NAME);
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(7, 83));
				frame.add(new MInput(furnace.getUp()));
				frame.add(new MInput(furnace.getDown()));
				frame.add(new MInput(furnace.getOut()));
				frame.add(furnace.getWorkProgress());
				frame.add(furnace.getBurnProgress());
			}
		}
		
	});
	
}
