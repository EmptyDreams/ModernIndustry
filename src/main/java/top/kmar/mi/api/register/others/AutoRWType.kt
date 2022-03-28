package top.kmar.mi.api.register.others

/**
 * 自动数据读写器注册机
 * @author EmptyDreams
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AutoRWType(
    /** 优先级 */
    val value: Int
)