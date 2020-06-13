package xyz.emptydreams.mi.register.item;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xyz.emptydreams.mi.ModernIndustry;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于自动注册物品，
 * 注意：此接口注册物品的优先级小于在{@link ItemRegister}类中声明的物品
 * @author EmptyDreams
 * @version V1.0
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface AutoItemRegister {

	/** 物品名称（不包括MOD ID） */
	String value();

	String ID = ModernIndustry.MODID;
	
	/** MOD ID */
	String ID() default ID;
	
	/** 将注册的物品对象存储到该类中的某个对象上 */
	String object() default "";
	
}
