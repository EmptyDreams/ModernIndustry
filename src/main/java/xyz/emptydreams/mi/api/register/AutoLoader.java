package xyz.emptydreams.mi.api.register;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 在加载时自动触发类的加载
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoLoader {
}
