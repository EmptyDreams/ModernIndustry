package xyz.emptydreams.mi.api.gui.listener;

import xyz.emptydreams.mi.api.dor.IDataReader;
import xyz.emptydreams.mi.api.dor.IDataWriter;

import javax.annotation.Nullable;

/**
 * 所有窗体事件的父接口
 * @author EmptyDreams
 */
public interface IListener {
	
	/**
	 * 在事件触发时将事件所需数据写入到IDataWriter中，然后通过网络发送到服务端（客户端）
	 * @return 如果不需要传输数据返回false
	 */
	@Nullable
	default boolean writeTo(IDataWriter writer) { return false; }
	
	/** 读取通过网络接收到的信息 */
	default void readFrom(IDataReader data) {}
	
}