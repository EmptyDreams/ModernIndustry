package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import xyz.emptydreams.mi.api.gui.client.GuiPainter;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseLocationListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseReleasedListener;

import javax.annotation.Nonnull;

/**
 * 滚动轴控件
 * @author EmptyDreams
 */
public class RollComponent extends MComponent {
	
	
	public static final int FULL = 10000;
	
	/** 存储是否为垂直显示 */
	private final boolean vertical;
	/** 存储按钮当前位置 */
	private int index = 0;
	/** 滚动按钮最大位置 */
	private int max;
	/** 滚动按钮最小位置 */
	private int min;
	/** 滚动条竖直时按钮的宽度（或水平时按钮的高度） */
	private int buttonSize;
	/** 滚动轴是否不可用 */
	private boolean isDisable = false;
	
	/**
	 * 创建一格滚动轴
	 * @param isVertical 是否为竖直
	 */
	public RollComponent(boolean isVertical) {
		vertical = isVertical;
	}
	
	/** 滚动轴是否为竖直 */
	public boolean isVertical() {
		return vertical;
	}
	
	/** 滚动轴是否不可用 */
	public boolean isDisable() {
		return isDisable;
	}
	
	/** 滚动轴是否为水平 */
	public boolean isHorizontal() {
		return !isVertical();
	}
	
	/** 获取当前按钮的位置 */
	public double getIndex() {
		return ((double) index) / FULL;
	}
	
	public boolean plusIndex(int plus) {
		if (isDisable) return false;
		if (plus >= 0) {
			if (index == max) return false;
			index = Math.min(index + plus, max);
		} else {
			if (index == min) return false;
			index = Math.max(index + plus, min);
		}
		return true;
	}
	
	/** 获取进度 */
	public double getTempo() {
		return ((double) (index - min)) / max;
	}
	
	/** 设置是否可用 */
	public void setDisable(boolean disable) {
		isDisable = disable;
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		index = min = (int) ((isVertical() ? (1.0 / (height - 2)) : (1.0 / (width - 2))) * FULL);
		max = (int) ((1 - (isVertical() ? (15.0 / (height - 2)) : (15.0 / (width - 2)))) * FULL);
		buttonSize = isVertical() ? width - 2 : height - 2;
	}
	
	private boolean isMouse = false;
	private boolean clicked = false;
	/** 鼠标点击时与滚动条的相对位置 */
	private int reLocation = -1;
	
	@Override
	protected void init(MIFrame frame, EntityPlayer player) {
		super.init(frame, player);
		registryListener((MouseLocationListener) (mouseX, mouseY) -> {
			if (isDisable) return;
			float rX = mouseX - getX();
			float rY = mouseY - getY();
			isMouse = isMouseInButton(rX, rY);
			if (clicked) {
				index = Math.max(min, Math.min(max + min, getReLocation(mouseX, mouseY) - reLocation));
			}
		});
		registryListener((MouseActionListener) (mouseX, mouseY) -> {
			if (isDisable) return;
			clicked = isMouse;
			reLocation = getReLocation(mouseX, mouseY) - index;
		});
		registryListener((MouseReleasedListener) (mouseX, mouseY, mouseButton) -> clicked = false);
	}
	
	private int getReLocation(float mouseX, float mouseY) {
		int result = (int)
				((isVertical() ? (mouseY - getStart()) / getHeight()
						: (mouseX - getStart()) / getWidth()) * FULL);
		return Math.max(result, 0);
	}
	
	private int getStart() {
		return (isVertical() ? getY() : getX()) + 1;
	}
	
	/**
	 * 判断鼠标是否在按钮上
	 * @param mouseX 鼠标横坐标（相对于控件）
	 * @param mouseY 鼠标纵坐标（相对于控件）
	 */
	private boolean isMouseInButton(float mouseX, float mouseY) {
		if (isVertical()) {
			int min = (int) (getIndex() * getHeight());
			int max = min + 15;
			return mouseY >= min && mouseY <= max;
		} else {
			int min = (int) (getIndex() * getWidth());
			int max = min + 15;
			return mouseX >= min && mouseX <= max;
		}
	}
	
	@Override
	public void realTimePaint(GuiPainter painter) {
		GlStateManager.color(1, 1, 1);
		paintBackground(painter);
		double index = getIndex();
		RuntimeTexture texture = bindTexture();
		if (isVertical()) {
			int offset = (int) (getHeight() * index);
			painter.drawTexture(getX() + 1, getY() + offset, buttonSize, 15, texture);
		} else {
			int offset = (int) (getWidth() * index);
			painter.drawTexture(getX() + offset, getY() + 1, 15, buttonSize, texture);
		}
	}
	
	public void paintBackground(@Nonnull GuiPainter painter) {
		RuntimeTexture texture;
		if (isVertical()) {
			texture = ImageData.createTexture(ImageData.ROLL_BACKGROUND_VER,
					getWidth(), getHeight(), createTextureName("ver"));
		} else {
			texture = ImageData.createTexture(ImageData.ROLL_BACKGROUND_VER,
					getWidth(), getHeight(), createTextureName("hor"));
		}
		texture.bindTexture();
		painter.drawTexture(0, 0, getWidth(), getHeight(), texture);
	}
	
	private RuntimeTexture bindTexture() {
		RuntimeTexture texture = RuntimeTexture.getInstance(getButtonTextureName());
		if (texture == null) {
			texture = createTexture();
		}
		return texture.bindTexture();
	}
	
	/** 获取按钮材质的名称 */
	private String getButtonTextureName() {
		return "MI:Roll" + buttonSize + getHeight() + isVertical();
	}
	
	private RuntimeTexture createTexture() {
		if (isVertical()) {
			if (isDisable()) return ImageData.createTexture(
						ImageData.ROLL_BUTTON_DISABLE_VER, buttonSize, 15, getButtonTextureName());
			else return ImageData.createTexture(
						ImageData.ROLL_BUTTON_VER, buttonSize, 15, getButtonTextureName());
		} else {
			if (isDisable()) return ImageData.createTexture(
						ImageData.ROLL_BUTTON_DISABLE_HOR, 15, buttonSize, getButtonTextureName());
			else return ImageData.createTexture(
						ImageData.ROLL_BUTTON_HOR, 15, buttonSize, getButtonTextureName());
		}
	}
	
}