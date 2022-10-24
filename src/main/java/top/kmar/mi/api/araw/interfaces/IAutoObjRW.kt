package top.kmar.mi.api.araw.interfaces

import net.minecraft.nbt.NBTBase
import kotlin.reflect.KClass

/**
 * 通过对象进行读写的数据读写器
 *
 * 该类型读写器不支持`final`(或`val`)类型的读写
 *
 * @author EmptyDreams
 */
interface IAutoObjRW<T> : IAutoMachine {

    /** 判断指定类型是否匹配 */
    fun match(type: KClass<*>): Boolean

    /**
     * 将指定对象写入到[NBTBase]中
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param value 要写入的数据
     * @param local 数据在本地存在的类型
     *
     * @return 需要存储的数据，返回`null`表示跳过存储
     */
    fun write2Local(value: T, local: KClass<*>): NBTBase?

    /**
     * 从[NBTBase]中读取指定类型的数据
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param reader 承载数据的`reader`
     * @param local 数据在本地存在的类型
     * @param receiver 数据接收器
     */
    fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (T) -> Unit)

}