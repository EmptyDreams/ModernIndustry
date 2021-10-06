package xyz.emptydreams.mi.api.gui.component.group;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.component.RollComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponentManager;
import xyz.emptydreams.mi.api.gui.listener.key.IKeyPressedListener;
import xyz.emptydreams.mi.api.gui.listener.key.IKeyReleaseListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseWheelListener;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
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
		super.setControlPanel(Panels::non);
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
		if (width <= 20) width = 21;
		if (height <= 20) height = 21;
		super.setSize(width, height);
	}
	
	@Override
	public RollGroup add(IComponent component) {
		innerGroup.add(component);
		return this;
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
	public Consumer<Group> getControlMode() {
		return innerGroup.getControlMode();
	}
	
	@Override
	public void setControlPanel(Consumer<Group> mode) {
		innerGroup.setControlPanel(mode);
	}
	
	@Override
	public Iterator<IComponent> iterator() {
		return innerGroup.iterator();
	}
	
	private final AtomicBoolean isShift = new AtomicBoolean(false);
	
	@Override
	protected void init(IComponentManager manager) {
		super.init(manager);
		registryListener((IKeyPressedListener) (keyCode, isFocus)
				-> isShift.set(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode() == keyCode));
		registryListener((IKeyReleaseListener) (keyCode, isFocus) -> isShift.set(false));
		registryListener((IMouseWheelListener) wheel -> {
			int roll = -wheel * 3;
			if (isShift.get()) {
				if (horRoll == null) {
					if (verRoll != null) verRoll.plusIndex(roll);
				} else if (!horRoll.plusIndex(roll)) {
					if (verRoll != null) verRoll.plusIndex(roll);
				}
			} else {
				if (verRoll == null) {
					if (horRoll != null) horRoll.plusIndex(roll);
				} else if (!verRoll.plusIndex(roll)) {
					if (horRoll != null) horRoll.plusIndex(roll);
				}
			}
		});
	}
	
	@Override
	public void paint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		if (horizontal != HorizontalEnum.NON) {
			horRoll.paint(
					painter.createPainter(horRoll.getX(), horRoll.getY(), horRoll.getWidth(), horRoll.getHeight()));
		}
		if (vertical != VerticalEnum.NON) {
			verRoll.paint(
					painter.createPainter(verRoll.getX(), verRoll.getY(), verRoll.getWidth(), verRoll.getHeight()));
		}
		GuiPainter innerPainter = painter.createPainter(
				innerGroup.getX(), innerGroup.getY(), -getXOffset(), -getYOffset(),
				innerGroup.getWidth(), innerGroup.getHeight());
		innerGroup.paint(innerPainter);
	}
	
	private int getXOffset() {
		if (horizontal == HorizontalEnum.NON) return 0;
		return (int) (horRoll.getTempo() * (innerGroup.getRealWidth() - innerGroup.getWidth()));
	}
	
	private int getYOffset() {
		if (vertical == VerticalEnum.NON) return 0;
		return (int) (verRoll.getTempo() * (innerGroup.getRealHeight() - innerGroup.getHeight()));
	}
	
	@Override
	public void onAdd2Manager(IComponentManager manager) {
		initGroupComponent();
		super.onAdd2Manager(manager);
		innerGroup.calculate();
		if (horRoll != null && innerGroup.getRealWidth() <= innerGroup.getWidth()) {
			horRoll.setDisable(true);
		}
		if (verRoll != null && innerGroup.getRealHeight() <= innerGroup.getHeight()) {
			verRoll.setDisable(true);
		}
	}
	
	/** 初始化内部控件 */
	private void initGroupComponent() {
		initRollSize();
		initRollObject();
		initGroupLocation();
		initRollLocation();
		innerGroup.close();
		superAddComponent(verRoll, horRoll, innerGroup);
	}
	
	/** 初始化滚动条对象以及内部Group的尺寸 */
	private void initRollObject() {
		int width = getWidth() - 13;  int height = getHeight() - 13;    //内部Group的尺寸
		if (vertical != VerticalEnum.NON) {
			verRoll = new RollComponent(true);
			verRoll.setSize(getVerRollWidth(), getVerRollHeight());
			width -= getVerRollWidth();
		}
		if (horizontal != HorizontalEnum.NON) {
			horRoll = new RollComponent(false);
			horRoll.setSize(getHorRollWidth(), getHorRollHeight());
			height -= getHorRollHeight();
		}
		innerGroup.setSize(width, height);
	}
	
	/** 初始化滚动条的尺寸 */
	private void initRollSize() {
		if (getVerRollHeight() == 0) {
			setVerRollHeight((horizontal == HorizontalEnum.NON)
					? (getHeight() - 30 - getHorRollHeight()) : (getHeight() - 30));
		}
		if (getHorRollWidth() == 0) {
			setHorRollWidth((vertical == VerticalEnum.NON)
					? (getWidth() - 30 - getVerRollWidth()) : (getWidth() - 30));
		}
	}
	
	/** 初始化内部Group的坐标 */
	private void initGroupLocation() {
		int x = 5;               int y = 5;         //内部Group的坐标
		if (vertical == VerticalEnum.LEFT) {
			x += (3 + verRoll.getWidth());
		}
		if (horizontal == HorizontalEnum.UP) {
			y += (3 + horRoll.getHeight());
		}
		innerGroup.setLocation(x, y);
	}
	
	/** 初始化滚动条的坐标 */
	private void initRollLocation() {
		int x = innerGroup.getX();
		int y = innerGroup.getY();
		switch (vertical) {
			case RIGHT:
				verRoll.setLocation(innerGroup.getWidth() + x + 3,
						(innerGroup.getHeight() - verRoll.getHeight()) / 2 + y);
				break;
			case LEFT:
				verRoll.setLocation(5, (innerGroup.getHeight() - verRoll.getHeight()) / 2 + y);
				break;
			default: break;
		}
		switch (horizontal) {
			case UP:
				horRoll.setLocation((innerGroup.getWidth() - horRoll.getWidth()) / 2 + x, 5);
				break;
			case DOWN:
				horRoll.setLocation((innerGroup.getWidth() - horRoll.getWidth()) / 2 + x,
						innerGroup.getHeight() + y + 3);
				break;
			default: break;
		}
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
	
	private static final class InnerGroup extends Group {
		
		private int realHeight = 0;
		private int realWidth = 0;
		
		private boolean canEdit = true;
		
		public void close() {
			canEdit = false;
		}
		
		@Override
		public void setSize(int width, int height) {
			if (canEdit) super.setSize(width, height);
		}
		
		public void calculate() {
			for (IComponent it : this) {
				realWidth = Math.max(realWidth, it.getX() + it.getWidth());
				realHeight = Math.max(realHeight, it.getY() + it.getHeight());
			}
		}
		
		@Override
		public int getRealHeight() {
			return realHeight;
		}
		@Override
		public int getRealWidth() {
			return realWidth;
		}
		
	}
	
}