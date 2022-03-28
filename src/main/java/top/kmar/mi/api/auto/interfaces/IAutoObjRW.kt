package top.kmar.mi.api.auto.interfaces

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import kotlin.reflect.KClass

/**
 * 通过对象进行读写的数据读写器
 *
 * 该类型读写器不支持`final`(或`val`)类型的读写
 *
 * @author EmptyDreams
 */
interface IAutoObjRW<T> {

    /** 判断指定类型是否匹配 */
    fun match(type: KClass<*>): Boolean

    /**
     * 将指定对象写入到[IDataWriter]中
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param writer 承载数据的`writer`
     * @param value 要写入的数据
     * @param local 数据在本地存在的类型
     *
     * @return 写入的结果，当写入过程发生异常时应当将异常包含在[RWResult]中而非直接抛出
     */
    fun write2Local(writer: IDataWriter, value: T, local: KClass<*>): RWResult

    /**
     * 从[IDataReader]中读取指定类型的数据
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param reader 承载数据的`reader`
     * @param local 数据在本地存在的类型
     * @param receiver 数据接收器
     *
     * @return 读取的结果，当写入过程发生异常时应当将异常包含在[RWResult]中而非直接抛出
     */
    fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (T) -> Unit): RWResult

}