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
import xyz.emptydreams.mi.register.AutoLoader;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoLoader
public class MuffleFuranceFrame {
	
	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, "muffleFurnace", 176, 166);
			init(frame, world, pos, player);
			return frame;
		}
		
		@Nonnull
		@Override
		public MIStaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, "firepower", 176, 166);
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
				frame.setTitle("tile.muffle_furnace.name");
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(7, 83), player);
				frame.add(new MInput(furnace.getUp()), player);
				frame.add(new MInput(furnace.getDown()), player);
				frame.add(new MInput(furnace.getOut()), player);
				frame.add(furnace.getWorkProgress(), player);
				frame.add(furnace.getBurnProgress(), player);
			} else {
				MIStaticFrameClient frame = (MIStaticFrameClient) o;
				frame.setTitle("tile.muffle_furnace.name");
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
