package xyz.emptydreams.mi.api.net.guinet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.utils.MISysInfo;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class GUIMessage implements IMessage {
	
	private NBTTagCompound compound;
	
	private int amount = 0;
	
	public GUIMessage() { }
	
	public GUIMessage(NBTTagCompound compound) {
		WaitList.checkNull(compound, "compound");
		this.compound = compound;
	}
	
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
	
	public NBTTagCompound getCompound() { return compound; }
	
	public int plusAmount() {
		return ++amount;
	}
	
	//------------------------------处理信息的内部类------------------------------//
	
	/**
	 * 客户端处理
	 */
	public static final class ClientHandler implements IMessageHandler<GUIMessage, IMessage> {
		@Override
		public IMessage onMessage(GUIMessage message, MessageContext ctx) {
			NBTTagCompound compound = message.getCompound();
			EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
			Container con = player.openContainer;
			if (con instanceof IAutoGuiNetWork) {
				int code = compound.getInteger("_code");
				IAutoGuiNetWork network = (IAutoGuiNetWork) con;
				net.minecraft.client.gui.inventory.GuiContainer gui = network.getGuiContainer();
				network = (IAutoGuiNetWork) gui;
				if (network.checkAutoCode(code)) {
					if (network.isLive()) {
						network.receive(compound);
					} else {
						MISysInfo.err("GUI网络传输丢包：客户端GUI不处于生存状态！");
					}
				} else {
					MISysInfo.err("GUI网络传输丢包：serveCode=" + code + ", clientCode=" + network.getAuthCode());
				}
			} else {
				WaitList.addMessageToClientList(message);
			}
			return null;
		}
	}
	
}
