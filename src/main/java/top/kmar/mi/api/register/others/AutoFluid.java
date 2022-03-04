package top.kmar.mi.api.register.others;

import net.minecraft.creativetab.CreativeTabs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动注册流体
 * @author EmptyDreams
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoFluid {
	
	/**
	 * 用于盛放流体对应的Block类的字段，默认为"block"
	 */
	String value() default "block";
	
	/**
	 * 流体桶所在的tab，该名称应该为一个在该类中用于获取{@link CreativeTabs}对象的静态共有方法
	 */
	String creativeTab() default "getBlockCreativeTab";
	
	/**
	 * 流体的本地名称，默认为modid.[fluid]
	 */
	String unlocalizedName() default "";
	
	/**
	 * <p>流体方块构建完毕后触发的方法.
	 * <p>格式：{@code public static void ***()}
	 */
	String end() default "";
	
}