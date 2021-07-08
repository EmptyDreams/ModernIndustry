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
				String name = I18n.format( block.getLocalizedName());
				String gui = I18n.format(LOCATION_NAME);
				String title = gui + "@" + name;
				return new StaticFrameClient(createService(world, player, pos), title);
			}
		});
	}
	
	public ClassInfoViewerFrame(TileEntity entity, EntityPlayer player) {
		super(LOCATION_NAME, player);
		setSize(200, 170);
		try {
			RollGroup rollGroup = new RollGroup(RollGroup.HorizontalEnum.UP, RollGroup.VerticalEnum.RIGHT);
			rollGroup.setControlPanel(Panels::horizontalCenter);
			rollGroup.setSize(180, 150);
			rollGroup.setLocation(0, 14);
			Class<?> clazz = entity.getClass();
			Group nameGroup = new Group(Panels::verticalCenter);
			Group valueGroup = new Group(Panels::verticalCenter);
			while (isContinue(clazz)) {
				Field[] fields = clazz.getDeclaredFields();
				clazz = clazz.getSuperclass();
				if (fields.length == 0) continue;
				task(nameGroup, valueGroup, fields, entity);
			}
			rollGroup.adds(nameGroup, valueGroup);
			add(rollGroup);
		} catch (Exception e) {
			throw TransferException.instance("创建类信息查看GUI时出现异常", e);
		}
	}
	
	private static void task(Group nameGroup, Group valueGroup, Field[] fields, Object obj)
			throws IllegalAccessException {
		for (Field field : fields) {
			if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
			int color = getStringColor(field);
			StringComponent name = new StringComponent(field.getName());
			StringComponent value = new StringComponent(String.valueOf(field.get(obj)));
			name.setColor(color);
			value.setColor(color);
			nameGroup.add(name);
			valueGroup.add(value);
		}
	}
	
	private static boolean isContinue(Class<?> clazz) {
		return clazz != TileEntity.class && clazz != BaseTileEntity.class;
	}
	
	private static int getStringColor(Field field) {
		int mod = field.getModifiers();
		if (Modifier.isStatic(mod)) return Modifier.isFinal(mod) ? 8388352 : 8087790;
		else return Modifier.isFinal(mod) ? 25600 : 0;
	}
	
}