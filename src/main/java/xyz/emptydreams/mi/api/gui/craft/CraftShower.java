package xyz.emptydreams.mi.api.gui.craft;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.api.gui.common.IContainerCreater;
import xyz.emptydreams.mi.api.gui.common.MIFrame;

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
	 * @param player 要打开GUI的玩家
	 */
	public static void show(CraftGuide<?, ?> craft, EntityPlayer player) {
		int frame = FRAMES.computeIfAbsent(craft,
				key -> GuiLoader.register(new IContainerCreater() {
					@Nonnull
					@Override
					public MIFrame createService(World world, EntityPlayer player1, BlockPos pos) {
						return new CraftFrame(craft, player1);
					}
					
					@Nonnull
					@Override
					public StaticFrameClient createClient(World world, EntityPlayer player1, BlockPos pos) {
						return new StaticFrameClient(
								new CraftFrame(craft, player1), craft.getLocalName(), craft.getName());
					}
				}
		));
		player.openGui(ModernIndustry.instance, frame,
				FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0], 0, 0, 0);
	}
	
}
