package minedreams.mi.api.net;

import java.util.List;

import io.netty.buffer.ByteBuf;
import minedreams.mi.api.electricity.Electricity;
import minedreams.mi.api.net.info.SimpleImplInfo;
import minedreams.mi.api.net.message.MessageList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MessageBase implements IMessage {
	
	/** 存储信息列表 */
	private MessageList list;
	/** 世界类型 */
	private int dimension;
	/** 方块坐标 */
	private BlockPos pos;
	
	/** 获取信息列表 */
	public MessageList getMessageList() {
		return list;
	}
	
	/** 获取世界类型 */
	public int getDimension() {
		return dimension;
	}
	
	/** 获取方块坐标 */
	public BlockPos getPos() {
		return pos;
	}
	
	/** 设置信息列表 */
	public void setMessageList(MessageList list) {
		this.list = list;
	}
	
	/** 设置世界类型 */
	public void setDimension(World dimension) {
		this.dimension = dimension.provider.getDimension();
	}
	
	/** 设置世界类型 */
	public void setDimension(int world) {
		this.dimension = world;
	}
	
	/** 设置方块坐标 */
	public void setPos(BlockPos pos) {
		this.pos = pos;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		dimension = buf.readInt();
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		
		int intSize = buf.readInt();
		int longSize = buf.readInt();
		int byteSize = buf.readInt();
		int shortSize = buf.readInt();
		int byteArraySize = buf.readInt();
		list = new MessageList();
		
		for (int i = 0; i < intSize; ++i)
			list.writeInt(ByteBufUtils.readUTF8String(buf), buf.readInt());
		for (int i = 0; i < longSize; ++i)
			list.writeLong(ByteBufUtils.readUTF8String(buf), buf.readLong());
		for (int i = 0; i < byteSize; ++i)
			list.writeByte(ByteBufUtils.readUTF8String(buf), buf.readByte());
		for (int i = 0; i < shortSize; ++i)
			list.writeShort(ByteBufUtils.readUTF8String(buf), buf.readShort());
		for (int i = 0; i < byteArraySize; ++i) {
			byte[] b = new byte[buf.readInt()];
			buf.readBytes(b);
			list.writeByteArray(ByteBufUtils.readUTF8String(buf), b);
		}
		
		list.getNames().readFrom(buf);
		List<String> nameList = list.getNames().getInfos();
		int size = buf.readInt();
		try {
			for (int i = 0; i < size; ++i) {
				Class<?> name = Class.forName(nameList.get(i));
				SimpleImplInfo<?> info = (SimpleImplInfo<?>) name.newInstance();
				info.readFrom(buf);
				list.writeInfo(ByteBufUtils.readUTF8String(buf), info);
			}
		} catch (ClassNotFoundException e) {
			throw new AssertionError("内部错误，目的类不存在!");
		} catch (IllegalAccessException | InstantiationException e) {
			throw new AssertionError("内部错误，原因未知！");
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimension);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		
		buf.writeInt(list == null ? 0 : list.intSize());
		buf.writeInt(list.longSize());
		buf.writeInt(list.byteSize());
		buf.writeInt(list.shortSize());
		buf.writeInt(list.byteArraySize());
		list.forEachInt((key, value) -> {
			ByteBufUtils.writeUTF8String(buf, key);
			buf.writeInt(value);
		});
		list.forEachLong((key, value) -> {
			ByteBufUtils.writeUTF8String(buf, key);
			buf.writeLong(value);
		});
		list.forEachByte((key, value) -> {
			ByteBufUtils.writeUTF8String(buf, key);
			buf.writeByte(value);
		});
		list.forEachShort((key, value) -> {
			ByteBufUtils.writeUTF8String(buf, key);
			buf.writeShort(value);
		});
		list.forEachByteArray((key, value) -> {
			buf.writeInt(value.length);
			ByteBufUtils.writeUTF8String(buf, key);
			buf.writeBytes(value);
		});
		
		list.getNames().writeTo(buf);
		buf.writeInt(list.infoSize());
		list.forEacnInfo((key, value) -> {
			value.writeTo(buf);
			ByteBufUtils.writeUTF8String(buf, key);
		});
	}
	
	/**
	 * 获取消息数量
	 * @return >= 0
	 */
	public int size() {
		return list.byteArraySize() + list.byteSize() + list.infoSize() +
				       list.intSize() + list.longSize() + list.shortSize();
	}
	
	@Override
	public String toString() {
		return "MessageBase{" +
				       "dimension=" + dimension +
				       "size=" + size() +
				       '}';
	}
	
	//------------------------------处理信息的内部类------------------------------//
	
	/**
	 * 客户端处理
	 */
	public static final class ClientHandler implements IMessageHandler<MessageBase, IMessage> {
		
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			WaitList.addMessageToClientList(message);
			return null;
		}
	}
	
	/**
	 * 服务端处理类
	 */
	public static final class ServiceHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			Electricity et = (Electricity) FMLCommonHandler.instance().getMinecraftServerInstance()
					                               .getWorld(message.dimension).getTileEntity(message.pos);
			WaitList.checkNull(et, "et");
			et.reveive(message.list);
			return null;
		}
	}
	
}
