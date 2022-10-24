package top.kmar.mi.api.net.message.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import top.kmar.mi.api.net.message.IMessageAddition;
import top.kmar.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;

/**
 * Player的Addition
 * @author EmptyDreams
 */
public class PlayerAddition implements IMessageAddition {
	
	/** 获取一个PlayerAddition对象 */
	@Nonnull
	public static PlayerAddition instance(NBTBase message) {
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
	public NBTBase writeTo() {
		return new NBTTagString(KEY.toString());
	}
	
	@Override
	public void readFrom(NBTBase reader) {
		KEY = new ResourceLocation(((NBTTagString) reader).getString());
	}
	
}