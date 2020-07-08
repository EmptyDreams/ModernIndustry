package xyz.emptydreams.mi.register.block;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 矿石注册类，目前有明显的BUG
 * @author EmptyDremas
 * @version V1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface OreCreate {

	int COUNT = 8;
	int TIME = 4;
	int Y_MIN = 16;
	int Y_RANGE = 64;
	float PROBABILITY = 0.8F;
	
	/** 生成规模 */
	int count() default 8;
	/** 生成次数 */
	int time() default 4;
	/** 最低高度 */
	int yMin() default 16;
	/** 高度范围 */
	int yRange() default 64;
	/** 生成成功几率 */
	float probability() default 1.0F;
	/** 方块名称（不包括MODID） */
	String name();
	
}
