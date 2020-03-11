package minedreams.mi.register.block;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于自动注册方块，其中所有有默认值的量都有对应的静态常量
 * @author EmptyDremas
 * @version V1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoBlockRegister {
	
	/**
	 * 方块的注册名称
	 */
	String registryName();
	
	/**
	 * 方块的unlocalized名称，""表示与registery名称保持一致
	 * @return
	 */
	String unlocalizedName() default "";
	
	//String TOOL = "pickaxe";
	
	/**
	 * 方块挖掘工具，默认为镐子
	 */
	//String tool() default TOOL;
	
	//int LEVEL = 1;
	
	/**
	 * 方块挖掘级别
	 * @see net.minecraft.block.Block#setHarvestLevel(String, int)
	 */
	//int level() default LEVEL;
	
	//float HARDNEXX = 3.5F;
	
	/**
	 * 挖掘方块所需要的命中数
	 */
	//float hardnexx() default HARDNEXX;
	
	//boolean TAB = true;
	
	/**
	 * 是否自动设置创造模式物品栏
	 */
	//boolean tab() default TAB;
	
	Class<?> REGISTER = AutoBlockRegister.class;
	
	/**
	 * 注册物品所用的函数地址，如果为自动注册则为
	 * minedreams.mi.register.block.AutoBlockRegister.class
	 */
	Class<?> register() default AutoBlockRegister.class;
	
}
