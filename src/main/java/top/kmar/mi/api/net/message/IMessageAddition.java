package top.kmar.mi.api.net.message;

import net.minecraft.nbt.NBTBase;

/**
 * 用于存储发送消息前的附加信息，
 * {@link IMessageHandle}可以接收一个该类对象然后将附加信息写入到信息中
 * @author EmptyDreams
 */
public interface IMessageAddition {
	
	/**
	 * 将附加信息写入到指定的NBTTagCompound中
	 * @throws NullPointerException 如果tag == null
	 */
	NBTBase writeTo();
	
	/**
	 * 从NBTTagCompound中读取信息到附加信息中
	 * @throws NullPointerException 如果tag == null
	 */
	void readFrom(NBTBase nbt);
	
}