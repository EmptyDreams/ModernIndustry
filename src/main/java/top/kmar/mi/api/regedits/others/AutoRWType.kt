package top.kmar.mi.api.regedits.others

/**
 * 自动数据读写器注册机
 * @author EmptyDreams
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AutoRWType(
    /** 优先级 */
    val value: Int,
    /**
     * **注册机调用的函数的名字**
     *
     * # 条例：
     * 1. 函数应当是共有的（`public`）
     * 2. 函数应当是静态的（`static`）
     * 3. 函数的参数列表应当为空
     * 4. 函数不应当返回`null`
     * 5. 函数返回值为可以转化为读写器接口的任意类型
     *
     * ## 函数格式样例：
     *
     * ### Kotlin:
     *
     * ```kotlin
     *  @AutoRWType(100)
     *  object Simple0 : IAutoFieldRW {
     *
     *      @JvmStatic fun instance() = Simple0
     *
     *  }
     * ```
     *
     * ### Java:
     *
     * ```java
     * @AutoRWType(value = 100, name = "create")
     * public class Simple1 implements IAutoFieldRW {
     *
     *      public static Simple1 create() {
     *          return new Simple1();
     *      }
     *
     * }
     * ```
     */
    val name: String = "INSTANCE"
)