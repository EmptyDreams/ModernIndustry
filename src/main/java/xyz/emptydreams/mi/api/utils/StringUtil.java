package xyz.emptydreams.mi.api.utils;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * 关于一些字符串操作的封装
 * @author EmptyDreams
 */
public class StringUtil {
	
	/** 判空检查 */
	public static <T> T checkNull(T object, String name) {
		return Objects.requireNonNull(object, () -> "name=" + name);
	}
	
	/**
	 * 向目标RL的Path中追加信息
	 * @param src 目标RL
	 * @param pathAddition 要追加的信息
	 * @return 修改后的RL
	 */
	@Nonnull
	public static ResourceLocation revampAddToRL(ResourceLocation src, String pathAddition) {
		return revampRL(src, src.getResourcePath() + pathAddition);
	}

	/**
	 * 将目标RL的Path替换为指定的path
	 * @param src 目标RL
	 * @param path 指定的path
	 * @return 修改后的RL
	 */
	@Nonnull
	public static ResourceLocation revampRL(ResourceLocation src, String path) {
		return new ResourceLocation(src.getResourceDomain(), path);
	}

	/**
	 * 合并两个字符串数组
	 * @return 如果参数任意一个为空则返回不为空的一个，如果两个都不为空则返回空。
	 *          如果其中一个数组长度为0则直接返回不为0的，如果两个数组长度都为0则返回arg0。
	 *          否则创建一个新的数组并返回
	 */
	public static String[] merge(String[] arg0, String[] arg1) {
		if (arg0 == null) return arg1;
		if (arg1 == null) return arg0;
		if (arg1.length == 0) return arg0;
		if (arg0.length == 0) return arg1;
		String[] result = new String[arg0.length + arg1.length];
		System.arraycopy(arg0, 0, result, 0, arg0.length);
		System.arraycopy(arg1, 0, result, arg0.length, arg1.length);
		return result;
	}

}
