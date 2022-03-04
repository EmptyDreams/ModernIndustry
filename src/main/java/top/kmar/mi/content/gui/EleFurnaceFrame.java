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
import top.kmar.mi.content.tileentity.user.EUFurnace;

import javax.annotation.Nonnull;

import static top.kmar.mi.ModernIndustry.MODID;

/**
 * 电炉的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class EleFurnaceFrame {
	
	public static final ResourceLocation NAME = new ResourceLocation(MODID, "ele_furnace");
	public static final String LOCATION_NAME = "tile.mi.ele_furnace.name";

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
				EUFurnace furnace = (EUFurnace) world.getTileEntity(pos);
				frame.init(world);
				
				Group group = new Group(0, 30, frame.getWidth(), 0, Panels::horizontalCenter);
				group.setMaxDistance(15);
				//noinspection ConstantConditions
				group.add(new MSlot(furnace.getInSlot()));
				group.add(furnace.getProgressBar());
				group.add(new MSlot(furnace.getOutSlot()));
				
				frame.add(group);
			}
			
		});
	}

}