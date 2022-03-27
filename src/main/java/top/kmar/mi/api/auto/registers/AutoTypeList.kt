package top.kmar.mi.api.auto.registers

import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass

/**
 * 读写器列表
 * @author EmptyDreams
 */
class AutoTypeList {

    private val machineList = LinkedList<IAutoRW>()

    /**
     * 尝试匹配一个读写器
     * @return 没有匹配到则返回`null`
     */
    fun match(field: Field): IAutoRW? {
        machineList.forEach { if (it.match(field)) return it }
        return null
    }

    /** 注册一个读写器 */
    fun registry(value: IAutoRW) {
        machineList.add(value)
    }

    /**
     * 删除指定读写器
     *
     * @param value 读写器的`KClass`对象
     */
    fun deleteValue(value: KClass<out IAutoRW>) {
        machineList.removeIf { it::class == value }
    }

}