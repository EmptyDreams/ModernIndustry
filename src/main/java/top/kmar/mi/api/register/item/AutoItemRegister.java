package top.kmar.mi.api.register.item;

import net.minecraftforge.client.event.ModelRegistryEvent;
import top.kmar.mi.ModernIndustry;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于自动注册物品，
 * 注意：此接口注册物品的优先级小于在{@link ItemRegister}类中声明的物品
 * @author EmptyDreams
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface AutoItemRegister {

	/** 物品名称（不包括MOD ID） */
	String value();

	/** 本地名称，留空为自动 */
	String unlocalizedName() default "";

	/** 矿物词典，留空为不添加矿物词典 */
	String[] oreDic() default { };
	
	/** MOD ID */
	String modid() default ModernIndustry.MODID;
	
	/** 将注册的物品对象存储到该类中的某个对象上 */
	String field() default "";
	
	/**
	 * <p>填写自定义CustomModel的方法名称，该方法必须在物品类中
	 * <p>填写"null"表明不调用任何方法
	 * <p>例如：{@code public static void customModel(Item item)}
	 * <p>默认注册：{@link ItemRegister#registryModel(ModelRegistryEvent)}
	 */
	String model() default "";
	
}