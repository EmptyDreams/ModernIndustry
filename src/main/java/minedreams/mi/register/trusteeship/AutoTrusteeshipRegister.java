package minedreams.mi.register.trusteeship;

import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;
import minedreams.mi.api.electricity.interfaces.IRegister;

/**
 * 自动注册托管
 */
public @interface AutoTrusteeshipRegister {
	
	/**
	 * 传入的类必须实现{@link IEleInputer}，{@link IEleOutputer}，{@link IEleTransfer}其中之一
	 */
	Class<? extends IRegister> value();
	
}
