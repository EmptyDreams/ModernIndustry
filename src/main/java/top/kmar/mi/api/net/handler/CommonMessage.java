package top.kmar.mi.api.net.handler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.utils.expands.IOExpandsKt;

/**
 * 通用信息传输
 * @author EmptyDreams
 */
public class CommonMessage implements IMessage {
	
	private NBTTagCompound data;
	private String key;
	
	public CommonMessage(NBTTagCompound data, String key) {
		this.data = data;
		this.key = key;
	}
	
	public CommonMessage() { }
	
	/** 服务端解析数据 */
	public void parseServer(EntityPlayerMP player) {
		ServiceRawQueue.add(data, key, player);
	}
	
	/** 客户端解析数据 */
	@SideOnly(Side.CLIENT)
	public void parseClient() {
		ClientRawQueue.add(data, key);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = IOExpandsKt.readString(buf);
		data = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		if (data == null) throw new IllegalArgumentException("没有给定需要传输的信息");
		IOExpandsKt.writeString(buf, key);
		ByteBufUtils.writeTag(buf, data);
	}
	
}