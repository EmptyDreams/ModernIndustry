package top.kmar.mi.api.auto.interfaces

/**
 * 表明读写操作的结果
 * @author EmptyDreams
 */
class RWResult private constructor(
    private val state: Result,
    val message: String = "",
    val exception: Throwable? = null
) {

    companion object {

        private val SUCCESS = RWResult(Result.SUCCESSFUL)
        private val FAILED_FINAL = failed("该数据被final修饰，但该数据类型只能在非final的值上进行读写")
        private val FAILED_STATIC = failed("该数据被static修饰，自动读写不支持对static属性进行读写")
        private val FAILED_NO_ANNOTATION = failed("该属性没有被`@AutoSave`注释")
        private val FAILED_UNSUPPORT = failed("未找到支持该属性的读写器")
        private val SKIP_NULL = skip("该属性为null")
        private val SKIP_NO_BASE = skip("该属性不为基本类型")

        @JvmStatic fun success() = SUCCESS

        @JvmStatic fun failed(message: String) = RWResult(Result.FAILED, message)

        @JvmStatic fun skip(message: String) = RWResult(Result.SKIP, message)

        /** 因为数据类型为`val`(`final`)导致的读写失败 */
        @JvmStatic fun failedFinal() = FAILED_FINAL

        /** 因为数据类型为`static`导致的读写失败 */
        @JvmStatic fun failedStatic() = FAILED_STATIC

        /** 因为属性没有被[AutoSave]注释导致的读写失败 */
        @JvmStatic fun failedNoAnnotation() = FAILED_NO_ANNOTATION

        /** 因为没有支持该属性的读写器导致的读写失败 */
        @JvmStatic fun failedUnsupport() = FAILED_UNSUPPORT

        /** 因为值为`null`而跳过读写 */
        @JvmStatic fun skipNull() = SKIP_NULL

        /** 因为值不为基本类型而跳过读写 */
        @JvmStatic fun skipNoBase() = SKIP_NO_BASE

    }

    fun isSuccessful() = state == Result.SUCCESSFUL

    fun isFailed() = state == Result.FAILED

    fun isSkip() = state == Result.SKIP

    private enum class Result {
        /** 读写成功 */
        SUCCESSFUL,
        /** 读写失败 */
        FAILED,
        /** 跳过读写 */
        SKIP
    }

}