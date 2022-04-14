package top.kmar.mi.api.araw.registers

import top.kmar.mi.api.araw.interfaces.IAutoObjRW
import java.util.*
import kotlin.reflect.KClass

/**
 * 读写器列表
 * @author EmptyDreams
 */
class ObjAutoTypeList {

    private val machineList = LinkedList<IAutoObjRW<*>>()

    /**
     * 尝试匹配一个读写器
     * @return 没有匹配到则返回`null`
     */
    fun match(type: KClass<*>): IAutoObjRW<*>? {
        machineList.forEach { if (it.match(type)) return it }
        return null
    }

    /** 注册一个读写器 */
    fun registry(value: IAutoObjRW<*>) {
        machineList.add(value)
    }

    /**
     * 删除指定读写器
     *
     * @param value 读写器的`KClass`对象
     */
    fun deleteValue(value: KClass<out IAutoObjRW<*>>) {
        machineList.removeIf { it::class == value }
    }

}