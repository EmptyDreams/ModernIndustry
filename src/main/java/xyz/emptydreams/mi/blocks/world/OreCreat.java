package xyz.emptydreams.mi.blocks.world;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 矿石注册类，目前有明显的BUG
 * @author EmptyDremas
 * @version V1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface OreCreat {

	int COUNT = 8;
	int TIME = 4;
	int YMIN = 16;
	int YRANGE = 64;
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