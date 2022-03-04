package top.kmar.mi.api.utils;

import net.minecraft.util.ResourceLocation;
import top.kmar.mi.ModernIndustry;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 关于一些字符串操作的封装
 * @author EmptyDreams
 */
public class StringUtil {
	
	/**
	 * 将字符串数组转化为Class数组
	 * @param names 类名数组
	 * @throws ClassNotFoundException 如果数组中有任意一个类不存在
	 */
	@Nonnull
	public static Class<?>[] castToClass(String... names) throws ClassNotFoundException {
		Class<?>[] clazz = new Class<?>[names.length];
		for (int i = 0; i < names.length; i++) {
			clazz[i] = Class.forName(names[i]);
		}
		return clazz;
	}
	
	/**
	 * <p>根据字符串获取一个{@link Method}对象
	 * <p>字符串格式：[类名]#[方法名](参数1,参数2)
	 * <p>例：xyz.emptydreams.mi.api.utils.StringUtil#checkNull(java.lang.Object,java.lang.String)
	 * <p>注：指定的方法必须为共有静态方法，字符串中无空格
	 * @param name 字符串
	 * @throws ClassNotFoundException 如果类不存在
	 * @throws NoSuchMethodException 如果方法不存在
	 * @throws IllegalArgumentException 如果字符串不合法
	 */
	@Nonnull
	public static Method getMethod(String name) throws ClassNotFoundException, NoSuchMethodException {
		int nameIndex = name.indexOf('#');
		int argIndex = name.indexOf("(");
		
		if (!name.endsWith(")")) throw new IllegalArgumentException("字符串应该以')'结尾：" + name);
		if (nameIndex == -1) throw new IllegalArgumentException("字符串中没有包含'#'：" + name);
		if (argIndex == -1) throw new IllegalArgumentException("字符串中没有包含'('：" + name);
		
		String className = name.substring(0, nameIndex);
		String methodName = name.substring(nameIndex + 1, argIndex);
		Class<?>[] argList = castToClass(name.substring(argIndex + 1, name.length() - 1).split(","));
		Class<?> clazz = Class.forName(className);
		return clazz.getMethod(methodName, argList);
	}
	
	/** 判空检查 */
	public static <T> T checkNull(T object, String name) {
		return Objects.requireNonNull(object, () -> "name=" + name);
	}
	
	/**
	 * 向目标ResourceLocation的Path中追加信息
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
	
	public static String getUnlocalizedName(String modid, String name) {
		return modid + "." + name;
	}
	
	public static String getUnlocalizedName(String name) {
		return getUnlocalizedName(ModernIndustry.MODID, name);
	}
	
}