package xyz.emptydreams.mi.api.register.machines;

import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleTransfer;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.agent.AutoAgentRegister;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.assignField;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.errClass;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.newInstance;

/**
 * 代理类注册机
 * @author EmptyDreams
 */
public class AgentRegistryMachine extends AutoRegisterMachine<AutoAgentRegister, Object> {
	
	@Nonnull
	@Override
	public Class<AutoAgentRegister> getTargetClass() {
		return AutoAgentRegister.class;
	}
	
	@Override
	public void registry(Class<?> clazz, AutoAgentRegister annotation, Object data) {
		Object o = newInstance(clazz, (Object[]) null);
		if (o == null) return;
		assignField(o, annotation.value(), o);
		boolean isTrue = false;
		if (o instanceof IEleTransfer) {
			EleWorker.registerTransfer((IEleTransfer) o);
			isTrue = true;
		}
		if (o instanceof IEleOutputer) {
			EleWorker.registerOutputer((IEleOutputer) o);
			isTrue = true;
		}
		if (o instanceof IEleInputer) {
			EleWorker.registerInputer((IEleInputer) o);
			isTrue = true;
		}
		if (!isTrue) errClass(clazz, "没有实现任意一个代理类", null);
	}
	
}