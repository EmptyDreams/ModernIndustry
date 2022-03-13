package top.kmar.mi.api.auto

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
        private val SKIP = RWResult(Result.SKIP)
        private val FAILED_FINAL = failed("该数据被final修饰，但该数据类型只能在非final的值上进行读写")

        @JvmStatic
        fun success() = SUCCESS

        @JvmStatic
        fun skip() = SKIP

        @JvmStatic
        fun failed(message: String) = RWResult(Result.FAILED, message)

        /** 因为数据类型为`val`(`final`)导致的读写失败 */
        fun failedFinal() = FAILED_FINAL

    }

    fun isSuccessful() = state == Result.SUCCESSFUL

    fun isSkip() = state == Result.SKIP

    fun isFailed() = state == Result.FAILED

    private enum class Result {
        SUCCESSFUL, FAILED, SKIP
    }

}