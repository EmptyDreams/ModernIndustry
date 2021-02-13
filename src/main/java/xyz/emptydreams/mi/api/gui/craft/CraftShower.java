package xyz.emptydreams.mi.api.gui.craft;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.client.LocalChildFrame;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 用于创建{@link CraftFrame}
 * @author EmptyDreams
 */
public final class CraftShower {
	
	private static final Map<CraftGuide<?, ?>, Integer> FRAMES = new Object2IntArrayMap<>();
	
	/**
	 * 使指定玩家打开GUI
	 * @param craft 要显示的合成表
	 * @param slots 需要填充的SlotGroup
	 */
	public static void show(CraftGuide<?, ?> craft, SlotGroup slots) {
		if (WorldUtil.isServer()) return;
		if (craft.size() == 0) return;
		int frame = FRAMES.computeIfAbsent(craft, c -> GuiLoader.register(new Frame(c, slots)));
		LocalChildFrame.openGUI(ModernIndustry.instance, frame, 0, 0, 0);
	}
	
	private static final class Frame implements IContainerCreater {
		
		private final CraftGuide<?, ?> craft;
		private final SlotGroup slots;
		
		Frame(CraftGuide<?, ?> craft, SlotGroup slots) {
			this.craft = craft;
			this.slots = slots;
		}
		
		@Nonnull
		@Override
		public MIFrame createService(World world, EntityPlayer player, BlockPos pos) {
			return new CraftFrame(craft, player, slots);
		}
		
		@Nonnull
		@Override
		public StaticFrameClient createClient(World world, EntityPlayer player, BlockPos pos) {
			return new StaticFrameClient(new CraftFrame(craft, player, slots), craft.getLocalName());
		}
		
	}
	
}