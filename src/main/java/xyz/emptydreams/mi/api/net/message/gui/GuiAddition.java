package xyz.emptydreams.mi.api.net.message.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.net.message.IMessageAddition;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;

/**
 * GUI的Addition
 * @author EmptyDreams
 */
public class GuiAddition implements IMessageAddition {
	
	/** 玩家对象 */
	private EntityPlayer player;
	/** 组件ID */
	private int id;
	/** GUI ID */
	private String guiID;
	
	/**
	 * 创建GUI网络传输的附加信息
	 * @param player 发送/接受信息的玩家
	 * @param id 如果进行网络传输的为GUI组件，则填写{@link IComponent#getCode()}，否则为-1
	 */
	public GuiAddition(EntityPlayer player, String guiID, int id) {
		this.player = player;
		this.guiID = StringUtil.checkNull(guiID, "guiID");
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
	
	@Nonnull
	public String getGuiID() {
		return guiID;
	}
	
	@Override
	public void writeTo(NBTTagCompound tag) {
		if (WorldUtil.isClient()) tag.setUniqueId("player", player.getUniqueID());
		tag.setInteger("id", id);
		tag.setString("guiId", getGuiID());
	}
	
	@Override
	public void readFrom(NBTTagCompound tag) {
		if (WorldUtil.isServer())
			player = WorldUtil.getPlayerAtService(tag.getUniqueId("player"));
		else player = Minecraft.getMinecraft().player;
		id = tag.getInteger("id");
		guiID = tag.getString("guiId");
	}
	
}
