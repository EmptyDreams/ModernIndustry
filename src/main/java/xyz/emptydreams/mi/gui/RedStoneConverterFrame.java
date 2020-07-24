package xyz.emptydreams.mi.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.gui.IContainerCreater;
import xyz.emptydreams.mi.api.gui.MIFrame;
import xyz.emptydreams.mi.api.gui.TitleModelEnum;
import xyz.emptydreams.mi.api.gui.client.MIStaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.MBackpack;
import xyz.emptydreams.mi.api.gui.component.MInput;
import xyz.emptydreams.mi.blocks.te.maker.EMRedStoneConverter;

import javax.annotation.Nonnull;

/**
 * 红石能转换器的GUI
 * @author EmptyDreams
 */
public class RedStoneConverterFrame {

	public static final String NAME = "red_stone_converter";
	public static final String LOCATION_NAME = "tile.red_stone_converter.name";

	public static final int ID = GuiLoader.register(new IContainerCreater() {
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, NAME, 176, 161);
			init(frame, world, pos, player);
			return frame;
		}

		@Nonnull
		@Override
		public MIStaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			MIFrame frame = new MIFrame(ModernIndustry.MODID, NAME, 176, 161);
			init(frame, world, pos, player);
			MIStaticFrameClient client = new MIStaticFrameClient(frame);
			init(client, world, pos, player);
			return client;
		}

		private void init(Object o, World world, BlockPos pos, EntityPlayer player) {
			EMRedStoneConverter firepower = (EMRedStoneConverter) world.getTileEntity(pos);
			if (o instanceof MIFrame) {
				MIFrame frame = (MIFrame) o;
				frame.init(world);
				frame.setTitle(LOCATION_NAME);
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(6, 77), player);
				frame.add(new MInput(firepower.getInput()), player);
				frame.add(firepower.getEnergyPro(), player);
				frame.add(firepower.getBurnPro(), player);
				frame.add(firepower.getStringShower(), player);
			} else {
				MIStaticFrameClient frame = (MIStaticFrameClient) o;
				frame.setTitle(LOCATION_NAME);
				frame.setTitleModel(TitleModelEnum.CENTRAL);
				frame.add(new MBackpack(6, 77));
				frame.add(new MInput(firepower.getInput()));
				frame.add(firepower.getEnergyPro());
				frame.add(firepower.getBurnPro());
				frame.add(firepower.getStringShower());
			}
		}

	});

}
