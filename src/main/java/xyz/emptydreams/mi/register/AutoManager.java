package xyz.emptydreams.mi.register;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册类中所有被(public static)修饰的元素
 * @author EmptyDreams
 * @version 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoManager {
	
	/** 是否注册{@link net.minecraft.block.Block} */
	boolean block();
	
	/** 是否注册{@link net.minecraft.item.Item} */
	boolean item();
	
}
