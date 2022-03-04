package top.kmar.mi.api.net.message.gui;

import net.minecraft.entity.player.EntityPlayer;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.gui.component.interfaces.IComponent;
import top.kmar.mi.api.net.message.IMessageAddition;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * GUI的Addition
 * @author EmptyDreams
 */
public class GuiAddition implements IMessageAddition {
	
	/** 玩家对象 */
	private UUID player;
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
		this.player = player.getUniqueID();
		this.guiID = StringUtil.checkNull(guiID, "guiID");
		this.id = id;
	}
	
	/** 构建一个空的附加信息 */
	public GuiAddition() { }
	
	public EntityPlayer getPlayer() {
		return WorldUtil.getPlayer(player);
	}
	
	public int getId() {
		return id;
	}
	
	@Nonnull
	public String getGuiID() {
		return guiID;
	}
	
	@Override
	public void writeTo(IDataWriter writer) {
		writer.writeVarInt(id);
		writer.writeString(getGuiID());
		writer.writeUuid(player);
	}
	
	@Override
	public void readFrom(IDataReader reader) {
		id = reader.readVarInt();
		guiID = reader.readString();
		player = reader.readUuid();
	}
	
}