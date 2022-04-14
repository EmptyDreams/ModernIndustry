package top.kmar.mi.api.araw.interfaces

import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 表明读写操作的结果
 * @author EmptyDreams
 */
class RWResult private constructor(
    private val state: Result,
    val name: KClass<*>?,
    val message: String = "",
    val exception: Throwable? = null
) {

    companion object {

        private val SUCCESS = RWResult(Result.SUCCESSFUL, null)
        private val FAILED_UNSUPPORT = failed(message = "未找到支持该属性的读写器")
        private val SKIP_NULL = skip("该属性为null")

        @JvmStatic fun success() = SUCCESS

        /**
         * 构建一个读写发生错误的结果
         * @param machine 读写器对象
         * @param message 错误信息
         */
        @JvmStatic fun failed(machine: IAutoMachine? = null, message: String = "未知错误") =
            RWResult(Result.FAILED, if (machine == null) null else machine::class, message)

        /**
         * 构建一个读写发生错误的结果
         * @param name 读写器类型
         * @param message 错误信息
         */
        @JvmStatic fun failed(name: KClass<out IAutoMachine>, message: String) =
            RWResult(Result.FAILED, name, message)

        /**
         * 构建一个读写发生错误并产生异常的读写器
         * @param machine 读写器对象
         * @param message 错误信息
         * @param exception 产生的异常
         */
        @JvmStatic fun failedWithException(machine: IAutoMachine?, message: String, exception: Throwable) =
            RWResult(Result.FAILED, if (machine == null) null else machine::class, message, exception)

        /**
         * 构建一个读写发生错误并产生异常的读写器
         * @param name 读写器类型
         * @param message 错误信息
         * @param exception 产生的异常
         */
        @JvmStatic fun failedWithException(name: KClass<out IAutoMachine>,
                                message: String, exception: Throwable) =
            RWResult(Result.FAILED, name, message, exception)

        @JvmStatic fun skip(message: String) = RWResult(Result.SKIP, null, message)

        /** 因为数据类型为`val`(`final`)导致的读写失败 */
        @JvmStatic fun failedFinal(machine: IAutoMachine?) =
            failed(machine, "该数据被final修饰，但该数据类型只能在非final的值上进行读写")

        /** 因为数据类型为`val`(`final`)导致的读写失败 */
        @JvmStatic fun failedFinal(name: KClass<out IAutoMachine>) =
            failed(name, "该数据被final修饰，但该数据类型只能在非final的值上进行读写")

        /** 因为数据类型为`static`导致的读写失败 */
        @JvmStatic fun failedStatic(machine: IAutoMachine?) =
            failed(machine, "该数据被static修饰，自动读写不支持对static属性进行读写")

        /** 因为数据类型为`static`导致的读写失败 */
        @JvmStatic fun failedStatic(name: KClass<out IAutoMachine>) =
            failed(name, "该数据被static修饰，自动读写不支持对static属性进行读写")

        /** 因为没有支持该属性的读写器导致的读写失败 */
        @JvmStatic fun failedUnsupport() = FAILED_UNSUPPORT

        /** 因为值为`null`而跳过读写 */
        @JvmStatic fun skipNull() = SKIP_NULL

    }

    fun isSuccessful() = state == Result.SUCCESSFUL

    fun isFailed() = state == Result.FAILED

    fun isSkip() = state == Result.SKIP

    fun hasException() = exception != null

    fun hasName() = name != null

    fun buildString(obj: Any, field: Field): String =
        "在进行数据读写时出现了错误：" +
                "\n\t\t读写器：${name?.qualifiedName}" +
                "\n\t\t类名：${obj::class.qualifiedName}" +
                "\n\t\t属性：${field.name}" +
                "\n\t\t信息：$message" +
                "\n\t\t异常：${hasException()}"

    private enum class Result {
        /** 读写成功 */
        SUCCESSFUL,
        /** 读写失败 */
        FAILED,
        /** 跳过读写 */
        SKIP
    }

}