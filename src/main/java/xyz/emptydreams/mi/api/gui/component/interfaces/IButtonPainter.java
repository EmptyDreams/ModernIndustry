package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author EmptyDreams
 */
public interface IButtonPainter {
	
	/**
	 * 绘制按钮，在{@link GuiButton#drawButton(Minecraft, int, int, float)}
	 * 被调用时触发
	 * @param mc MC对象
	 * @param mouseX 鼠标坐标
	 * @param mouseY 鼠标坐标
	 * @see GuiButton#drawButton(Minecraft, int, int, float) 
	 */
	@SideOnly(Side.CLIENT)
	void drawButton(Minecraft mc, int mouseX, int mouseY);
	
}
