package top.kmar.mi.api.utils.interfaces

fun interface Obj2IntFunction<T> {

    operator fun invoke(it: T): Int

}