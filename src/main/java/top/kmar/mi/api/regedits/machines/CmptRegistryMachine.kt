package top.kmar.mi.api.regedits.machines

import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.components.interfaces.CmptRegister
import top.kmar.mi.api.regedits.AutoRegisterMachine
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.MISysInfo

/**
 * GUI控件的注册机
 * @author EmptyDreams
 */
object CmptRegistryMachine : AutoRegisterMachine<AutoCmpt, Any>() {

    override fun getTargetClass() = AutoCmpt::class.java

    override fun registry(clazz: Class<*>, annotation: AutoCmpt, data: Any?) {
        if (!Cmpt::class.java.isAssignableFrom(clazz)) {
            if (ICmptClient::class.java.isAssignableFrom(clazz))
                return MISysInfo.err("注册控件类时应当注册服务端类而非客户端类")
            return MISysInfo.err("控件类必须从Cmpt类继承")
        }
        @Suppress("UNCHECKED_CAST")
        CmptRegister.registry(annotation.value, clazz as Class<Cmpt>)
    }

}