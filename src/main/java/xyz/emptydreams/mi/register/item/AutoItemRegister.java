package xyz.emptydreams.mi.register.item;

import xyz.emptydreams.mi.ModernIndustry;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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

	/** 本地名称，留空为自动 */
	String unlocalizedName() default "";

	/** 矿物词典，留空为不添加矿物词典 */
	String[] oreDic() default { };
	
	/** MOD ID */
	String modid() default ModernIndustry.MODID;
	
	/** 将注册的物品对象存储到该类中的某个对象上 */
	String field() default "";
	
}
