package top.kmar.mi.api.utils.interfaces

/**
 * 支持 `break` 的遍历接口
 *
 * 调用传入的第二个函数即可终止遍历
 */
typealias BreakConsumer<T> = (T, () -> Unit) -> Unit