package xyz.emptydreams.mi.register;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注册管理类，该类允许用户添加自己的注册机制，
 * 类中必须定义静态方法(可为私有)：{@code register(Class<?>)}，
 * 若需注册物品/方块需调用{@link AutoRegister}中的方法
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface RegisterManager {
}
