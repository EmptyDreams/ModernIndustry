package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * 用于显示单行字符串
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public class StringComponent extends MComponent {
	
	private String value;
	private int color = 0;

	/** 创建一个包含空字符串的组件 */
	public StringComponent() { this(""); }

	/** 创建一个包含指定字符串的组件 */
	public StringComponent(String str) {
		WaitList.checkNull(str, "str");
		value = str;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void realTimePaint(GuiContainer gui) {
		gui.mc.fontRenderer.drawString(getString(),
				getX() + gui.getGuiLeft(), getY() + gui.getGuiLeft(), getColor());
	}

	/** 设置字符串颜色 */
	public void setColor(int color) { this.color = color; }
	/** 获取字符串颜色 */
	public int getColor() { return color; }
	/** 获取要显示的字符串 */
	public String getString() { return value; }
	/** 设置要显示的字符串 */
	public void setString(String str) {
		WaitList.checkNull(str, "str");
		this.value = str;
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
