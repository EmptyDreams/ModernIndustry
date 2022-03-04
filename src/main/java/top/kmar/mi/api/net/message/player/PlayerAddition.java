package top.kmar.mi.api.net.message.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.net.message.IMessageAddition;

import javax.annotation.Nonnull;

/**
 * Player的Addition
 * @author EmptyDreams
 */
public class PlayerAddition implements IMessageAddition {
	
	/** 获取一个PlayerAddition对象 */
	@Nonnull
	public static PlayerAddition instance(IDataReader message) {
		PlayerAddition result = new PlayerAddition();
		result.readFrom(message);
		return result;
	}
	
	private EntityPlayer PLAYER;
	private ResourceLocation KEY;
	
	private PlayerAddition() { }
	
	public PlayerAddition(EntityPlayer player, ResourceLocation key) {
		this.PLAYER = StringUtil.checkNull(player, "player");
		KEY = StringUtil.checkNull(key, "key");
	}
	
	/** 获取玩家的UUID */
	public EntityPlayer getPlayer() {
		return PLAYER;
	}
	
	public ResourceLocation getKey() {
		return KEY;
	}
	
	@Override
	public void writeTo(IDataWriter writer) {
		writer.writeString(KEY.getResourceDomain());
		writer.writeString(KEY.getResourcePath());
	}
	
	@Override
	public void readFrom(IDataReader reader) {
		String modid = reader.readString();
		String name = reader.readString();
		KEY = new ResourceLocation(modid, name);
	}
	
}