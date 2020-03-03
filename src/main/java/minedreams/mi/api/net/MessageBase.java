package minedreams.mi.api.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
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
	
	public MessageBase() { }
	
	public MessageBase(NBTTagCompound compound) {
		WaitList.checkNull(compound, "compound");
		this.compound = compound;
	}
	
	private NBTTagCompound compound = null;
	
	/**
	 * 设置要传输的Tag，若已存在一个Tag，会覆盖原来的信息
	 */
	public void writeNBT(NBTTagCompound compound) {
		WaitList.checkNull(compound, "compound");
		this.compound = compound;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		compound = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		if (compound == null) throw new NullPointerException("没有给信息类设定要传输的信息");
		ByteBufUtils.writeTag(buf, compound);
	}
	
	public NBTTagCompound getCompound() {
		return compound;
	}
	
	public int getDimension() {
		return compound.getInteger("_world");
	}
	
	public BlockPos getBlockPos() {
		int[] pos = compound.getIntArray("_pos");
		return new BlockPos(pos[0], pos[1], pos[2]);
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
			IAutoNetwork et = (IAutoNetwork) FMLCommonHandler.instance().getMinecraftServerInstance()
					                               .getWorld(message.getDimension())
					                               .getTileEntity(message.getBlockPos());
			WaitList.checkNull(et, "et");
			et.reveive(message.getCompound());
			return null;
		}
	}
	
}
