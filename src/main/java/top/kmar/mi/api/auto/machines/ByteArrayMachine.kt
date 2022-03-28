package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Byte数组的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_ARRAY_TYPE)
class ByteArrayMachine : IAutoFieldRW, IAutoObjRW<ByteArray> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == ByteArray::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as ByteArray?) ?: return RWResult.skipNull()
        when (val local = annotation.local(field)) {
            ByteArray::class -> {
                writer.writeVarInt(value.size)
                for (it in value) writer.writeByte(it)
            }
            else -> return RWResult.failed("byte[]不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val length = reader.readVarInt()
        var value = field[obj] as ByteArray?
        if (value == null || value.size < length) {
            if (Modifier.isFinal(field.modifiers)) return RWResult.failedFinal()
            value = ByteArray(length)
            field.set(obj, value)
        }
        for (i in 0 until length) value[i] = reader.readByte()
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == ByteArray::class

    override fun write2Local(writer: IDataWriter, value: ByteArray, local: KClass<*>): RWResult {
        if (local != ByteArray::class) return RWResult.failed("byte[]不能转化为${local.qualifiedName}")
        writer.writeVarInt(value.size)
        for (it in value) writer.writeByte(it)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (ByteArray) -> Unit): RWResult {
        val length = reader.readVarInt()
        val result = ByteArray(length)
        for (i in 0 until length) result[i] = reader.readByte()
        receiver(result)
        return RWResult.success()
    }
}