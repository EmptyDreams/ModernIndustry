package xyz.emptydreams.mi.api.gui.listener;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * 所有窗体事件的父接口
 * @author EmptyDreams
 */
public interface IListener {
	
	/**
	 * 在事件触发时将事件所需数据写入到NBTTagCompound中，然后通过网络发送到服务端（客户端）
	 * @return 如果不需要传输数据返回null
	 */
	@Nullable
	default NBTTagCompound writeTo() { return null; }
	
	/** 读取通过网络接收到的信息 */
	default void readFrom(NBTTagCompound data) {}
	
}
