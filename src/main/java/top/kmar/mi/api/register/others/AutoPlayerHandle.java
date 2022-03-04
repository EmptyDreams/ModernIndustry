package top.kmar.mi.api.register.others;

import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.net.message.player.IPlayerHandle;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册有该注解的{@link IPlayerHandle}
 * @author EmptyDreams
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoPlayerHandle {
	
	String modid() default ModernIndustry.MODID;
	
	/** 名称 */
	String value();
	
}