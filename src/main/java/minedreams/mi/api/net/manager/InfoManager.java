package minedreams.mi.api.net.manager;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * 信息传递管理器
 * @author EmptyDremas
 * @version V1.0
 */
public interface InfoManager<T extends TileEntity> {
	
	/** 管理的信息数量 */
	int size();
	
	/** 通过下标获取信息 */
	Object get(int index);
	
	/** 从te读取数据并写入到ByteBuf中 */
	void writeTo(T t, ByteBuf buf);
	
	/** 从te读取数据 */
	void readFrom(T t);
	
	/** 从ByteBuf读取数据 */
	void readFrom(ByteBuf buf);
	
	/** 向TE写入数据 */
	void readTo(T t);
	
}
