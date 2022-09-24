package top.kmar.mi.api.register.machines

import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.CmptRegister
import top.kmar.mi.api.register.AutoRegisterMachine
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.MISysInfo

/**
 * GUI控件的注册机
 * @author EmptyDreams
 */
object CmptRegistryMachine : AutoRegisterMachine<AutoCmpt, Any>() {

    override fun getTargetClass() = AutoCmpt::class.java

    override fun registry(clazz: Class<*>, annotation: AutoCmpt, data: Any?) {
        if (!Cmpt::class.java.isAssignableFrom(clazz)) {
            if (CmptClient::class.java.isAssignableFrom(clazz))
                return MISysInfo.err("注册控件类时应当注册服务端类而非客户端类")
            return MISysInfo.err("控件类必须从Cmpt类继承")
        }
        @Suppress("UNCHECKED_CAST")
        CmptRegister.registry(annotation.tag, clazz as Class<Cmpt>)
    }

}