package minedreams.mi.register.trusteeship;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册托管.<br>
 * 使用该注解的类必须实现{@link IEleInputer}，{@link IEleOutputer}，{@link IEleTransfer}其中之一
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoTrusteeshipRegister {
}
