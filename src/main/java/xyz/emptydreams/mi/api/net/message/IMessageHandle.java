package xyz.emptydreams.mi.api.net.message;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.handler.CommonMessage;

import javax.annotation.Nonnull;

/**
 * 用于处理消息.<br>
 * <b>子类规范：<br>
 * 1.子类的类注释中必须说明该类应当在客户端还是服务端处理<br>
 * 2.子类抛出{@link UnsupportedOperationException}异常时应通过{@link #throwException(Side)}抛出<br></b>
 * 3.该类中没有"@throws"注释的方法不得抛出异常<br>
 * 4.有"@throws"注释的方法不得抛出规定外的异常
 * @author EmptyDreams
 */
public interface IMessageHandle<T extends IMessageAddition> {
	
	/**
	 * 客户端处理指定消息.<br>
	 * <b>若消息处理失败，系统默认会直接丢弃该信息，若允许信息重试处理，则需要手动在message写入信息：
	 * {@code message.setByte("_retry", (byte) 0)}</b><br>
	 * 补充：数据类型不一定为byte，只需要保证key是"_retry"即可
	 * @return 消息处理是否成功
	 * @throws UnsupportedOperationException 如果该信息只能由服务端处理
	 * @throws NullPointerException 如果message==null或处理时遇到意外错误
	 */
	@SideOnly(Side.CLIENT)
	boolean parseOnClient(@Nonnull NBTTagCompound message);
	
	/**
	 * 服务端处理指定消息.<br>
	 * <b>若消息处理失败，系统默认会直接丢弃该信息，若允许信息重试处理，则需要手动在message写入信息：
	 * {@code message.setByte("_retry", (byte) 0)}</b><br>
	 * 补充：数据类型不一定为byte，只需要保证key是"_retry"即可
	 * @return 消息处理是否成功
	 * @throws UnsupportedOperationException 如果该信息只能由客户端处理
	 * @throws NullPointerException 如果message==null或处理时遇到意外错误
	 */
	boolean parseOnServer(@Nonnull NBTTagCompound message);
	
	/**
	 * 判断能否处理指定的消息
	 * @param message 指定的消息
	 */
	boolean match(@Nonnull NBTTagCompound message);
	
	/**
	 * 判断消息能否在指定的位置处理
	 * @param side 客户端或服务端
	 */
	boolean match(@Nonnull Side side);
	
	/**
	 * 将消息封装为当前处理类支持的类型，额外信息中一般包含世界、方块坐标等信息。
	 * @param data 数据信息
	 * @param addition 额外信息，
	 * @return 封装后的消息
	 * @throws NullPointerException 如果data==null||addition==null
	 */
	@Nonnull
	NBTTagCompound packaging(@Nonnull NBTTagCompound data, T addition);
	
	/**
	 * 获取消息的字符串简略信息，用于在消息丢失时向后台输出错误信息，
	 * 信息越精准越容易检查错误，简略信息不宜过长，避免后台日志过于杂乱。
	 * @param message 丢失的信息
	 * @return 简略信息
	 * @throws NullPointerException 如果message==null
	 */
	@Nonnull
	String getInfo(NBTTagCompound message);
	
	/** 抛出一个异常 */
	static UnsupportedOperationException throwException(Side side) {
		if (side.isServer()) {
			return new UnsupportedOperationException("该IMessageHandle不支持在服务端处理信息");
		}
		return new UnsupportedOperationException("该IMessageHandle不支持在客户端处理信息");
	}
	
	/**
	 * 构建一个{@link IMessage}
	 * @param data 数据信息
	 * @param addition 附加信息
	 * @throws NullPointerException 如果data==null||addition==null
	 */
	default IMessage create(NBTTagCompound data, T addition) {
		return new CommonMessage(packaging(data, addition));
	}
	
}