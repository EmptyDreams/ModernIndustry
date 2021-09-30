package xyz.emptydreams.mi.content.gui;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.registries.IForgeRegistryEntry;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.event.GuiRegistryEvent;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.gui.component.group.Group;
import xyz.emptydreams.mi.api.gui.component.group.Panels;
import xyz.emptydreams.mi.api.gui.component.group.RollGroup;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.player.PlayerAddition;
import xyz.emptydreams.mi.api.net.message.player.PlayerMessage;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.tools.FrontTileEntity;
import xyz.emptydreams.mi.content.items.debug.DebugDetails;
import xyz.emptydreams.mi.content.net.ClassInfoViewerMessage;

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
				sendToClient(player, te);
				if (player.world.isRemote) {
					MIFrame result = new ClassInfoViewerFrame(ClassInfoViewerMessage.getTileEntity(), player);
					ClassInfoViewerMessage.unUpdate();
					return result;
				}
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
	
	private static void sendToClient(EntityPlayer player, TileEntity te) {
		if (te.getWorld().isRemote) return;
		PlayerAddition addition = new PlayerAddition(player,
				new ResourceLocation(ModernIndustry.MODID, "ClassInfoViewerMessage"));
		ByteDataOperator operator = new ByteDataOperator();
		operator.writeBlockPos(te.getPos());
		NBTTagCompound data = te.writeToNBT(new NBTTagCompound());
		operator.writeTag(data);
		IMessage message = PlayerMessage.instance().create(operator, addition);
		MessageSender.sendToServer(message);
	}
	
	public ClassInfoViewerFrame(TileEntity entity, EntityPlayer player) {
		super(LOCATION_NAME, player);
		setSize(210, 200);
		
		RollGroup serviceRoll = new RollGroup(RollGroup.HorizontalEnum.UP, RollGroup.VerticalEnum.RIGHT);
		RollGroup clientRoll = new RollGroup(RollGroup.HorizontalEnum.UP, RollGroup.VerticalEnum.RIGHT);
		TileEntity clientTE = player.world.getTileEntity(entity.getPos());
		//noinspection ConstantConditions
		init(clientRoll, clientTE);
		init(serviceRoll, entity);
		add(serviceRoll);
	}
	
	private void init(RollGroup rollGroup, TileEntity te) {
		rollGroup.setControlPanel(Panels::horizontalUp);
		rollGroup.setMinDistance(6);
		rollGroup.setSize(185, 150);
		Class<?> clazz = te.getClass();
		Group nameGroup = new Group(Panels::verticalRight);
		Group valueGroup = new Group(Panels::verticalLeft);
		try {
			addText(nameGroup, valueGroup, "pos", te.getPos().toString(), 8388352);
			while (isContinue(clazz)) {
				Field[] fields = clazz.getDeclaredFields();
				String className = clazz.getSimpleName();
				clazz = clazz.getSuperclass();
				if (fields.length == 0) continue;
				boundary(nameGroup, valueGroup, className);
				task(nameGroup, valueGroup, fields, te);
			}
		} catch (Exception e) {
			throw TransferException.instance("创建类信息查看GUI时出现异常", e);
		}
		rollGroup.adds(nameGroup, valueGroup);
	}
	
	private static void task(Group nameGroup, Group valueGroup, Field[] fields, Object obj)
			throws IllegalAccessException {
		for (Field field : fields) {
			task(nameGroup, valueGroup, field, obj);
		}
	}
	
	private static void task(Group nameGroup, Group valueGroup, Field field, Object obj)
			throws IllegalAccessException {
		String nameText = field.getName();
		if (nameText.contains("$")) return;
		if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
		Class<?> clazz = field.getType();
		Object details = field.get(obj);
		if (clazz.isAnnotationPresent(DebugDetails.class)) {
			task(nameGroup, valueGroup, clazz.getDeclaredFields(), details);
			return;
		}
		int color = getStringColor(field);
		addText(nameGroup, valueGroup, nameText, getValue(details), color);
	}
	
	private static void addText(Group nameGroup, Group valueGroup, String name, String value, int color) {
		StringComponent nameComponent = new StringComponent(name);
		StringComponent valueComponent = new StringComponent(value);
		nameComponent.setColor(color);
		valueComponent.setColor(color);
		nameGroup.add(nameComponent);
		valueGroup.add(valueComponent);
	}
	
	private static void boundary(Group nameGroup, Group valueGroup, String name) {
		StringComponent boundary = new StringComponent("*-*" + name);
		StringComponent nullComponent = new StringComponent("*-*-*-*-*-*-*-*");
		boundary.setColor(292325);
		nameGroup.add(boundary);
		valueGroup.add(nullComponent);
	}
	
	private static String getValue(Object details) {
		if (details instanceof FluidStack) {
			FluidStack stack = (FluidStack) details;
			return getValue(stack.getFluid()) + ":" + stack.amount;
		}
		String text = String.valueOf(details);
		String hash = '@' + Integer.toHexString(System.identityHashCode(details));
		if (text.endsWith(hash)) {
			if (details instanceof IForgeRegistryEntry.Impl) {
				return ((IForgeRegistryEntry.Impl<?>) details).getRegistryName().toString();
			}
			if (details instanceof Fluid) {
				return I18n.format(((Fluid) details).getBlock().getUnlocalizedName() + ".name");
			}
			return "未重写toString()";
		}
		return text;
	}
	
	private static boolean isContinue(Class<?> clazz) {
		return clazz != TileEntity.class && clazz != BaseTileEntity.class && clazz != FrontTileEntity.class;
	}
	
	private static int getStringColor(Field field) {
		int mod = field.getModifiers();
		if (Modifier.isStatic(mod)) return Modifier.isFinal(mod) ? 8388352 : 8087790;
		else return Modifier.isFinal(mod) ? 25600 : 0;
	}
	
}