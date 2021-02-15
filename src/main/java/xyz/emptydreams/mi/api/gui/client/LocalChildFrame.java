package xyz.emptydreams.mi.api.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.craft.ICraftFrameHandle;

import javax.annotation.Nullable;

/**
 * 仅在本地显示的子GUI
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class LocalChildFrame {
	
	/** 存储当前打开的子GUI */
	private static GuiScreen container;
	/** 存储打开子GUI前玩家打开的GUI */
	private static Container playerOpen;
	
	/**
	 * 打开一个子GUI，若已经有子GUI被打开，则先打开的将被关闭
	 * @param handle 生成GUI类对象的生成器
	 * @param pos 方块坐标
	 */
	public static void openGUI(ICraftFrameHandle handle, BlockPos pos) {
		Minecraft mc = Minecraft.getMinecraft();
		playerOpen = mc.player.openContainer;
		StaticFrameClient localGUI = handle.createFrame(mc.world, mc.player, pos);
		if (container != null) container.onGuiClosed();
		container = localGUI;
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		container.setWorldAndResolution(Minecraft.getMinecraft(), i, j);
	}
	
	/** 关闭当前显示的子GUI */
	public static void closeGUI() {
		if (container == null) return;
		container.onGuiClosed();
		Minecraft.getMinecraft().player.openContainer = playerOpen;
		playerOpen = null;
		container = null;
	}
	
	/** 判断是否有子GUI被显示 */
	public static boolean hasContainer() {
		return getContainer() != null;
	}
	
	/** 获取已经打开的子GUI，若没有打开的子GUI则返回null */
	@Nullable
	public static GuiScreen getContainer() {
		return container;
	}
	
}