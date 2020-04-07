package minedreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;

import minedreams.mi.api.net.WaitList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@SideOnly(Side.CLIENT)
public class StringComponent extends MComponent {
	
	private String value;
	private int color = 0;
	
	public StringComponent(String str) {
		WaitList.checkNull(str, "str");
		value = str;
	}
	
	@Override
	public void setStringColor(int color) { this.color = color; }
	@Override
	public int getStringColor() { return color; }
	@Override
	public String getString() { return value; }
	@Override
	public void setString(String str) {
		WaitList.checkNull(str, "str");
		this.value = str;
	}
	@Override
	public boolean isString() { return true; }
	@Override
	public void paint(@Nonnull Graphics g) { }
}
