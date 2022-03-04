package top.kmar.mi.api.gui.component.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.net.message.gui.GuiAddition;
import top.kmar.mi.api.net.message.gui.GuiMessage;
import top.kmar.mi.api.gui.client.GuiPainter;
import top.kmar.mi.api.gui.client.StaticFrameClient;
import top.kmar.mi.api.gui.common.MIFrame;
import top.kmar.mi.api.gui.listener.IListener;
import top.kmar.mi.api.net.handler.MessageSender;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * 所有控件的接口.<br>
 * @author EmptyDreams
 */
public interface IComponent {
	
	/**
	 * <p>设置组件在控件组中的坐标
	 * <p>控件组负责将该坐标转化为在GUI中的坐标
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
	/** 获取控件在GUI中的Y轴坐标 */
	int getY();
	/** 获取控件在GUI中的X轴坐标 */
	int getX();
	/** 获取显示高度 */
	int getHeight();
	/** 获取显示宽度 */
	int getWidth();
	
	/** 获取真实高度（部分控件的尺寸与显示出来的尺寸不同） */
	default int getRealHeight() {
		return getHeight();
	}
	/** 获取真实宽度（部分控件的尺寸与显示出来的尺寸不同） */
	default int getRealWidth() {
		return getWidth();
	}
	
	/**
	 * <p>在组件被添加到管理类时调用。
	 * <p>在该方法中不应该向管理类添加控件或从管理类删除控件。
	 * <p><b>在该方法被执行时，不能保证父级管理类一定存在父级管理类</b>
	 * <p><b>和窗体无关的初始化操作不应该放在这个方法里，因为这可能会导致重复初始化，
	 *          如果必须在该方法中进行初始化，须自行检查是否应当进行初始化</b>
	 * @throws NullPointerException 如果需要使用con或player却传入了null
	 */
	void onAdd2Manager(IComponentManager manager);
	
	/**
	 * <p>在组件于客户端被添加到GUI时调用。
	 * <p>在该方法中不应该向管理类添加控件或从管理类删除控件。
	 * <p><b>在该方法被执行时，不能保证父级管理类一定存在父级管理类</b>
	 * <p><b>和窗体无关的初始化操作不应该放在这个方法里，因为这可能会导致重复初始化，
	 *          如果必须在该方法中进行初始化，须自行检查是否应当进行初始化</b>
	 * @throws NullPointerException 如果需要使用con或player却传入了null
	 */
	void onAdd2ClientFrame(StaticFrameClient frame);
	
	/** 实时渲染 */
	@SideOnly(Side.CLIENT)
	default void paint(GuiPainter painter) { }
	
	/**
	 * 构建一个默认的材质名称
	 * @return 格式：[modid]:[类名简称]@[width]![height]
	 */
	@Nonnull
	@SideOnly(Side.CLIENT)
	default String createTextureName() {
		return FMLCommonHandler.instance().getModName()
				+ ":"  +getClass().getSimpleName() + "@" + getWidth() + "!" + getHeight();
	}
	
	/**
	 * 构建一个默认的材质名称
	 * @param extra 额外信息
	 * @return 格式：[modid]:[类名简称]+[extra]@[width]![height]
	 */
	@Nonnull
	@SideOnly(Side.CLIENT)
	default String createTextureName(String extra) {
		return FMLCommonHandler.instance().getModName() + ":"
				+ getClass().getSimpleName() + "+" + extra  + "@" + getWidth() + "!" + getHeight();
	}
	
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
	default void receive(IDataReader data) { }
	
	/**
	 * 发送信息到服务端
	 * @param data 数据内容
	 */
	@SideOnly(Side.CLIENT)
	default void sendToServer(MIFrame frame, IDataReader data) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		IMessage message = GuiMessage.instance().create(
				data, new GuiAddition(player, frame.getID(), getCode()));
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
	
	/** 获取指定下标的事件 */
	@Nonnull
	IListener getListener(int index);
	
	/**
	 * 触发指定事件
	 * @param name 事件名称，所有继承自该类的事件都将被触发
	 * @param consumer 需要执行的操作
	 */
	<T extends IListener> void activateListener(MIFrame frame, Class<T> name, Consumer<T> consumer);
	
	/**
	 * 注册指定事件
	 * @return 是否注册成功
	 */
	@SuppressWarnings("UnusedReturnValue")
	boolean registryListener(IListener listener);
	
}