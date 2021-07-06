package xyz.emptydreams.mi.api.gui.component.group;

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
	
	private final InnerGroup innerGroup;
	
	public RollGroup(HorizontalEnum horizontal, VerticalEnum vertical) {
		innerGroup = new InnerGroup();
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
		if (verRoll != null) verRoll.realTimePaint(painter);
		if (horRoll != null) horRoll.realTimePaint(painter);
	}
	
	private int getXOffset() {
		if (horizontal == HorizontalEnum.NON) return 0;
		return (int) (horRoll.getTempo() * innerGroup.getWidth());
	}
	
	private int getYOffset() {
		if (vertical == VerticalEnum.NON) return 0;
		return (int) (verRoll.getTempo() * innerGroup.getHeight());
	}
	
	@Nullable
	@Override
	public IComponent getMouseTarget(float mouseX, float mouseY) {
		IComponent target = super.getMouseTarget(mouseX, mouseY);
		if (target == null)
			return innerGroup.getMouseTarget(mouseX, mouseY);
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
		//计算内部Group的大小
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
		that.innerGroup.setSize(width, height);
		//计算内部Group的坐标
		if (that.vertical == VerticalEnum.LEFT) {
			x += (5 + verRoll.getWidth());
		}
		if (that.horizontal == HorizontalEnum.UP) {
			y += (5 + horRoll.getHeight());
		}
		that.innerGroup.setLocation(x, y);
		that.innerGroup.close();
		
		if (horRoll != null && that.innerGroup.getRealWidth() <= that.innerGroup.getWidth()) {
			horRoll.setDisable(true);
		}
		if (verRoll != null && that.innerGroup.getRealHeight() <= that.innerGroup.getHeight()) {
			verRoll.setDisable(true);
		}
		
		switch (that.vertical) {
			case RIGHT:
				verRoll.setLocation(that.innerGroup.getWidth() + 5 + x,
						(that.innerGroup.getHeight() - verRoll.getHeight()) / 2 + y);
				break;
			case LEFT:
				verRoll.setLocation(5, (that.innerGroup.getHeight() - verRoll.getHeight()) / 2 + y);
				break;
			default: break;
		}
		switch (that.horizontal) {
			case UP:
				horRoll.setLocation((that.innerGroup.getWidth() - horRoll.getWidth()) / 2 + x, 5);
				break;
			case DOWN:
				horRoll.setLocation((that.innerGroup.getWidth() - horRoll.getWidth()) / 2 + x,
						that.innerGroup.getHeight() + y + 5);
				break;
			default: break;
		}
		that.verRoll = verRoll;
		that.horRoll = horRoll;
		that.superAddComponent(verRoll, horRoll, that.innerGroup);
	}
	
	private static final class InnerGroup extends Group {
		
		private boolean canEdit = true;
		private int realHeight = 0;
		private int realWidth = 0;
		
		@Override
		public void setSize(int width, int height) {
			if (canEdit) super.setSize(width, height);
		}
		
		public void close() {
			canEdit = false;
			for (IComponent it : this) {
				int x = it.getX() - getX();
				int y = it.getY() - getY();
				realWidth = Math.max(realWidth, x + it.getWidth());
				realHeight = Math.max(realHeight, y + it.getHeight());
			}
		}
		
		public int getRealHeight() {
			return realHeight;
		}
		
		public int getRealWidth() {
			return realWidth;
		}
		
	}
	
}