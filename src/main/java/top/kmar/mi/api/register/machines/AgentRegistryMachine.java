package top.kmar.mi.api.register.machines;

import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;
import top.kmar.mi.api.electricity.interfaces.IEleTransfer;
import top.kmar.mi.api.register.AutoRegisterMachine;
import top.kmar.mi.api.register.others.AutoAgentRegister;

import javax.annotation.Nonnull;

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
		Object o = RegisterHelp.newInstance(clazz, (Object[]) null);
		if (o == null) return;
		RegisterHelp.assignField(o, annotation.value(), o);
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
		if (!isTrue) RegisterHelp.errClass(clazz, "没有实现任意一个代理类", null);
	}
	
}