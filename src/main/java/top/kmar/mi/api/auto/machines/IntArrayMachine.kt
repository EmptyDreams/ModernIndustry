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
 * 整型数组的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_ARRAY_TYPE)
class IntArrayMachine : IAutoFieldRW, IAutoObjRW<IntArray> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        return annotation.source(field) == IntArray::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val value = (field[obj] as IntArray?) ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val length = reader.readVarInt()
        var value = field[obj] as IntArray?
        if (value == null || value.size < length) {
            if (Modifier.isFinal(field.modifiers)) return RWResult.failedFinal()
            value = IntArray(length)
            field.set(obj, value)
        }
        return readHelper(reader, annotation.local(field), length, value)
    }

    override fun match(type: KClass<*>) = type == IntArray::class

    override fun write2Local(writer: IDataWriter, value: IntArray, local: KClass<*>): RWResult {
        when (local) {
            IntArray::class -> {
                writer.writeVarInt(value.size)
                for (it in value) writer.writeInt(it)
            }
            ByteArray::class -> {
                writer.writeVarInt(value.size)
                for (it in value) writer.writeByte(it.toByte())
            }
            ShortArray::class -> {
                writer.writeVarInt(value.size)
                for (it in value) writer.writeShort(it.toShort())
            }
            else -> return RWResult.failed("int[]不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (IntArray) -> Unit): RWResult {
        val length = reader.readVarInt()
        val result = IntArray(length)
        val check = readHelper(reader, local, length, result)
        if (check.isFailed()) return check
        receiver(result)
        return RWResult.success()
    }

    private fun readHelper(reader: IDataReader, local: KClass<*>, length: Int, result: IntArray): RWResult {
        when (local) {
            IntArray::class -> for (i in 0 until length) result[i] = reader.readInt()
            ShortArray::class -> for (i in 0 until length) result[i] = reader.readShort().toInt()
            ByteArray::class -> for (i in 0 until  length) result[i] = reader.readByte().toInt()
            else -> return RWResult.failed("${local.qualifiedName}不能转化为int[]")
        }
        return RWResult.success()
    }

}