package top.kmar.mi.content.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.kmar.mi.api.event.GuiRegistryEvent;
import top.kmar.mi.api.gui.client.StaticFrameClient;
import top.kmar.mi.api.gui.common.IContainerCreater;
import top.kmar.mi.api.gui.common.MIFrame;
import top.kmar.mi.api.gui.component.MSlot;
import top.kmar.mi.api.gui.component.group.Group;
import top.kmar.mi.api.gui.component.group.Panels;
import top.kmar.mi.content.tileentity.user.EUPulverizer;

import javax.annotation.Nonnull;

import static top.kmar.mi.ModernIndustry.MODID;

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
				init(frame, world, pos);
				return frame;
			}
			
			@Nonnull
			@Override
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 156, player);
				init(frame, world, pos);
				return new StaticFrameClient(frame, LOCATION_NAME);
			}
			
			private void init(MIFrame frame, World world, BlockPos pos) {
				EUPulverizer pulverizer = (EUPulverizer) world.getTileEntity(pos);
				frame.init(world);
				
				Group group = new Group(0, 5, frame.getWidth(), 70, Panels::horizontalCenter);
				//noinspection ConstantConditions
				group.adds(new MSlot(pulverizer.getInSlot()),
						pulverizer.getProgress(), new MSlot(pulverizer.getOutSlot()));
				frame.add(group);
			}
			
		});
	}

}