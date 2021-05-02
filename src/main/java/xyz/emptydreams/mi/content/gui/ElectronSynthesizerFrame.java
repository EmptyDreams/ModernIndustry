package xyz.emptydreams.mi.content.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.event.GuiRegistryEvent;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.content.blocks.tileentity.user.EUElectronSynthesizer;

import javax.annotation.Nonnull;

/**
 * 电子工作台的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public class ElectronSynthesizerFrame {
	
	public final static ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "electron_synthesizer");
	public final static String LOCATION_NAME = "tile.mi.electron_synthesizer.name";
	
	@SubscribeEvent
	public static void registry(GuiRegistryEvent event) {
		event.registry(NAME, new IContainerCreater() {
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 230, 210, player);
				init(frame, world, pos, player);
				return frame;
			}
			
			@Nonnull
			@Override
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 230, 210, player);
				init(frame, world, pos, player);
				return new StaticFrameClient(frame, LOCATION_NAME);
			}
			
			private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
				EUElectronSynthesizer synthesizer = (EUElectronSynthesizer) world.getTileEntity(pos);
				frame.init(world);
				
				Group backpack = new Group(0, 125, 230, 0, Panels::horizontalCenter);
				Group group = new Group(0, 18, 230, 0, Panels::horizontalCenter);
				//noinspection ConstantConditions
				group.adds(synthesizer.getInput(), synthesizer.getProgress(), synthesizer.getOutput());
				
				frame.add(backpack, player);
				frame.add(group, player);
			}
			
		});
	}
	
}