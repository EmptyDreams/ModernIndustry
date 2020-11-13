package xyz.emptydreams.mi.api.net.message.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.net.message.IMessageAddition;
import xyz.emptydreams.mi.api.utils.WorldUtil;

/**
 * GUI的Addition
 * @author EmptyDreams
 */
public class GuiAddition implements IMessageAddition {
	
	/** 玩家对象 */
	private EntityPlayer player;
	/** 组件ID */
	private int id;
	
	/**
	 * 创建GUI网络传输的附加信息
	 * @param player 发送/接受信息的玩家
	 * @param id 如果进行网络传输的为GUI组件，则填写{@link IComponent#getCode()}，否则为-1
	 */
	public GuiAddition(EntityPlayer player, int id) {
		this.player = player;
		this.id = id;
	}
	
	/** 构建一个空的附加信息 */
	public GuiAddition() { }
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public void writeTo(NBTTagCompound tag) {
		if (WorldUtil.isClient()) tag.setUniqueId("player", player.getUniqueID());
		tag.setInteger("id", id);
	}
	
	@Override
	public void readFrom(NBTTagCompound tag) {
		if (WorldUtil.isServer())
			player = WorldUtil.getPlayerAtService(tag.getUniqueId("player"));
		else player = Minecraft.getMinecraft().player;
		id = tag.getInteger("id");
	}
	
}
