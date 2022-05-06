package top.kmar.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.graph.utils.GuiPainter;

/**
 * 用于显示单行字符串
 * @author EmptyDreams
 */
public class StringComponent extends MComponent {
	
	private String value;
	private int color = 0;

	/** 创建一个包含空字符串的组件 */
	public StringComponent() { this(""); }

	/** 创建一个包含指定字符串的组件 */
	public StringComponent(String str) {
		value = StringUtil.checkNull(str, "str");
		height = 9;
		setString(str);
	}

	@SideOnly(Side.CLIENT) private String text;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		if (text == null) text = I18n.format(getString());
		int real = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
		int realX = (getWidth() - real) / 2;
		int realY = (getHeight() - 9) / 2;
		painter.drawString(realX, realY, text, getColor());
	}

	/** 设置字符串颜色 */
	public void setColor(int color) { this.color = color; }
	/** 获取字符串颜色 */
	public int getColor() { return color; }
	/** 获取要显示的字符串 */
	public String getString() { return value; }
	/** 设置要显示的字符串 */
	public void setString(String str) {
		this.value = StringUtil.checkNull(str, "str");
		text = null;
		if (WorldUtil.isClient()) {
			text = I18n.format(str);
			int real = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
			if (real > width) width = real;
		}
	}
	
	@Override
	public String toString() {
		return "StringComponent{" +
				       "value='" + value + '\'' +
				       ", color=" + color +
				       '}';
	}
}