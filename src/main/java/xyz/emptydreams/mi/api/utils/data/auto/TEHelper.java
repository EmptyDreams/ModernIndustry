package xyz.emptydreams.mi.api.utils.data.auto;

import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.dor.ClassDataOperator;
import xyz.emptydreams.mi.api.dor.IClassData;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动化的TE数据处理
 * @author EmptyDreams
 */
public interface TEHelper extends IClassData {
	
	@Override
	default boolean needOperate(Field field) {
		int mod = field.getModifiers();
		//不对静态及终态的field进行读写
		return field.isAnnotationPresent(Storage.class)
				&& (!(Modifier.isStatic(mod) || Modifier.isFinal(mod)));
	}
	
	/**
	 * 向指定NBT写入需要自动写入的数据
	 */
	default NBTTagCompound writeToNBT(NBTTagCompound data) {
		if (WorldUtil.isClient()) return data;
		ClassDataOperator dor = new ClassDataOperator(this);
		dor.writeAll();
		dor.writeToNBT(data);
		return data;
	}
	
	/** 读取数据 */
	default void readFromNBT(NBTTagCompound data) {
		if (WorldUtil.isClient()) return;
		ClassDataOperator dor = new ClassDataOperator(this, data);
		dor.readAll();
	}
	
	/**
	 * <p>用于标志需要被离线的数据，不能被static修饰.
	 * <p>支持且仅支持在{@link DataTypeRegister}中注册的数据类型
	 */
	@Documented
	@Retention(RUNTIME)
	@Target(ElementType.FIELD)
	@interface Storage { }
	
}