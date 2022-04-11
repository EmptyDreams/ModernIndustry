package top.kmar.mi.api.auto.interfaces

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import java.lang.reflect.Field

/**
 * 使用[Field]的数据读写器接口
 *
 * 该接口的子类不应当是匿名类，这会导致无法删除该读写器
 *
 * @author EmptyDreams
 */
interface IAutoFieldRW : IAutoMachine {

    /** 指明该类型的读写器是否允许对不可变类型进行读写 */
    fun allowFinal(): Boolean

    /**
     * 判断指定数据是否可以被当前读写器捕获
     *
     * 函数保证在被调用时存在[AutoSave]注解
     */
    fun match(field: Field): Boolean

    /**
     * 将指定类的内容写入到[IDataWriter]中
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]、[allowFinal]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param writer 承载数据的`writer`，函数内部不允许修改该`writer`的已有数据
     * @param field 要写入的数据
     * @param obj 持有该`field`的类的对象
     *
     * @return 写入的结果，当写入过程发生异常时应当将异常包含在[RWResult]中而非直接抛出
     */
    fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult

    /**
     * 从[IDataReader]中读取数据
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]、[allowFinal]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param reader 承载数据的`reader`，函数保证读取完毕后`reader`的指针处于下一个数据的开头
     * @param field 属性的`Field`
     * @param obj 持有该`field`的类的对象
     *
     * @return 读取的结果，当写入过程发生异常时应当将异常包含在[RWResult]中而非直接抛出
     */
    fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult

}