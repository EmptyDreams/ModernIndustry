package xyz.emptydreams.mi.api.register.others;

import xyz.emptydreams.mi.ModernIndustry;

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
	
	String modid() default ModernIndustry.MODID;
	
}