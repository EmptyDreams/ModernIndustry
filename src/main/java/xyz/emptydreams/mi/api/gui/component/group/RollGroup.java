package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.RollComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.GuiPainter;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 带滚动条的控件组
 * @author EmptyDreams
 */
public class RollGroup extends Group {

	private final HorizontalEnum horizontal;
	/** 水平方向上的滚动条高度 */
	private int horRollHeight = 13;
	/** 水平方向上的滚动条宽度 */
	private int horRollWidth;
	
	private final VerticalEnum vertical;
	/** 垂直方向上的滚动条高度 */
	private int verRollHeight;
	/** 垂直方向上的滚动条宽度 */
	private int verRollWidth = 13;
	
	private RollComponent verRoll;
	private RollComponent horRoll;
	
	private final InnerGroup components;
	
	public RollGroup(HorizontalEnum horizontal, VerticalEnum vertical) {
		components = new InnerGroup();
		this.horizontal = StringUtil.checkNull(horizontal, "horizontal");
		this.vertical = StringUtil.checkNull(vertical, "vertical");
		super.setControlPanel(RollGroup::putInOrder);
	}
	
	/** 设置水平方向上的滚动条的高度 */
	public void setHorRollHeight(int horRollHeight) {
		this.horRollHeight = horRollHeight;
	}
	
	/** 设置水平方向上的滚动条的宽度 */
	public void setHorRollWidth(int horRollWidth) {
		this.horRollWidth = horRollWidth;
	}
	
	/** 设置垂直方向上的滚动条的高度 */
	public void setVerRollHeight(int verRollHeight) {
		this.verRollHeight = verRollHeight;
	}
	
	/** 设置垂直方向上的滚动条的宽度 */
	public void setVerRollWidth(int verRollWidth) {
		this.verRollWidth = verRollWidth;
	}
	
	/** 获取水平方向上的滚动条的高度 */
	public int getHorRollHeight() {
		return horRollHeight;
	}
	
	/** 获取水平方向上的滚动条的宽度 */
	public int getHorRollWidth() {
		return horRollWidth;
	}
	
	/** 获取垂直方向上的滚动条的高度 */
	public int getVerRollHeight() {
		return verRollHeight;
	}
	
	/** 获取垂直方向上的滚动条的宽度 */
	public int getVerRollWidth() {
		return verRollWidth;
	}
	
	@Override
	public void setSize(int width, int height) {
		if (width < 20) width = 20;
		if (height < 20) height = 20;
		super.setSize(width, height);
		if (vertical != VerticalEnum.NON && getVerRollHeight() == 0) {
			setVerRollHeight(height - 20);
		}
		if (horizontal != HorizontalEnum.NON && getHorRollWidth() == 0) {
			setHorRollWidth(width - 20);
		}
	}
	
	@Override
	public boolean remove(IComponent component) {
		return components.remove(component);
	}
	
	@Override
	public void add(IComponent component) {
		components.add(component);
	}
	
	@Override
	public void adds(IComponent... components) {
		this.components.adds(components);
	}
	
	@Override
	public IComponent containCode(int code) {
		IComponent result = super.containCode(code);
		if (result == null) return components.containCode(code);
		return result;
	}
	
	@Override
	public int getMinDistance() {
		return components.getMinDistance();
	}
	
	@Override
	public void setMinDistance(int minDistance) {
		components.setMinDistance(minDistance);
	}
	
	@Override
	public int getMaxDistance() {
		return components.getMaxDistance();
	}
	
	@Override
	public void setMaxDistance(int maxDistance) {
		components.setMaxDistance(maxDistance);
	}
	
	@Override
	public Consumer<Group> getArrangeMode() {
		return components.getArrangeMode();
	}
	
	@Override
	public void setControlPanel(Consumer<Group> mode) {
		components.setControlPanel(mode);
	}
	
	@Override
	public int size() {
		return components.size();
	}
	
	@Override
	public Iterator<IComponent> iterator() {
		return components.iterator();
	}
	
	@Override
	public void onAddToGUI(MIFrame con, EntityPlayer player) {
		components.onAddToGUI(con, player);
		super.onAddToGUI(con, player);
	}
	
	@Override
	public void onAddToGUI(StaticFrameClient con, EntityPlayer player) {
		components.onAddToGUI(con, player);
		super.onAddToGUI(con, player);
	}
	
