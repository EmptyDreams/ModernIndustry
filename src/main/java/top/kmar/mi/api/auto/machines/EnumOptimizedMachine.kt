package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import java.lang.reflect.Field

/**
 * 带存储优化的枚举类的读写器
 * @author EmptyDreams
 */
class EnumOptimizedMachine : IAutoFieldRW {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val source = annotation.source(field).java
        return field.type == source && annotation.local(field) == source &&
                Enum::class.java.isAssignableFrom(source)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val value = field[obj] as Enum<*>? ?: return RWResult.skipNull()
        writer.writeVarInt(value.ordinal)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val index = reader.readVarInt()
        field[obj] = (annotation.source(field).java.getDeclaredField("values")[null] as Array<*>)[index]
        return RWResult.success()
    }

}