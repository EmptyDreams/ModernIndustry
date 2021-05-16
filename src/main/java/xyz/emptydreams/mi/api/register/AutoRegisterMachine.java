package xyz.emptydreams.mi.api.register;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 类型注册器
 * @param <V> 注解类型
 * @param <T> 存储数据的类型
 * @author EmptyDreams
 */
public abstract class AutoRegisterMachine<V extends Annotation, T>
						implements Comparable<AutoRegisterMachine<?, ?>> {
	
	/**
	 * 进行注册前提前解析ASM
	 * @return 解析后的内容
	 */
	@Nullable
	public T parse(ASMDataTable asm) {
		return null;
	}
	
	/**
	 * 获取需要处理的注解
	 * @return 返回的类型必须为注解类
	 */
	@Nonnull
	public abstract Class<V> getTargetClass();
	
	@SuppressWarnings("unchecked")
	public void registryAll(ASMDataTable asm) {
		T data = parse(asm);
		Class<?> cache = getTargetClass();
		if (!cache.isAnnotation())
			throw new IllegalArgumentException("getTargetClass方法返回了非注解的class");
		Class<? extends Annotation> annotation = (Class<? extends Annotation>) cache;
		Set<ASMDataTable.ASMData> dataSet = asm.getAll(annotation.getName());
		for (ASMDataTable.ASMData asmData : dataSet) {
			try {
				Class<?> clazz = Class.forName(asmData.getClassName());
				V an = (V) clazz.getAnnotation(annotation);
				registry(clazz, an, data);
			} catch (Throwable e) {
				MISysInfo.err("注册[" + asmData.getClassName() + "]时遇到意料之外的错误", e);
			}
		}
	}
	
	/**
	 * 注册指定类. <br>不允许抛出异常，如有异常必须在方法内部处理</br>
	 * @param clazz 被注解注释的类
	 * @param annotation 注解对象
	 * @param data 附加数据
	 */
	public abstract void registry(Class<?> clazz, V annotation, T data);
	
	/**
	 * <p>获取该注册机的依赖项.
	 * <p>系统会保证该注册机的依赖项先于该注册机执行
	 * @return 包含依赖注册机的注册名称
	 */
	@Nonnull
	public Collection<String> getDependency() {
		return Collections.emptyList();
	}
	
	/** 判断当前注册机是否依赖指定注册机 */
	public final boolean isDependency(AutoRegisterMachine<?, ?> register) {
		for (String key : getDependency()) {
			AutoRegisterMachine<?, ?> dependency = AutoRegister.getInstance(key);
			if (dependency.equals(register)) return true;
			boolean test = dependency.isDependency(register);
			if (test) return true;
		}
		return false;
	}
	
	@Override
	public final int compareTo(AutoRegisterMachine<?, ?> register) {
		return isDependency(register) ? 1 : register == this ? 0 : -1;
	}
}