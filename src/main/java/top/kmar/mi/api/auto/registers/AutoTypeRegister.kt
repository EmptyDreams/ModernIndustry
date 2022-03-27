package top.kmar.mi.api.auto.registers

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 自动数据存储的注册机
 * @author EmptyDreams
 */
object AutoTypeRegister {

    private val machineList = Int2ObjectRBTreeMap<AutoTypeList>()

    /**
     * 注册一个读写器
     * @param machine 读写器对象
     * @param priority 读写器优先级，数字越小优先级越高
     */
    fun registry(machine: IAutoRW, priority: Int) {
        machineList.computeIfAbsent(priority) { AutoTypeList() }.registry(machine)
    }

    /**
     * 注册一个读写器
     * @param priority 读写器优先级，数字越小优先级越高
     * @param builder 读写器对象构建器
     */
    fun registry(priority: Int, builder: () -> IAutoRW) {
        registry(builder(), priority)
    }

    /** 读写器 */
    fun match(field: Field): IAutoRW? {
        for ((_, list) in machineList) return list.match(field) ?: continue
        return null
    }

    /**
     * 删除指定的读写器
     * @param value 读写器的`KClass`对象
     */
    fun deleteValue(value: KClass<out IAutoRW>) {
        for ((_, list) in machineList) {
            list.deleteValue(value)
        }
    }

}