package top.kmar.mi.api.araw.registers

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import top.kmar.mi.api.araw.interfaces.IAutoFieldRW
import top.kmar.mi.api.araw.interfaces.IAutoObjRW
import java.lang.reflect.Field
import javax.activation.UnsupportedDataTypeException
import kotlin.reflect.KClass

/**
 * 自动数据存储的注册机
 * @author EmptyDreams
 */
object AutoTypeRegister {

    /** 基本类型的读写器默认优先级 */
    const val BASE_TYPE = 100
    /** 基本类型的数组读写器默认优先级 */
    const val BASE_ARRAY_TYPE = 500
    /** 一般类型读写器默认优先级 */
    const val VALUE_TYPE = 2000
    /** 通用类型的读写器的默认优先级 */
    const val GENERAL_TYPE = 1000000000

    private val fieldList = Int2ObjectRBTreeMap<FieldAutoTypeList>()
    private val objList = Int2ObjectRBTreeMap<ObjAutoTypeList>()

    /**
     * 注册一个读写器
     * @param machine 读写器对象
     * @param priority 读写器优先级，数字越小优先级越高
     */
    fun registry(machine: IAutoFieldRW, priority: Int) {
        fieldList.computeIfAbsent(priority) { FieldAutoTypeList() }.registry(machine)
    }

    /**
     * 注册一个读写器
     * @param machine 读写器对象
     * @param priority 读写器优先级，数字越小优先级越高
     */
    fun registry(machine: IAutoObjRW<*>, priority: Int) {
        objList.computeIfAbsent(priority) { ObjAutoTypeList() }.registry(machine)
    }

    /**
     * 注册一个读写器
     * @param priority 读写器优先级，数字越小优先级越高
     * @param builder 读写器对象构建器
     */
    fun registryField(priority: Int, builder: () -> IAutoFieldRW) {
        registry(builder(), priority)
    }

    /**
     * 注册一个读写器
     * @param priority 读写器优先级，数字越小优先级越高
     * @param builder 读写器对象构建器
     */
    fun registryObj(priority: Int, builder: () -> IAutoObjRW<*>) {
        registry(builder(), priority)
    }

    /** 匹配读写器 */
    fun match(field: Field): IAutoFieldRW {
        for ((_, list) in fieldList) return list.match(field) ?: continue
        throw UnsupportedDataTypeException("不支持指定类型[${field.type}]的读写")
    }

    /** 匹配读写器 */
    fun match(type: KClass<*>): IAutoObjRW<Any> {
        for ((_, list) in objList) {
            @Suppress("UNCHECKED_CAST")
             return list.match(type) as IAutoObjRW<Any>? ?: continue
        }
        throw UnsupportedDataTypeException("不支持指定类型[$type]的读写")
    }

    /**
     * 删除指定的读写器
     * @param value 读写器的`KClass`对象
     */
    fun deleteFieldValue(value: KClass<out IAutoFieldRW>) {
        for ((_, list) in fieldList) list.deleteValue(value)
    }

    /**
     * 删除指定的读写器
     * @param value 读写器的`KClass`对象
     */
    fun deleteObjValue(value: KClass<out IAutoObjRW<*>>) {
        for ((_, list) in objList) list.deleteValue(value)
    }

}