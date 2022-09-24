package top.kmar.mi.api.register.others;

import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;
import top.kmar.mi.api.electricity.interfaces.IEleTransfer;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册托管.<br>
 * 使用该注解的类必须实现{@link IEleInputer}，{@link IEleOutputer}，{@link IEleTransfer}其中之一
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoAgentRegister {

	/** 用于接收注册时生成的实例的变量，留空为不保留实例 */
	String value() default "";

}