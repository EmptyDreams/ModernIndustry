package top.kmar.mi.api.register.machines

import top.kmar.mi.api.auto.interfaces.IAutoFieldRW
import top.kmar.mi.api.auto.interfaces.IAutoObjRW
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.register.AutoRegisterMachine
import top.kmar.mi.api.register.others.AutoRWType
import top.kmar.mi.api.utils.MISysInfo

/**
 * 读写器的注册机
 * @author EmptyDreams
 */
class AutoTypeRegistryMachine : AutoRegisterMachine<AutoRWType, Any>() {

    override fun getTargetClass() = AutoRWType::class.java

    override fun registry(clazz: Class<*>, annotation: AutoRWType, data: Any?) {
        val value = try {
            clazz.getMethod(annotation.name)(null)
        } catch (e: Throwable) {
            return MISysInfo.err("构造读写器对象时发生异常", e)
        }
        var op = false
        if (value is IAutoFieldRW) {
            AutoTypeRegister.registry(value, annotation.value)
            op = true
        }
        if (value is IAutoObjRW<*>) {
            AutoTypeRegister.registry(value, annotation.value)
            op = true
        }
        if (!op)
            MISysInfo.err("[AutoSaveRegistryMachine]: 指定类没有实现任何一个类型的读写器（${clazz.name}）")
    }

}