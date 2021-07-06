package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.GuiPainter;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 带标签的Group
 * @author EmptyDreams
 */
public class TitleGroup extends Group {

	private final InnerGroup innerGroup = new InnerGroup();
	private final String text;
	
	public TitleGroup(String text) {
		this(text, 0, 0);
	}
	
	public TitleGroup(String text, int width, int height) {
		super.setControlPanel(Panels::non);
		setSize(width, height);
		innerGroup.setLocation(2, 10);
		if (WorldUtil.isServer()) this.text = "";
		else this.text = I18n.format(text);
		StringComponent title = new StringComponent(text);
		title.setLocation(2, 1);
		super.add(title);
		innerGroup.close();
	}
	
	@Override
	public void setSize(int width, int height) {
		if (height < 16) height = 16;
		super.setSize(width, height);
		innerGroup.setSize(Math.max(1, width - 4), height - 4 - 11);
	}
	
	@Override
	public void add(IComponent component) {
		innerGroup.add(component);
	}
	
	@Override
	public void adds(IComponent... components) {
		this.innerGroup.adds(components);
	}
	
	@Override
	public IComponent containCode(int code) {
		IComponent result = super.containCode(code);
		if (result == null) return innerGroup.containCode(code);
		return result;
	}
	
	@Override
	public int getMinDistance() {
		return innerGroup.getMinDistance();
	}
	
	@Override
	public void setMinDistance(int minDistance) {
		innerGroup.setMinDistance(minDistance);
	}
	
	@Override
	public int getMaxDistance() {
		return innerGroup.getMaxDistance();
	}
	
	@Override
	public void setMaxDistance(int maxDistance) {
		innerGroup.setMaxDistance(maxDistance);
	}
	
	@Override
	public Consumer<Group> getArrangeMode() {
		return innerGroup.getArrangeMode();
	}
	
	@Override
	public void setControlPanel(Consumer<Group> mode) {
		innerGroup.setControlPanel(mode);
	}
	
	/** 获取演示的内容 */
	@SideOnly(Side.CLIENT)
	public String getText() {
		return text;
	}
	
	@Override
	public Iterator<IComponent> iterator() {
		return innerGroup.iterator();
	}
	
	
	
	@Override
	public void paint(@Nonnull Graphics g) {
		BufferedImage image = new BufferedImage(
				innerGroup.getWidth(), innerGroup.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics innerG = image.createGraphics();
		innerGroup.paint(innerG);
		innerG.dispose();
		g.drawImage(image, innerGroup.getX() - getX(), innerGroup.getY() - getY(), null);
		super.paint(g);
	}
	
	@Override
	public void realTimePaint(GuiPainter painter) {
		GuiPainter innerPainter = new GuiPainter(painter.getGuiContainer(), innerGroup.getX(), innerGroup.getY(),
				getXOffset(), getYOffset(), innerGroup.getWidth(), innerGroup.getHeight());
		innerGroup.realTimePaint(innerPainter);
		super.realTimePaint(painter);
	}
	
	private int getXOffset() {
		return 2;
	}
	
	private int getYOffset() {
		return 11;
	}
	
	@Nullable
	@Override
	public IComponent getMouseTarget(float mouseX, float mouseY) {
		IComponent target = super.getMouseTarget(mouseX, mouseY);
		if (target == null)
			return innerGroup.getMouseTarget(mouseX, mouseY);
		return target;
	}
	
	private static final class InnerGroup extends Group {
		
		private boolean canEdit = true;
		
		@Override
		public void setSize(int width, int height) {
			if (canEdit) super.setSize(width, height);
		}
		
		public void close() {
			canEdit = false;
		}
		
	}
	
}