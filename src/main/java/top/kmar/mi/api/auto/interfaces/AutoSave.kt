package top.kmar.mi.api.auto.interfaces

import java.lang.reflect.Field
import kotlin.reflect.KClass

annotation class AutoSave(

    /** 存储唯一标识名，默认为变量名 */
    val value: String = "",

    /**
     * 存储源类型，默认为属性声明类型
     *
     * 例如：
     * ```kotlin
     *  @AutoSave var intTest = 10;
     *  @AutoSave var listTest: List<Int> = new ArrayList();
     * ```
     *
     * 上述代码中，`intTest`的`from`属性为`Int::class`，`listTest`的`from`属性为`List::class`
     */
    val source: KClass<*> = Any::class,

    /**
     * 存储时的类型，默认为属性声明类型
     * @see [source]
     */
    val local: KClass<*> = Any::class

)

fun AutoSave.value(field: Field): String = value.ifEmpty { field.name }

fun AutoSave.source(field: Field): KClass<*> =
    if (source == Any::class) field.type.kotlin else source

fun AutoSave.local(field: Field): KClass<*> =
    if (local == Any::class) source(field) else local