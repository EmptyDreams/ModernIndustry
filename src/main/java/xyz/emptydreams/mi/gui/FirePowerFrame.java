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
import xyz.emptydreams.mi.blocks.te.maker.EMFirePower;

import javax.annotation.Nonnull;

/**
 * 火力发电机的GUI
 * @author EmptyDreams
 */
public final class FirePowerFrame {

	public static final String NAME = "firepower";
	public static final String LOCATION_NAME = "tile.fire_power.name";

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
		
		private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
			EMFirePower firepower = (EMFirePower) world.getTileEntity(pos);
			frame.init(world);
			frame.setTitle(LOCATION_NAME);
			frame.setTitleModel(TitleModelEnum.CENTRAL);
			frame.add(new MBackpack(6, 72), player);
			frame.add(new MSlot(firepower.getInSlot()), player);
			frame.add(new MSlot(firepower.getOutSlot()), player);
			frame.add(firepower.getProgressBar(), player);
			frame.add(firepower.getEnergyProBar(), player);
			frame.add(firepower.getStringShower(), player);
		}
		
	});
	
}
