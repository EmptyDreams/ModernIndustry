package xyz.emptydreams.mi.api.utils.data.te;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>用于标志需要被离线的数据，不能被static修饰.
 * <p>支持且仅支持在{@link DataTypeRegister}中注册的数据类型
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface Storage {
	
	/**
	 * 目的读写类型，<code>Object.class</code>表示不进行数据转换
	 * @return 目的类型
	 */
	Class<?> value() default Object.class;
	
}