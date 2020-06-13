package xyz.emptydreams.mi.register.te;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册TE
 * @author EmptyDremas
 * @version V1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoTileEntity {
	
	String value();
	
}
