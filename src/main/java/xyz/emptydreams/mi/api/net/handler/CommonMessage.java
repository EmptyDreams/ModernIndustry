package xyz.emptydreams.mi.api.net.handler;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.MessageRegister;

/**
 * 通用信息传输
 * @author EmptyDreams
 */
public class CommonMessage implements IMessage {
	
	private NBTTagCompound data;
	
	public CommonMessage(NBTTagCompound data) {
		this.data = data.copy();
	}
	
	public CommonMessage() { }
	
	public void setData(NBTTagCompound data) {
		this.data = data.copy();
	}
	
	/** 服务端解析数据 */
	public boolean parseServer() {
		return MessageRegister.parseServer(data);
	}
	
	/** 客户端解析数据 */
	@SideOnly(Side.CLIENT)
	public void parseClient() {
		RawQueue.add(data);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		if (data == null) throw new IllegalArgumentException("没有给定需要传输的信息");
		ByteBufUtils.writeTag(buf, data);
	}
	
}