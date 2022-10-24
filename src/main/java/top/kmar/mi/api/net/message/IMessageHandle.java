package top.kmar.mi.api.net.message;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.net.handler.CommonMessage;

import javax.annotation.Nonnull;

/**
 * <p>用于处理消息
 * <p>注意：<b>子类的类注释中必须说明该类应当在客户端还是服务端处理</b>
 * @author EmptyDreams
 */
public interface IMessageHandle<T extends IMessageAddition, V extends ParseAddition> {
    
    /**
     * <p>客户端处理指定消息
     * <p>补充：数据类型不一定为byte，只需要保证key是"_retry"即可
     * @param message 需要进行解析的数据
     * @param result 额外数据，初次运行时该对象的数据类型必定为{@link ParseAddition}
     * @return 额外数据
     * @throws UnsupportedOperationException 如果该信息只能由服务端处理
     * @throws NullPointerException 如果message == null || result == null或处理时遇到意外错误
     */
    @SideOnly(Side.CLIENT)
    @Nonnull
    V parseOnClient(@Nonnull NBTTagCompound message, @Nonnull V result);
    
    /**
     * <p>服务端处理指定消息
     * <p>补充：数据类型不一定为byte，只需要保证key是"_retry"即可
     * @param message 需要进行解析的数据
     * @param result 额外数据，初次运行时该对象的数据类型必定为{@link ParseAddition}
     * @return 额外数据
     * @throws UnsupportedOperationException 如果该信息只能由客户端处理
     * @throws NullPointerException 如果message == null || result == null或处理时遇到意外错误
     */
    @Nonnull
    V parseOnServer(@Nonnull NBTTagCompound message, @Nonnull V result);
    
    /** 获取消息对应的KEY */
    @Nonnull
    default String getKey() {
        return getClass().getSimpleName();
    }
    
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
    default NBTTagCompound packaging(@Nonnull NBTBase data, T addition) {
        NBTTagCompound result = new NBTTagCompound();
        result.setTag("data", data);
        result.setTag("add", addition.writeTo());
        return result;
    }
    
    /**
     * 构建一个{@link IMessage}
     * @param data 数据信息
     * @param addition 附加信息
     * @throws NullPointerException 如果data == null || addition == null
     */
    default IMessage create(NBTBase data, T addition) {
        return new CommonMessage(packaging(data, addition), getKey());
    }
    
}