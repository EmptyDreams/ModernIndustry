package top.kmar.mi.api.net.handler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.DataReader;
import top.kmar.mi.api.dor.interfaces.IDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.utils.ExpandFunctionKt;

/**
 * 通用信息传输
 * @author EmptyDreams
 */
public class CommonMessage implements IMessage {
	
	private IDataReader reader;
	private String key;
	
	public CommonMessage(IDataReader reader, String key) {
		this.reader = DataReader.instance(reader);
		this.key = key;
	}
	
	public CommonMessage() { }
	
	/** 服务端解析数据 */
	public void parseServer(EntityPlayerMP player) {
		ServiceRawQueue.add(reader, key, player);
	}
	
	/** 客户端解析数据 */
	@SideOnly(Side.CLIENT)
	public void parseClient() {
		ClientRawQueue.add(reader, key);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = ExpandFunctionKt.readString(buf);
		IDataOperator reader = new ByteDataOperator(50);
		reader.writeFromByteBuf(buf);
		this.reader = reader;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		if (reader == null) throw new IllegalArgumentException("没有给定需要传输的信息");
		ExpandFunctionKt.writeString(buf, key);
		reader.readToByteBuf(buf);
	}
	
}