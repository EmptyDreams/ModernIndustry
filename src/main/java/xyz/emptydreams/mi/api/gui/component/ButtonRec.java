package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.interfaces.IButton;
import xyz.emptydreams.mi.api.gui.component.interfaces.IButtonPainter;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 普通矩形按钮
 * @author EmptyDreams
 */
public class ButtonRec extends MComponent implements IButton {
	
	public static final String RESOURCE_NAME = "button";
	public static final String RESOURCE_CLICKED_NAME = "buttonClicked";
	
	private final IButtonPainter operator;
	private final Consumer<ButtonRec> click;
	
	public ButtonRec(IButtonPainter painter, Consumer<ButtonRec> click) {
		this.operator = StringUtil.checkNull(painter, "painter");
		this.click = StringUtil.checkNull(click, "click");
	}
	
	@Override
	public void click() {
		click.accept(this);
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		//g.drawImage(ImageData.getImage(RESOURCE_NAME, getWidth(), getHeight()), 0, 0, null);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void onAddToGUI(StaticFrameClient con, EntityPlayer player) {
		super.onAddToGUI(con, player);
		con.addButton(this);
	}
	
	@Override
	public void onAddToGUI(MIFrame con, EntityPlayer player) {
		super.onAddToGUI(con, player);
		con.addButton(this);
	}
	
	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public GuiButton createGuiButtonObject(int id) {
		return new GuiButton(id,getX(), getY(), getWidth(), getHeight(), "text") {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				super.drawButton(mc, mouseX, mouseY, partialTicks);
				if (operator != null) operator.drawButton(mc, mouseX, mouseY);
			}
		};
	}
	
	
}
