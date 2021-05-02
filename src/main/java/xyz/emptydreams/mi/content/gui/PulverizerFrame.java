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
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.MSlot;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.content.blocks.tileentity.user.EUPulverizer;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.ModernIndustry.MODID;

/**
 * 粉碎机的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class PulverizerFrame {

	public static final ResourceLocation NAME = new ResourceLocation(MODID, "pulverizer");
	public static final String LOCATION_NAME = "tile.mi.pulverizer.name";

	@SubscribeEvent
	public static void registry(GuiRegistryEvent event) {
		event.registry(NAME, new IContainerCreater() {
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 156, player);
				init(frame, world, pos, player);
				return frame;
			}
			
			@Nonnull
			@Override
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 156, player);
				init(frame, world, pos, player);
				return new StaticFrameClient(frame, LOCATION_NAME);
			}
			
			private void init(IFrame frame, World world, BlockPos pos, EntityPlayer player) {
				EUPulverizer pulverizer = (EUPulverizer) world.getTileEntity(pos);
				frame.init(world);
				
				Group group = new Group(0, 5, frame.getWidth(), 70, Panels::horizontalCenter);
				//noinspection ConstantConditions
				group.adds(new MSlot(pulverizer.getInSlot()),
						pulverizer.getProgress(), new MSlot(pulverizer.getOutSlot()));
				frame.add(group, player);
			}
			
		});
	}

}