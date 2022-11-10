package top.kmar.mi.api.regedits.others;

import top.kmar.mi.api.net.messages.player.IPlayerHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册有该注解的{@link IPlayerHandler}
 * @author EmptyDreams
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoPlayerHandler {
    
    /** key 值 */
    String value();
    
    /** 静态全局变量名称 */
    String field() default "INSTANCE";
    
}