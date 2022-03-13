package top.kmar.mi.api.auto.registers

import top.kmar.mi.api.auto.RWResult
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.utils.container.Wrapper
import kotlin.reflect.KClass

/**
 * 数据读写器接口
 *
 * @param T 该读写器支持的数据类型
 *
 * @author EmptyDreams
 */
interface IAutoRW<T : Any> {

    /** 指明该类型的读写器是否允许对不可变类型进行读写 */
    fun allowFinal()

    /**
     * 判断指定数据是否可以被当前读写器捕获
     *
     * @param clazz 该对象在代码中声明的类型的class，参见[write2Local]
     */
    fun match(value: T, clazz: KClass<T>?): Boolean

    /**
     * 将数据转换为指定类型
     *
     * @throws ClassCastException 如果转换不被支持
     */
    fun <V : Any> cast(value: T, distClazz: KClass<V>): V

    /**
     * 将指定类的内容写入到[IDataWriter]中
     *
     * 该函数允许抛出异常，但应当尽量将异常放置在返回值中，抛出异常可能会导致后续的读写操作终止
     *
     * @param writer 承载数据的`writer`，函数内部不允许修改该`writer`的已有数据
     *
     * @param clazz 该对象在代码中声明的类型的class
     *
     * 例：
     *
     * ```kotlin
     *     class Sample {
     *         var test0: List<String> = ArrayList()
     *     }
     * ```
     *
     * 上述代码传入的class将为[List]，也有可能为`null`
     *
     * @return 写入的结果，当写入过程发生异常时应当将异常包含在[RWResult]中而非直接抛出
     */
    fun write2Local(writer: IDataWriter, value: T, clazz: KClass<T>?): RWResult

    /**
     * 从[IDataReader]中读取数据
     *
     * 该函数允许抛出异常，但应当尽量将异常放置在返回值中，抛出异常可能会导致后续的读写操作终止
     *
     * @param reader 承载数据的`reader`，函数保证读取完毕后`reader`的指针处于下一个数据的开头
     *
     * @param value 该参数内部的初值是无效的，如果读取器需要修改对象引用，则应当将`value`内部的对象应用改为最终结果
     *
     * @param getter 用于获取默认对象
     *
     * 例：
     *
     * ```kotlin
     *     class Sample {
     *         var test0: List<String>? = ArrayList()
     *         var test1: String? = null
     *     }
     * ```
     *
     * 上述代码中，`test0`的`getter`将会返回`test0`自身，`test1`的`getter`将会返回`null`
     *
     * 如果传入的`getter`为`null`表示不支持获取默认值
     *
     * @return 写入的结果，当写入过程发生异常时应当将异常包含在[RWResult]中而非直接抛出
     */
    fun readFromLocal(reader: IDataReader, value: Wrapper<T>, getter: (() -> T?)?): RWResult

}