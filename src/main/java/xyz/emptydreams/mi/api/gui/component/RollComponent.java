package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import xyz.emptydreams.mi.api.gui.client.ImageData;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
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
	
	/** 滚动按钮最大位置 */
	public static final int MAX = 10000;
	
	/** 存储是否为垂直显示 */
	private final boolean vertical;
	/** 存储按钮当前位置 */
	private int index = 0;
	
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
	
	/** 滚动轴是否为水平 */
	public boolean isHorizontal() {
		return !isVertical();
	}
	
	/** 获取当前按钮的位置 */
	public double getIndex() {
		return ((double) index) / MAX;
	}
	
	private boolean isMouse = false;
	private boolean clicked = false;
	/** 鼠标点击时与滚动条的相对位置 */
	private int reLocation = -1;
	
	@Override
	protected void init(MIFrame frame, EntityPlayer player) {
		super.init(frame, player);
		registryListener((MouseLocationListener) (mouseX, mouseY) -> {
			float rX = mouseX - getX();
			float rY = mouseY - getY();
			isMouse = isMouseInButton(rX, rY);
			if (clicked) {
				index = getReLocation(mouseX, mouseY);
			}
		});
		registryListener((MouseActionListener) (mouseX, mouseY) -> {
			clicked = isMouse;
			reLocation = getReLocation(mouseX, mouseY);
		});
		registryListener((MouseReleasedListener) (mouseX, mouseY, mouseButton) -> clicked = false);
	}
	
	private int getReLocation(float mouseX, float mouseY) {
		return isVertical() ? (int) (mouseY - getY()) : (int) (mouseX - getX());
	}
	
	/**
	 * 判断鼠标是否在按钮上
	 * @param mouseX 鼠标横坐标（相对于控件）
	 * @param mouseY 鼠标纵坐标（相对于控件）
	 */
	private boolean isMouseInButton(float mouseX, float mouseY) {
		double min = getIndex();
		double max = min + 15;
		if (isVertical()) {
			return mouseY >= min && mouseY <= max;
		} else {
			return mouseX >= min && mouseX <= max;
		}
	}
	
	@Override
	public void realTimePaint(GuiContainer gui) {
		double index = getIndex();
		RuntimeTexture texture = bindTexture();
		if (isVertical()) {
			int offset = (int) (getHeight() * index);
			texture.drawToFrame(gui.getGuiLeft() + getX(), gui.getGuiTop() + getY() + offset,
								0, 0, getWidth(), 15);
		} else {
			int offset = (int) (getWidth() * index);
			texture.drawToFrame(gui.getGuiLeft() + getX() + offset, gui.getGuiTop() + getY(),
								0, 0, 15, getHeight());
		}
	}
	
	@SuppressWarnings("ConstantConditions")
	private RuntimeTexture bindTexture() {
		return isMouse ? RuntimeTexture.getInstance(getClickTextureName()).bindTexture()
							: RuntimeTexture.getInstance(getSrcTextureName()).bindTexture();
	}
	
	private String getSrcTextureName() {
		return "MI:Roll" + getWidth() + getHeight() + isVertical();
	}
	
	private String getClickTextureName() {
		return "MI:RollC" + getWidth() + getHeight() + isVertical();
	}
	
	@Override
	public void paint(@Nonnull Graphics g) {
		g.drawImage(ImageData.getImage(ImageData.ROLL_BACKGROUND, getWidth(), getHeight()), 0, 0, null);
		if (isVertical()) {
			ImageData.createTexture(getSrcTextureName(), getWidth(), 15, ImageData.ROLL_BUTTON);
			ImageData.createTexture(getClickTextureName(), getWidth(), 15, ImageData.ROLL_BUTTON_CLICK);
		} else {
			ImageData.createTexture(getSrcTextureName(), 15, getHeight(), ImageData.ROLL_BUTTON);
			ImageData.createTexture(getClickTextureName(), 15, getHeight(), ImageData.ROLL_BUTTON_CLICK);
		}
	}
	
}