	@Override
	public void onRemoveFromGUI(Container con) {
		components.onRemoveFromGUI(con);
		super.onRemoveFromGUI(con);
	}
	
	@Override
	public void send(Container con, IContainerListener listener) {
		super.send(con, listener);
		components.send(con, listener);
	}
	
	@Override
	public boolean update(int codeID, int data) {
		return components.update(codeID, data);
	}
	
	@SideOnly(Side.CLIENT)
	private BufferedImage image;
	
	@Override
	public void paint(@Nonnull Graphics g) {
		super.paint(g);
		image = new BufferedImage(components.getWidth(), components.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics innerG = image.createGraphics();
		components.paint(innerG);
		innerG.dispose();
	}
	
	@Override
	public void realTimePaint(GuiPainter painter) {
		GuiPainter innerPainter = new GuiPainter(painter.getGuiContainer(),
				getXOffset(), getYOffset(), components.getWidth(), components.getHeight());
		components.realTimePaint(painter);
		super.realTimePaint(painter);
	}
	
	private int getXOffset() {
		if (horizontal == HorizontalEnum.NON) return 0;
		return (int) (horRoll.getTempo() * components.getWidth());
	}
	
	private int getYOffset() {
		if (vertical == VerticalEnum.NON) return 0;
		return (int) (verRoll.getTempo() * components.getHeight());
	}
	
	@Nullable
	@Override
	public IComponent getMouseTarget(float mouseX, float mouseY) {
		IComponent target = super.getMouseTarget(mouseX, mouseY);
		if (target == this) return components.getMouseTarget(mouseX, mouseY);
		return target;
	}
	
	private void superAddComponent(IComponent... components) {
		for (IComponent it : components) {
			if (it == null) continue;
			super.add(it);
		}
	}
	
	/** 水平方向上的滚动条 */
	public enum HorizontalEnum { UP, DOWN, NON }
	/** 垂直方向上的滚动条 */
	public enum VerticalEnum { RIGHT, LEFT, NON }
	
	public static void putInOrder(Group group) {
		if (!(group instanceof RollGroup)) {
			MISysInfo.err("[RollGroup]整理方法中传入了不支持的参数：" + group.getClass().getName());
			return;
		}
		RollGroup that = (RollGroup) group;
		int width = that.getWidth() - 10;       //内部Group的宽度
		int height = that.getHeight() - 10;     //内部Group的高度
		int x = 5;      int y = 5;              //内部Group的坐标
		RollComponent verRoll = null, horRoll = null;
		
		if (that.vertical != VerticalEnum.NON) {
			verRoll = new RollComponent(true);
			verRoll.setSize(that.getVerRollWidth(), that.getVerRollHeight());
			width -= verRoll.getWidth();
		}
		if (that.horizontal != HorizontalEnum.NON) {
			horRoll = new RollComponent(false);
			horRoll.setSize(that.getHorRollWidth(),that.getHorRollHeight());
			height -= horRoll.getHeight();
		}
		that.components.setSize(width, height);
		
		if (that.vertical == VerticalEnum.LEFT) {
			x += (5 + verRoll.getWidth());
		}
		if (that.horizontal == HorizontalEnum.UP) {
			y += (5 + horRoll.getHeight());
		}
		that.components.setLocation(x, y);
		that.components.canEdit = false;
		
		switch (that.vertical) {
			case RIGHT:
				verRoll.setLocation(that.components.getWidth() + 5 + x,
						(that.components.getHeight() - verRoll.getHeight()) / 2 + y);
				break;
			case LEFT:
				verRoll.setLocation(5, (that.components.getHeight() - verRoll.getHeight()) / 2 + y);
				break;
			default: break;
		}
		switch (that.horizontal) {
			case UP:
				horRoll.setLocation((that.components.getWidth() - horRoll.getWidth()) / 2 + x, 5);
				break;
			case DOWN:
				horRoll.setLocation((that.components.getWidth() - horRoll.getWidth()) / 2 + x,
						that.components.getHeight() + y + 5);
				break;
			default: break;
		}
		that.verRoll = verRoll;
		that.horRoll = horRoll;
		that.superAddComponent(verRoll, horRoll, that.components);
	}
	
	private static final class InnerGroup extends Group {
		
		private boolean canEdit = true;
		
		@Override
		public void setSize(int width, int height) {
			if (canEdit) super.setSize(width, height);
		}
		
	}
	
}