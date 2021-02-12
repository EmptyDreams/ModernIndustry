package xyz.emptydreams.mi.api.net.message.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.net.message.IMessageAddition;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Player的Addition
 * @author EmptyDreams
 */
public class PlayerAddition implements IMessageAddition {
	
	/** 获取一个PlayerAddition对象 */
	@Nonnull
	public static PlayerAddition instance(NBTTagCompound message) {
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
	public void writeTo(NBTTagCompound tag) {
		tag.setUniqueId("player", PLAYER.getUniqueID());
		tag.setString("modid", KEY.getResourceDomain());
		tag.setString("name", KEY.getResourcePath());
	}
	
	@Override
	public void readFrom(NBTTagCompound tag) {
		UUID uuid = tag.getUniqueId("player");
		String modid = tag.getString("modid");
		String name = tag.getString("name");
		PLAYER = WorldUtil.getPlayer(uuid);
		KEY = new ResourceLocation(modid, name);
	}
	
}