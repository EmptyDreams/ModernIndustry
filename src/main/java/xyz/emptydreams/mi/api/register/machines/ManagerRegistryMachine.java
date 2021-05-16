package xyz.emptydreams.mi.api.register.machines;

import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.others.RegisterManager;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.*;

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