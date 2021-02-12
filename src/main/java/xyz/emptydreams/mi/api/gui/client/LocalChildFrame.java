package xyz.emptydreams.mi.api.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nullable;

/**
 * 仅在本地显示的子GUI
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class LocalChildFrame {
	
	/** 存储当前打开的子GUI */
	private static GuiScreen container;
	/** 存储打开子GUI前玩家代开的GUI */
	private static Container playerOpen;
	
	/**
	 * 打开一个子GUI，若已经有子GUI被打开，则先打开的将被关闭
	 * @param mod 模组主类
	 * @param guiID GUI的ID
	 * @param x　方块坐标
	 * @param y　方块坐标
	 * @param z　方块坐标
	 */
	public static void openGUI(Object mod, int guiID, int x, int y, int z) {
		playerOpen = Minecraft.getMinecraft().player.openContainer;
		ModContainer modContainer = FMLCommonHandler.instance().findContainerFor(mod);
		Object localGUI = NetworkRegistry.INSTANCE.getLocalGuiContainer(
				modContainer, WorldUtil.getPlayerAtClient(), guiID, WorldUtil.getClientWorld(), x, y, z);
		if (container != null) container.onGuiClosed();
		container = (GuiScreen) localGUI;
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