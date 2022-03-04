package top.kmar.mi.content.items.debug;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link ClassInfoViewer}遇到被该注解所注释的类时，会展开显示其中的数据
 * @author EmptyDreams
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface DebugDetails {
}