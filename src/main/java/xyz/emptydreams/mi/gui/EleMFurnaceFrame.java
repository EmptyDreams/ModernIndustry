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
import xyz.emptydreams.mi.blocks.te.user.EUMFurnace;

import javax.annotation.Nonnull;

/**
 * 高温电炉的GUI
 * @author EmptyDreams
 */
public final class EleMFurnaceFrame {

	public static final String NAME = "ele_mfurnace";
	public static final String LOCATION_NAME = "tile.ele_mfurnace.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, NAME, 176, 156);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		public MIStaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, NAME, 176, 156);
			init(frame, world, pos, player);
			MIStaticFrameClient client = new MIStaticFrameClient(frame);
			init(client, world, pos, player);
			return client;
		}

		private void init(Object o, World world, BlockPos pos, EntityPlayer player) {
			EUMFurnace firepower = (EUMFurnace) world.getTileEntity(pos);
			if (o instanceof MIFrame) {
				MIFrame frame = (MIFrame) o;
				frame.init(world);
				frame.setTitle(LOCATION_NAME);
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(6, 72), player);
				frame.add(new MInput(firepower.getInSlot()), player);
				frame.add(new MInput(firepower.getOutSlot()), player);
				frame.add(firepower.getProgressBar(), player);
			} else {
				MIStaticFrameClient frame = (MIStaticFrameClient) o;
				frame.setTitle(LOCATION_NAME);
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(6, 72));
				frame.add(new MInput(firepower.getInSlot()));
				frame.add(new MInput(firepower.getOutSlot()));
				frame.add(firepower.getProgressBar());
			}
		}

	});

}
