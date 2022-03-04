package top.kmar.mi.api.gui.craft;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.craftguide.CraftGuide;
import top.kmar.mi.api.gui.client.LocalChildFrame;
import top.kmar.mi.api.gui.client.StaticFrameClient;
import top.kmar.mi.api.gui.component.group.SlotGroup;
import top.kmar.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * 用于创建{@link CraftFrame}
 * @author EmptyDreams
 */
public final class CraftShower {
	
	private static final Map<CraftGuide<?, ?>, FrameHandle> FRAMES = new Object2ObjectArrayMap<>();
	
	/**
	 * 使指定玩家打开GUI
	 * @param craft 要显示的合成表
	 * @param pos 打开合成表的方块的坐标
	 * @param slotGroupGetter 通过TileEntity来获取slotGroupGetter
	 */
	public static void show(CraftGuide<?, ?> craft, BlockPos pos,
	                        Function<TileEntity, SlotGroup> slotGroupGetter) {
		if (craft.size() == 0) return;
		//该代码在if前是为了在打开合成表时在服务端记录下数据
		FrameHandle frame = FRAMES.computeIfAbsent(craft, c -> new FrameHandle(c, slotGroupGetter));
		if (WorldUtil.isServer()) return;
		LocalChildFrame.openGUI(frame, pos);
	}
	
	/**
	 * 获取指定方块的SlotGroup
	 * @param craft 合成表对象
	 * @param te 指定的方块的TE
	 * @throws NullPointerException 如果合成表未注册
	 */
	public static SlotGroup getSlotGroup(CraftGuide<?, ?> craft, TileEntity te) {
		return FRAMES.get(craft).getSlots(te);
	}
	
	private static final class FrameHandle implements ICraftFrameHandle {
		
		private final CraftGuide<?, ?> craft;
		private final Function<TileEntity, SlotGroup> slotGroupGetter;
		
		FrameHandle(CraftGuide<?, ?> craft, Function<TileEntity, SlotGroup> slotGroupGetter) {
			this.craft = craft;
			this.slotGroupGetter = slotGroupGetter;
		}
		
		public SlotGroup getSlots(TileEntity te) {
			return slotGroupGetter.apply(te);
		}
		
		public SlotGroup getSlots(World world, BlockPos pos) {
			return getSlots(world.getTileEntity(pos));
		}
		
		@Nonnull
		@Override
		public StaticFrameClient createFrame(World world, EntityPlayer player, BlockPos pos) {
			return new StaticFrameClient(new CraftFrame(
					craft, player, getSlots(world, pos)), craft.getLocalName());
		}
		
	}
	
}