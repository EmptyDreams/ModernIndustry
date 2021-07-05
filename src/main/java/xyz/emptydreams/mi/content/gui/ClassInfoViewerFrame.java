package xyz.emptydreams.mi.content.gui;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.event.GuiRegistryEvent;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.api.gui.component.group.RollGroup;
import xyz.emptydreams.mi.api.gui.component.group.TitleGroup;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static xyz.emptydreams.mi.ModernIndustry.MODID;

@Mod.EventBusSubscriber
public class ClassInfoViewerFrame extends MIFrame {
	
	public static final ResourceLocation NAME = new ResourceLocation(MODID, "classInfoViewerFrame");
	public static final String LOCATION_NAME = "item.mi.class_info_viewer.name";
	
	@SubscribeEvent
	public static void registry(GuiRegistryEvent event) {
		event.registry(NAME, new IContainerCreater() {
			@Nonnull
			@Override
			public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
				TileEntity te = world.getTileEntity(pos);
				if (te == null) throw new NullPointerException("指定位置" + pos + "不存在TileEntity");
				return new ClassInfoViewerFrame(te, player);
			}
			
			@Nonnull
			@Override
			public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
				Block block = world.getBlockState(pos).getBlock();
				String name = I18n.format("tile." + block.getLocalizedName() + ".name");
				String gui = I18n.format("tile.mi.class_info_viewer.name");
				String title = gui + "@" + name;
				return new StaticFrameClient(createService(world, player, pos), title);
			}
		});
	}
	
	public ClassInfoViewerFrame(TileEntity entity, EntityPlayer player) {
		super(LOCATION_NAME, player);
		setSize(176, 166);
		try {
			RollGroup rollGroup = new RollGroup(RollGroup.HorizontalEnum.NON, RollGroup.VerticalEnum.RIGHT);
			rollGroup.setControlPanel(Panels::verticalCenter);
			Class<?> clazz = entity.getClass();
			int height = 10;
			int width = 0;
			while (isContinue(clazz)) {
				Group inner = createGroupHeight(clazz, entity);
				clazz = clazz.getSuperclass();
				if (inner == null) continue;
				height += inner.getHeight();
				width = Math.max(width, inner.getWidth());
				rollGroup.add(inner);
			}
			width += rollGroup.getVerRollWidth() + 5;
			rollGroup.setSize(width, height);
			add(rollGroup);
		} catch (Exception e) {
			throw new TransferException("创建类信息查看GUI时出现异常", e);
		}
	}
	
	private static boolean isContinue(Class<?> clazz) {
		return clazz != TileEntity.class && clazz != BaseTileEntity.class;
	}
	
	private static Group createGroupHeight(Class<?> clazz, Object obj) throws IllegalAccessException {
		Group result = new TitleGroup(clazz.getSimpleName());
		result.setControlPanel(Panels::horizontalCenter);
		int height = 15;
		int width = 0;
		Group nameGroup = new Group(0, 0, 0, 0, Panels::verticalCenter);
		Group valueGroup = new Group(0, 0, 0, 0, Panels::verticalCenter);
		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
			height += 12;
			String name = field.getName();
			String value = String.valueOf(field.get(obj));
			int color = getStringColor(field);
			StringComponent nameShower = new StringComponent(name);
			nameShower.setColor(color);
			StringComponent valueShower = new StringComponent(value);
			valueShower.setColor(color);
			nameGroup.add(nameShower);
			valueGroup.add(valueShower);
			width = Math.max(nameShower.getWidth() + valueShower.getWidth() + 10, width);
		}
		if (width == 0) return null;
		result.setSize(width, height);
		result.adds(nameGroup, valueGroup);
		return result;
	}
	
	private static int getStringColor(Field field) {
		int mod = field.getModifiers();
		if (Modifier.isStatic(mod)) return Modifier.isFinal(mod) ? 8388352 : 8087790;
		else return Modifier.isFinal(mod) ? 25600 : 0;
	}
	
}