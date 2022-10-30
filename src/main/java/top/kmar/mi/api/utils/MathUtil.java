package top.kmar.mi.api.utils;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 有关数学的计算
 * @author EmptyDreams
 */
public final class MathUtil {
	
	private static final Random RANDOM = new Random();
	
	/** 获取随机数 */
	@Nonnull
	public static Random random() {
		return RANDOM;
	}
	
}