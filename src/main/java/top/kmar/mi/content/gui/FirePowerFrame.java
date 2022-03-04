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
import top.kmar.mi.content.tileentity.maker.EMFirePower;

import javax.annotation.Nonnull;

import static top.kmar.mi.ModernIndustry.MODID;

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
				EMFirePower firepower = (EMFirePower) world.getTileEntity(pos);
				frame.init(world);
				
				Group up = new Group(0, 0, frame.getWidth(), 18, Panels::horizontalCenter);
				Group group = new Group(0, 12, 0, 70, Panels::verticalCenter);
				up.setMaxDistance(10);
				group.setMaxDistance(8);
				
				//noinspection ConstantConditions
				up.adds(new MSlot(firepower.getInSlot()), firepower.getProgressBar(), new MSlot(firepower.getOutSlot()));
				group.adds(up, firepower.getEnergyProBar());
				frame.add(group);
			}
			
		});
	}
	
}