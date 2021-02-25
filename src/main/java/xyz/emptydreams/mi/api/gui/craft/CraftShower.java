package xyz.emptydreams.mi.api.gui.craft;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * 用于创建{@link CraftFrame}
 * @author EmptyDreams
 */
public final class CraftShower {
	
	private static final Map<CraftGuide<?, ?>, Frame> FRAMES = new Object2ObjectArrayMap<>();
	
	/**
	 * 使指定玩家打开GUI
	 * @param craft 要显示的合成表
	 * @param slotGroupGetter 通过TileEntity来获取slotGroupGetter
	 */
	public static void show(CraftGuide<?, ?> craft, TileEntity te,
	                        Function<TileEntity, SlotGroup> slotGroupGetter) {
		if (craft.size() == 0) return;
		Frame frame = FRAMES.computeIfAbsent(craft, c -> new Frame(c, slotGroupGetter));
		if (WorldUtil.isServer()) return;
		LocalChildFrame.openGUI(frame, te.getPos());
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
	
	private static final class Frame implements ICraftFrameHandle {
		
		private final CraftGuide<?, ?> craft;
		private final Function<TileEntity, SlotGroup> slotGroupGetter;
		
		Frame(CraftGuide<?, ?> craft, Function<TileEntity, SlotGroup> slotGroupGetter) {
			this.craft = craft;
			this.slotGroupGetter = slotGroupGetter;
		}
		
		@Override
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