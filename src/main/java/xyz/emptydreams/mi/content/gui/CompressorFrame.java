package xyz.emptydreams.mi.content.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.event.GuiRegistryEvent;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.content.blocks.tileentity.user.EUCompressor;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.ModernIndustry.MODID;

/**
 * 压缩机的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class CompressorFrame {
	
	public static final ResourceLocation NAME = new ResourceLocation(MODID, "compressor");
	public static final String LOCATION_NAME = "tile.mi.compressor.name";

	@SubscribeEvent
	public static void registry(GuiRegistryEvent event) {
		event.registry(NAME, new IContainerCreater() {
			
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 166, player);
				init(frame, world, pos);
				return frame;
			}
			
			@Nonnull
			@Override
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 166, player);
				init(frame, world, pos);
				return new StaticFrameClient(frame, LOCATION_NAME);
			}
			
			private void init(MIFrame frame, World world, BlockPos pos) {
				EUCompressor compressor = (EUCompressor) world.getTileEntity(pos);
				frame.init(world);
				
				Group fir = new Group(0, 0, 18, 54, Panels::verticalCenter);
				Group group = new Group(0, 15, frame.getWidth(), 0, Panels::horizontalCenter);
				group.setMaxDistance(10);
				
				//noinspection ConstantConditions
				fir.adds(new MSlot(compressor.getSlot(0)), new MSlot(compressor.getSlot(1)));
				group.adds(fir, compressor.getProgressBar(), new MSlot(compressor.getSlot(2)));
				frame.add(group);
			}
			
		});
	}
	
}