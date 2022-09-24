package top.kmar.mi.api.register.block.annotations;

import net.minecraftforge.client.event.ModelRegistryEvent;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.register.block.BlockRegister;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于自动注册方块，其中所有有默认值的量都有对应的静态常量
 * @author EmptyDremas
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoBlockRegister {

	/** modid */
	String modid() default ModernIndustry.MODID;

	/** 方块的注册名称 */
	String registryName();
	
	/** 方块的unlocalized名称，""表示与registry名称保持一致 */
	String unlocalizedName() default "";

	/** 矿物词典，留空为不添加 */
	String[] oreDic() default { };

	Class<?> REGISTER = AutoBlockRegister.class;
	
	/**
	 * 注册物品所用的函数地址，如果为自动注册则为
	 * AutoBlockRegister.class
	 */
	Class<?> register() default AutoBlockRegister.class;

	/** 用于接收注册时生成的实例的变量，留空为不保留实例 */
	String field() default "";
	
	/**
	 * <p>填写自定义CustomModel的方法名称，该方法必须在方块类中
	 * <p>填写"null"表明不调用任何方法
	 * <p>例如：{@code public static void customModel(Block block, Item item)}
	 * <p>其中block为需要注册的方块，item为方块对应的物品对象
	 * <p>默认注册：{@link BlockRegister#registryModel(ModelRegistryEvent)}
	 */
	String model() default "";
	
}