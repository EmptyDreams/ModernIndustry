package xyz.emptydreams.mi.api.net;

import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;

import javax.annotation.Nonnull;

/**
 * 自动化的网络传输类，实现该接口的类应该同时实现{@link net.minecraft.tileentity.TileEntity}
 * @author EmptyDreams
 */
public interface IAutoNetwork {
	
	/**
	 * 处理接收的信息
	 */
	void receive(@Nonnull IDataReader reader);
	
}