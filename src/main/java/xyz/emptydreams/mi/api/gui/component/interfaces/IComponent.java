package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.listener.IListener;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.gui.GuiAddition;
import xyz.emptydreams.mi.api.net.message.gui.GuiMessage;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 所有控件的接口
 * @author EmptyDreams
 */
public interface IComponent {
	
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
	 * 绘制图像，在组件被渲染时调用.
	 * @param g 画笔
	 */
	@SideOnly(Side.CLIENT)
	void paint(@Nonnull Graphics g);
	/**
	 * 在组件被添加到GUI时调用
	 * @throws NullPointerException 如果需要使用con或player却传入了null
	 */
	void onAddToGUI(MIFrame con, EntityPlayer player);
	/**
	 * 在组件被添加到客户端GUI时调用
	 * @throws NullPointerException 如果需要使用con或player却传入了null
	 */
	@SideOnly(Side.CLIENT)
	void onAddToGUI(StaticFrameClient con, EntityPlayer player);
	/** 在组件被移除GUI时 */
	void onRemoveFromGUI(Container con);
	
	/**
	 * 判断鼠标是否在组件内
	 * @param mouseX 鼠标X轴坐标（相对于GUI）
	 * @param mouseY 鼠标Y轴坐标（相对于GUI）
	 */
	default boolean isMouseInside(float mouseX, float mouseY) {
		return getMouseTarget(mouseX, mouseY) != null;
	}
	
	/**
	 * 获取鼠标指向的组件
	 * @param mouseX 鼠标X轴坐标（相对于GUI）
	 * @param mouseY 鼠标Y轴坐标（相对于GUI）
	 * @return 若鼠标坐标不在组件范围内或指定地点无组件则返回null
	 */
	default IComponent getMouseTarget(float mouseX, float mouseY) {
		return this;
	}
	
	/** 实时渲染 */
	@SideOnly(Side.CLIENT)
	default void realTimePaint(GuiContainer gui) { }
	
	/**
	 * 发送数据到客户端，发送所需的varToUpdate参数范围是[{@link #getCode()}, {@link #getCode()} + 100)
	 * @param listener 数据发送接口
	 */
	default void send(Container con, IContainerListener listener) { }

	/**
	 * 接受服务端发送的信息
	 * @param codeID 数据代号
	 * @param data 数据信息
	 * @return 若输入的数据代号符合要求则返回true，可以帮助客户端减少处理时长
	 */
	@SideOnly(Side.CLIENT)
	default boolean update(int codeID, int data) { return false; }
	
	/**
	 * 接收由{@link GuiMessage}发送的信息
	 * @param data 数据内容
	 */
	default void receive(NBTTagCompound data) { }
	
	/**
	 * 发送信息到服务端
	 * @param data 数据内容
	 */
	@SideOnly(Side.CLIENT)
	default void sendToServer(NBTTagCompound data) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("data", data);
		tag.setInteger("id", getCode());
		EntityPlayer player = Minecraft.getMinecraft().player;
		IMessage message = GuiMessage.instance().create(tag, new GuiAddition(player, getCode()));
		MessageSender.sendToServer(message);
	}
	
	/**
	 * 获取传输数据用的codeID
	 * @param code 私有ID，大于等于0
	 * @return 共有ID
	 */
	default int getCodeID(int code) {
		return getCode() + code;
	}

	/**
	 * 通过共有获取私有ID
	 * @param code 共有ID
	 * @return 私有ID
	 */
	default int getPrivateCodeID(int code) {
		return code - getCode();
	}

	/** 获取传输数据用的codeID */
	int getCode();
	
	/** 设置code起点，该方法由GUI类调用 */
	void setCodeStart(int code);
	
	/**
	 * 判断指定的网络传输ID是否在当前组件范围内
	 * @return 是则返回具体值，不是则返回null
	 */
	default IComponent containCode(int code) {
		return (code >= getCode() && code < getCode() + 100) ? this : null;
	}
	
	/**
	 * 获取GUI监听的事件列表
	 * @return 返回值需经过保护性拷贝
	 */
	Object getListeners();
	
	/**
	 * 触发指定事件
	 * @param name 事件名称，所有继承自该类的事件都将被触发
	 * @param consumer 需要执行的操作
	 */
	<T extends IListener> void activateListener(Class<T> name, Consumer<T> consumer);
	
	/**
	 * 注册指定事件
	 * @return 是否注册成功
	 */
	boolean registryListener(IListener listener);
	
	/**
	 * 删除满足指定条件的事件
	 * @param test 指定条件
	 * @return 是否移除成功
	 */
	boolean removeListenerIf(Predicate<IListener> test);
	
	/**
	 * 移除指定事件
	 * @return 是否移除成功
	 */
	boolean removeListener(IListener listener);
	
}