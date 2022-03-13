package top.kmar.mi.api.auto

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

/**
 * 自动独写数据
 * @author EmptyDreams
 */
@Suppress("DuplicatedCode")
object AutoDataRW {

    /** 写入数据到[IDataWriter] */
    fun write2Local(writer: IDataWriter, field: KProperty1<Any, *>, obj: Any): RWResult {
        val annotation = field.findAnnotation<AutoSave>() ?: return RWResult.skip()
        val fromClass = if (annotation.from == Any::class) field.javaField!!.type.kotlin else annotation.from
        if (field.isFinal && !AutoType.allowFinal(fromClass)) return RWResult.failedFinal()
        return if (annotation.to == Any::class) {
            val value = field.get(obj) ?: return RWResult.skip()
            AutoType.write(writer, value, field)
        } else {
            val value = AutoType.castTo(field.get(obj), annotation.to) ?: RWResult.skip()
            AutoType.write(writer, value, field)
        }
    }

    /** 从[IDataReader]读取数据 */
    fun readFromLocal(reader: IDataReader, field: KProperty1<Any, *>, obj: Any): RWResult {
        val annotation = field.findAnnotation<AutoSave>() ?: return RWResult.skip()
        val fromClass = if (annotation.from == Any::class) field.javaField!!.type.kotlin else annotation.from
        if (field.isFinal && !AutoType.allowFinal(fromClass)) return RWResult.failedFinal()
        return if (annotation.to == Any::class) {
            AutoType.read(reader, field, obj, fromClass)
        } else {
            AutoType.read(reader, field, obj, annotation.to)
        }
    }

}