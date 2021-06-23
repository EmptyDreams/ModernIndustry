package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.interfaces.GuiPainter;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseLocationListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseReleasedListener;

import javax.annotation.Nonnull;
import java.awt.*;

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
	
	/** 获取进度 */
	public double getTempo() {
		return ((double) index) / max;
	}
	
	/** 设置是否可用 */
	public void setDisable(boolean disable) {
		isDisable = disable;
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		max = (int) (FULL - (isVertical() ? (15.0 / (height - 2) * FULL) : (15.0 / (width - 2) * FULL)));
		min = (int) (isVertical() ? (1.5 / (height - 2) * FULL) : (1.5 / (width - 2) * FULL));
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
			if (isDisable()) return;
			float rX = mouseX - getX();
			float rY = mouseY - getY();
			isMouse = isMouseInButton(rX, rY);
			if (clicked) {
				index = Math.max(min, Math.min(max, getReLocation(mouseX, mouseY) - reLocation));
			}
		});
		registryListener((MouseActionListener) (mouseX, mouseY) -> {
			if (isDisable()) return;
			clicked = isMouse;
			reLocation = getReLocation(mouseX, mouseY) - index;
		});
		registryListener((MouseReleasedListener) (mouseX, mouseY, mouseButton) -> clicked = false);
	}
	
	private int getReLocation(float mouseX, float mouseY) {
		return (int) ((isVertical() ? (mouseY - getY()) / getHeight() : (mouseX - getX()) / getWidth()) * FULL);
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
		double index = getIndex();
		RuntimeTexture texture = bindTexture();
		if (isVertical()) {
			int offset = (int) (getHeight() * index);
			painter.drawTexture(getX() + 1, getY() + offset, 0, 0, buttonSize, 15, texture);
		} else {
			int offset = (int) (getWidth() * index);
			painter.drawTexture(getX() + offset, getY() + 1, 0, 0, 15, buttonSize, texture);
		}
	}
	
	@SuppressWarnings("ConstantConditions")
	private RuntimeTexture bindTexture() {
		return RuntimeTexture.getInstance(getButtonTextureName()).bindTexture();
	}
	
	/** 获取按钮材质的名称 */
	private String getButtonTextureName() {
		return "MI:Roll" + buttonSize + getHeight() + isVertical();
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		if (isVertical()) {
			g.drawImage(ImageData.getImage(ImageData.ROLL_BACKGROUND_VER, getWidth(), getHeight()), 0, 0, null);
			if (isDisable())
				ImageData.createTexture(ImageData.ROLL_BUTTON_DISABLE_VER, buttonSize, 15, getButtonTextureName());
			else
				ImageData.createTexture(ImageData.ROLL_BUTTON_VER, buttonSize, 15, getButtonTextureName());
		} else {
			g.drawImage(ImageData.getImage(ImageData.ROLL_BACKGROUND_HOR, getWidth(), getHeight()), 0, 0, null);
			if (isDisable())
				ImageData.createTexture(ImageData.ROLL_BUTTON_DISABLE_HOR, buttonSize, 15, getButtonTextureName());
			else
				ImageData.createTexture(ImageData.ROLL_BUTTON_HOR, 15, buttonSize, getButtonTextureName());
		}
	}
	
}