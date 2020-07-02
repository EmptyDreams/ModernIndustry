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
import xyz.emptydreams.mi.blocks.te.user.EUFurnace;
import xyz.emptydreams.mi.register.AutoLoader;

import javax.annotation.Nonnull;

/**
 * 电炉的GUI
 * @author EmptyDreams
 * @version V1.0
 */
@AutoLoader
public final class EleFurnaceFrame {

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, "ele_furnace", 176, 156);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		public MIStaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, "ele_furnace", 176, 156);
			init(frame, world, pos, player);
			MIStaticFrameClient client = new MIStaticFrameClient(frame);
			init(client, world, pos, player);
			return client;
		}

		private void init(Object o, World world, BlockPos pos, EntityPlayer player) {
			EUFurnace firepower = (EUFurnace) world.getTileEntity(pos);
			if (o instanceof MIFrame) {
				MIFrame frame = (MIFrame) o;
				frame.init(world);
				frame.setTitle("tile.ele_furnace.name");
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(6, 72), player);
				frame.add(new MInput(firepower.getInSlot()), player);
				frame.add(new MInput(firepower.getOutSlot()), player);
				frame.add(firepower.getProgressBar(), player);
			} else {
				MIStaticFrameClient frame = (MIStaticFrameClient) o;
				frame.setTitle("tile.ele_furnace.name");
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(6, 72));
				frame.add(new MInput(firepower.getInSlot()));
				frame.add(new MInput(firepower.getOutSlot()));
				frame.add(firepower.getProgressBar());
			}
		}

	});

}
