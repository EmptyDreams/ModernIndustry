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
import xyz.emptydreams.mi.content.blocks.tileentity.maker.EMFirePower;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.ModernIndustry.MODID;

/**
 * 火力发电机的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class FirePowerFrame {
	
	public static final ResourceLocation NAME = new ResourceLocation(MODID, "firepower");
	public static final String LOCATION_NAME = "tile.mi.fire_power.name";

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
				EMFirePower firepower = (EMFirePower) world.getTileEntity(pos);
				frame.init(world);
				
				Group up = new Group(0, 0, frame.getWidth(), 18, Panels::horizontalCenter);
				Group group = new Group(0, 12, 0, 70, Panels::verticalCenter);
				up.setMaxDistance(10);
				group.setMaxDistance(8);
				
				//noinspection ConstantConditions
				up.adds(new MSlot(firepower.getInSlot()), firepower.getProgressBar(), new MSlot(firepower.getOutSlot()));
				group.adds(up, firepower.getEnergyProBar());
				frame.add(group, player);
			}
			
		});
	}
	
}