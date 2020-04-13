package xyz.emptydreams.mi.api.gui.component;

import javax.annotation.Nonnull;
import java.awt.*;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IComponent {
	
	/**
	 * 获取需要渲染的字符串
	 * @return 若{@link #isString()}返回true需要返回具体值
	 */
	String getString();
	/** 判断组件是否为字符串类组件 */
	boolean isString();
	/**
	 * 获取字符串颜色.
	 * @return 若{@link #isString()}返回true需要返回具体值
	 */
	int getStringColor();
	/** 设置字符串颜色 */
	void setStringColor(int color);
	/** 设置字符串内容 */
	void setString(String str);
	
	/**
	 * 设置组件在GUI中的坐标
	 * @param x X轴
	 * @param y Y轴
	 */
	void setLocation(int x, int y);
	/**
	 * 设置组件大小
	 * @param width 宽度
	 * @param height 高度
	 */
	void setSize(int width, int height);
	/** 获取Y轴坐标 */
	int getY();
	/** 获取X轴坐标 */
	int getX();
	/** 获取高度 */
	int getHeight();
	/** 获取宽度 */
	int getWidth();
	
	/**
	 * 绘制图像，在组件被渲染时调用.<br>
	 * 在{@link #isString()}返回true时该方法依然被调用，
	 * 但是渲染字符串是自动完成的，用户不需要在这个方法中绘制字符串
	 * @param g 画笔
	 */
	void paint(@Nonnull Graphics g);
	/**
	 * 在组件被添加到GUI时调用
	 */
	void onAddToGUI(Container con, EntityPlayer player);
	/**
	 * 在组件被移除GUI时
	 */
	void onRemoveFromGUI(Container con);
	
	/** 实时渲染 */
	default void realTimePaint(GuiContainer gui) { }
	
	/**
	 * 发送数据到客户端，发送所需的varToUpdate参数范围是[{@link #getCode()}, {@link #getCode()} + 100)
	 * @param listener 数据发送接口
	 */
	default void send(Container con, IContainerListener listener) { }
	
	default boolean update(int codeID, int data) { return false; }
	
	int getCode();
	
	/** 设置code起点，该方法由GUI类调用 */
	void setCodeStart(int code);
	
}
