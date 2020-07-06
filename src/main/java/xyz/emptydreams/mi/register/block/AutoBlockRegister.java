package xyz.emptydreams.mi.register.block;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于自动注册方块，其中所有有默认值的量都有对应的静态常量
 * @author EmptyDremas
 * @version V1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoBlockRegister {
	
	/**
	 * 方块的注册名称
	 */
	String registryName();
	
	/** 方块的unlocalized名称，""表示与registry名称保持一致 */
	String unlocalizedName() default "";
	
	Class<?> REGISTER = AutoBlockRegister.class;
	
	/**
	 * 注册物品所用的函数地址，如果为自动注册则为
	 * AutoBlockRegister.class
	 */
	Class<?> register() default AutoBlockRegister.class;
	
}
