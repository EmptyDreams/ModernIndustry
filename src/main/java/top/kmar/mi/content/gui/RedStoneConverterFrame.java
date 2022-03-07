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
import top.kmar.mi.content.tileentity.maker.EMRedStoneConverter;

import javax.annotation.Nonnull;

import static top.kmar.mi.ModernIndustry.MODID;

/**
 * 红石能转换器的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public class RedStoneConverterFrame {

	public static final ResourceLocation NAME = new ResourceLocation(MODID, "red_stone_converter");
	public static final String LOCATION_NAME = "tile.mi.red_stone_converter.name";

	@SubscribeEvent
	public static void registry(GuiRegistryEvent event) {
		event.registry(NAME, new IContainerCreater() {
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 161, player);
				init(frame, world, pos);
				return frame;
			}
			
			@Nonnull
			@Override
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, 176, 161, player);
				init(frame, world, pos);
				return new StaticFrameClient(frame, LOCATION_NAME);
			}
			
			private void init(MIFrame frame, World world, BlockPos pos) {
				EMRedStoneConverter converter = (EMRedStoneConverter) world.getTileEntity(pos);
				frame.init(world);
				
				Group group = new Group(0, 12, frame.getWidth(), 70, Panels::verticalCenter);
				//noinspection ConstantConditions
				group.adds(new MSlot(converter.getInput()), converter.getBurnPro(), converter.getEnergyPro());
				group.setMaxDistance(2);
				frame.add(group);
			}
			
		});
	}

}