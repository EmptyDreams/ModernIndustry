package top.kmar.mi.api.regedits.machines;

import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.others.RegisterManager;

import javax.annotation.Nonnull;

import static top.kmar.mi.api.regedits.machines.RegisterHelp.*;

/**
 * 管理类注册机
 * @author EmptyDreams
 */
public class ManagerRegistryMachine extends AutoRegisterMachine<RegisterManager, Object> {

    @Nonnull
    @Override
    public Class<RegisterManager> getTargetClass() {
        return RegisterManager.class;
    }

    @Override
    public void registry(Class<?> clazz, RegisterManager annotation, Object data) {
        invokeStaticMethod(clazz, annotation.value(), (Object[]) null);
    }

}