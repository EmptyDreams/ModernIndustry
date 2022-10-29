package top.kmar.mi.api.regedits.machines;

import top.kmar.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author EmptyDreams
 */
public final class RegisterHelp {
	
	/** 通过类名生成List */
	@Nonnull
	public static List<String> listOf(String modid, Class<?>... clazz) {
		List<String> result = new ArrayList<>(clazz.length);
		for (Class<?> aClass : clazz) {
			result.add(modid + ":" + aClass.getSimpleName());
		}
		return result;
	}
	
	/**
	 * <p>输出一个错误信息
	 * <p>格式：目标类[className]中的目标变量[fieldName]text \n throwableInfo
	 * @param clazz 目标类
	 * @param fieldName 变量名称
	 * @param text 文本
	 * @param throwable 发生的异常
	 */
	public static void errField(Class<?> clazz, String fieldName, String text, Throwable throwable) {
		errClass(clazz, "中的目标变量[" + fieldName + "]" + text, throwable);
	}
	
	/**
	 * <p>输出一个错误信息
	 * <p>格式：目标类[className]text \n throwableInfo
	 * @param clazz 目标类
	 * @param text 文本
	 * @param throwable 发生的异常
	 */
	public static void errClass(Class<?> clazz, String text, Throwable throwable) {
		if (throwable == null) MISysInfo.err("目标类[" + clazz.getName() + "]" + text);
		else MISysInfo.err("目标类[" + clazz.getName() + "]" + text, throwable);
	}
	
	/**
	 * 判断input中的所有类型能否转换为src中对应的类型
	 * @param src 源
	 * @param input 输入
	 */
	public static boolean matchClass(Class<?>[] src, Class<?>[] input) {
		if (src.length != input.length) return false;
		for (int i = 0; i < src.length; i++) {
			if (!src[i].isAssignableFrom(input[i])) return false;
		}
		return true;
	}
	
	/**
	 * 获取指定类中指定名称和参数的方法，与{@link Class#getDeclaredMethod(String, Class[])}不同，
	 * 该方法获取指定方法时只需要args中的参数可以转换为方法中的参数即可
	 * @param clazz 方法所在类
	 * @param methodName 方法名称
	 * @param args 方法参数类型
	 * @throws NoSuchMethodException 如果方法不存在
	 */
	@Nonnull
	public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] args)
			throws NoSuchMethodException {
		Method[] allMethods = clazz.getDeclaredMethods();
		for (Method it : allMethods) {
			if (it.getName().equals(methodName) && matchClass(it.getParameterTypes(), args)) {
				return it;
			}
		}
		throw new NoSuchMethodException();
	}
	
	/**
	 * 调用指定的静态方法（可私有）
	 * @param clazz 方法所在类
	 * @param methodName 方法名称
	 * @param args 参数，无参可填null或空数组
	 * @return 返回返回值
	 */
	public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object... args) {
		Class<?>[] argsClass = args == null ? new Class[0] : new Class[args.length];
		for (int i = 0; i < argsClass.length; i++) {
			argsClass[i] = args[i].getClass();
		}
		try {
			Method method = getDeclaredMethod(clazz, methodName, argsClass);
			if (!Modifier.isPublic(method.getModifiers())) method.setAccessible(true);
			return method.invoke(null, args);
		} catch (NoSuchMethodException e) {
			errClass(clazz, "不含有输入的方法{" + Arrays.toString(argsClass) + "}", e);
		} catch (InvocationTargetException e) {
			errClass(clazz, "调用的方法发生异常", e);
		} catch (IllegalAccessException e) {
			errClass(clazz, "方法无法访问", e);
		}
		return null;
	}
	
	/**
	 * 为指定类中一个变量赋值
	 * @param obj 指定类对象
	 * @param fieldName 变量名称
	 * @param value 值
	 * @return 是否没有发生异常
	 */
	public static boolean assignField(Object obj, String fieldName, Object value) {
		if (fieldName.length() == 0) return true;
		Class<?> clazz = obj.getClass();
		try {
			Field declaredField = clazz.getDeclaredField(fieldName);
			if (!Modifier.isPublic(declaredField.getModifiers())) {
				declaredField.setAccessible(true);
			}
			declaredField.set(obj, value);
			return true;
		} catch (NoSuchFieldException e) {
			errField(clazz, fieldName, "不存在", e);
		} catch (IllegalAccessException e) {
			errField(clazz, fieldName, "赋值失败", e);
		}
		return false;
	}
	
	/**
	 * 调用指定类的共有构造函数
	 * @param clazz 目标类
	 * @param args 构造函数的参数，无参可以填null或空的数组
	 * @param <T> 指定类的类型
	 * @return 若构造失败返回null
	 */
	@Nullable
	public static <T> T newInstance(Class<T> clazz, Object... args) {
		try {
			if (args == null || args.length == 0) {
				return clazz.newInstance();
			}
			Class<?>[] argsClass = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				argsClass[i] = args[i].getClass();
			}
			Constructor<T> constructor = clazz.getConstructor(argsClass);
			return constructor.newInstance(args);
		} catch (InstantiationException e) {
			errClass(clazz, "无法被初始化", e);
		} catch (IllegalAccessException e) {
			errClass(clazz, "构造函数无法访问", e);
		}catch (NoSuchMethodException e) {
			Class<?>[] argsClass = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				argsClass[i] = args[i].getClass();
			}
			errClass(clazz, "不含有输入的构造函数[" + Arrays.toString(argsClass), e);
		} catch (InvocationTargetException e) {
			errClass(clazz, "构造函数发生异常", e);
		}
		return null;
	}

}