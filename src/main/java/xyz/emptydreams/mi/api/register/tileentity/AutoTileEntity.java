package xyz.emptydreams.mi.api.register.tileentity;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册TE
 * @author EmptyDremas
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoTileEntity {
	
	String value();
	
}
