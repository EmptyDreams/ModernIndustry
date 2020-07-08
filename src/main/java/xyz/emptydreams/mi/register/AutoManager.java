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
	boolean block() default false;

	/**
	 * 方块是否拥有自定义注册，若为true则在方块进入注册表后调用
	 * {@code public static blockCustom(Block)}方法。<br>
	 * 如果方法不存在会抛出{@link NoSuchMethodException}
	 */
	boolean blockCustom() default false;

	/** 是否注册{@link net.minecraft.item.Item} */
	boolean item() default false;

	/**
	 * 物品是否拥有自定义注册，若为true则在物品进入注册表后调用
	 * {@code public static blockCustom(Item)}方法。<br>
	 * 如果方法不存在会抛出{@link NoSuchMethodException}
	 */
	boolean itemCustom() default false;

}
