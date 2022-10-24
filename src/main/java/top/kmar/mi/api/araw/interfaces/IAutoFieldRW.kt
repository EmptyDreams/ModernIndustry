package top.kmar.mi.api.araw.interfaces

import net.minecraft.nbt.NBTBase
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
     * 将指定类的内容写入到[NBTBase]中
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]、[allowFinal]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param field 要写入的数据
     * @param obj 持有该`field`的类的对象
     *
     * @return 要写入的数据，返回`null`表示跳过写入
     */
    fun write2Local(field: Field, obj: Any): NBTBase?

    /**
     * 从[NBTBase]中读取数据
     *
     * 条例：
     * 1. 调用该函数前请在外部通过[match]、[allowFinal]检查是否满足读写条件
     * 2. 该函数不应抛出异常（但可以抛出由于用户错误修改注解参数导致的类型转换异常）
     *
     * @param reader 承载数据的`reader`
     * @param field 属性的`Field`
     * @param obj 持有该`field`的类的对象
     */
    fun read2Obj(reader: NBTBase, field: Field, obj: Any)

}