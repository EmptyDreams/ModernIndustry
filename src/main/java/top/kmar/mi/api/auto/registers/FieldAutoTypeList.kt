package top.kmar.mi.api.auto.registers

import top.kmar.mi.api.auto.interfaces.IAutoFieldRW
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass

/**
 * 读写器列表
 * @author EmptyDreams
 */
class FieldAutoTypeList {

    private val machineList = LinkedList<IAutoFieldRW>()

    /**
     * 尝试匹配一个读写器
     * @return 没有匹配到则返回`null`
     */
    fun match(field: Field): IAutoFieldRW? {
        machineList.forEach { if (it.match(field)) return it }
        return null
    }

    /** 注册一个读写器 */
    fun registry(value: IAutoFieldRW) {
        machineList.add(value)
    }

    /**
     * 删除指定读写器
     *
     * @param value 读写器的`KClass`对象
     */
    fun deleteValue(value: KClass<out IAutoFieldRW>) {
        machineList.removeIf { it::class == value }
    }

}