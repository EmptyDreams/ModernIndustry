package minedreams.mi.blocks.register;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于自动注册方块，其中所有有默认值的量都有对应的静态常量
 * @author EmptyDremas
 * @version V1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface BlockAutoRegister {
	
	/**
	 * 方块名称
	 */
	int name();
	
	/**
	 * 方块的注册名称，UnlocalizedName会自动初始化
	 */
	String registryName();
	
	String TOOL = "pickaxe";
	
	/**
	 * 方块挖掘工具，默认为镐子
	 */
	String tool() default TOOL;
	
	int LEVEL = 1;
	
	/**
	 * 方块挖掘级别
	 * @see net.minecraft.block.Block#setHarvestLevel(String, int)
	 */
	int level() default LEVEL;
	
	float HARDNEXX = 3.5F;
	
	/**
	 * 挖掘方块所需要的命中数
	 */
	float hardnexx() default HARDNEXX;
	
	boolean TAB = true;
	
	/**
	 * 是否自动设置创造模式物品栏
	 */
	boolean tab() default TAB;
	
	Class<?> REGISTER = BlockAutoRegister.class;
	
	/**
	 * 注册物品所用的函数地址，如果为自动注册则为
	 * minedreams.mi.blocks.register.BlockAutoRegister.class
	 */
	Class<?> register() default BlockAutoRegister.class;
	
}
