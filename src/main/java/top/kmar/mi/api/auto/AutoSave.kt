package top.kmar.mi.api.auto

import kotlin.reflect.KClass

/**
 * 用于标志需要被离线的数据，不能被static修饰
 *
 * 对于`val`（或`final`）类型的变量，如果其可以在不被修改的情况下完成写入则可以支持，否则不允许为其添加该注解
 * @author EmptyDreams
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoSave(
    /** 存储key，默认为变量名 */
    val value: String = "",

    /** 数据的源类型，默认为声明的类型 */
    val from: KClass<*> = Any::class,

    /** 目的独写类型，默认为被注解的变量的类型 */
    val to: KClass<*> = Any::class
)