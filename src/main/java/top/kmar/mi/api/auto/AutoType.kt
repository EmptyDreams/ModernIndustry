package top.kmar.mi.api.auto

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 *
 * @author EmptyDreams
 */
object AutoType {

    /** 判断指定类型在进行读写时是否允许为`val`(`final`) */
    fun allowFinal(clazz: KClass<*>): Boolean {

    }

    fun <T : Any> castTo(obj: Any?, clazz: KClass<T>): T? {

    }

    fun write(writer: IDataWriter, value: Any, field: KProperty1<*, *>): RWResult {

    }

    fun read(reader: IDataReader, field: KProperty1<*, *>, obj: Any, clazz: KClass<*>): RWResult {

    }

}