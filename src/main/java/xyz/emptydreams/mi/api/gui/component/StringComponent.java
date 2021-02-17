package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import java.awt.*;

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
	}

	@SideOnly(Side.CLIENT) private String text;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void realTimePaint(GuiContainer gui) {
		if (text == null) text = I18n.format(getString());
		gui.mc.fontRenderer.drawString(text,
				getX() + gui.getGuiLeft(), getY() + gui.getGuiTop(), getColor());
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
	}

	@Override
	public void paint(@Nonnull Graphics g) { }
	
	@Override
	public String toString() {
		return "StringComponent{" +
				       "value='" + value + '\'' +
				       ", color=" + color +
				       '}';
	}
}