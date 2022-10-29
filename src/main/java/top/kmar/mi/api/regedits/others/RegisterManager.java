package top.kmar.mi.api.regedits.others;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注册管理类，该类允许用户添加自己的注册机制
 * @author EmptyDreams
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface RegisterManager {
	
	/** 注册时调用的方法名称（无参，可私有） */
	String value() default "registry";
	
}