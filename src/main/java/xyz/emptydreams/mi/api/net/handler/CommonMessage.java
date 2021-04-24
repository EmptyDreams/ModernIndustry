package xyz.emptydreams.mi.api.net.handler;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.DataReader;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.ParseResultEnum;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;

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
	public ParseResultEnum parseServer() {
		return MessageRegister.parseServer(reader, key);
	}
	
	/** 客户端解析数据 */
	@SideOnly(Side.CLIENT)
	public void parseClient() {
		RawQueue.add(reader, key);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = DataTypeRegister.read(buf, String.class, null);
		IDataOperator reader = new ByteDataOperator(50);
		reader.writeFromByteBuf(buf);
		this.reader = reader;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		if (reader == null) throw new IllegalArgumentException("没有给定需要传输的信息");
		DataTypeRegister.write(buf, key);
		reader.readToByteBuf(buf);
	}
	
}