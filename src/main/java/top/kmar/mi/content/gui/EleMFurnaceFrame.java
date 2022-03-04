package top.kmar.mi.content.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.event.GuiRegistryEvent;
import top.kmar.mi.api.gui.client.StaticFrameClient;
import top.kmar.mi.api.gui.common.IContainerCreater;
import top.kmar.mi.api.gui.common.MIFrame;
import top.kmar.mi.api.gui.component.MSlot;
import top.kmar.mi.api.gui.component.group.Group;
import top.kmar.mi.api.gui.component.group.Panels;
import top.kmar.mi.content.tileentity.user.EUMFurnace;

import javax.annotation.Nonnull;

import static top.kmar.mi.ModernIndustry.MODID;

/**
 * 高温电炉的GUI
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class EleMFurnaceFrame {

	public static final ResourceLocation NAME = new ResourceLocation(MODID, "ele_mfurnace");
	public static final String LOCATION_NAME = "tile.mi.ele_mfurnace.name";
	public static final int WIDTH = 176;
	public static final int HEIGHT = 156;

	@SubscribeEvent
	public static void registry(GuiRegistryEvent event) {
		event.registry(NAME, new IContainerCreater() {
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, WIDTH, HEIGHT, player);
				init(frame, world, pos);
				return frame;
			}
			
			@Nonnull
			@Override
			@SideOnly(Side.CLIENT)
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				MIFrame frame = new MIFrame(LOCATION_NAME, WIDTH, HEIGHT, player);
				init(frame, world, pos);
				return new StaticFrameClient(frame, LOCATION_NAME);
			}
			
			private void init(MIFrame frame, World world, BlockPos pos) {
				EUMFurnace firepower = (EUMFurnace) world.getTileEntity(pos);
				frame.init(world);
				
				Group group = new Group(0, 30, frame.getWidth(), 0, Panels::horizontalCenter);
				group.setMaxDistance(10);
				//noinspection ConstantConditions
				group.adds(new MSlot(firepower.getInSlot()),
						firepower.getProgressBar(), new MSlot(firepower.getOutSlot()));
				frame.add(group);
			}
			
		});
	}

}