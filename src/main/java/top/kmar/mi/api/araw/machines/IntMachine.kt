package top.kmar.mi.api.araw.machines

import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 整型的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object IntMachine : IAutoFieldRW, IAutoObjRW<Int> {

    @JvmStatic fun instance() = IntMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Int::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getInt(obj)
        return writeHelper(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        when (val local = annotation.local(field)) {
            Int::class -> field.setInt(obj, reader.readInt())
            Byte::class -> field.setInt(obj, reader.readByte().toInt())
            Boolean::class -> field.setInt(obj, if (reader.readBoolean()) 1 else 0)
            else -> return RWResult.failed(this, "${local.qualifiedName}不能转化为int")
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Int::class

    override fun write2Local(writer: IDataWriter, value: Int, local: KClass<*>): RWResult {
        return writeHelper(writer, value, local)
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Int) -> Unit): RWResult {
        when (local) {
            Int::class -> receiver(reader.readInt())
            Byte::class -> receiver(reader.readByte().toInt())
            Boolean::class -> receiver(if (reader.readBoolean()) 1 else 0)
            else -> return RWResult.failed(this, "${local.qualifiedName}不能转化为int")
        }
        return RWResult.success()
    }

    private fun writeHelper(writer: IDataWriter, value: Int, local: KClass<*>): RWResult {
        when (local) {
            Int::class -> writer.writeInt(value)
            Byte::class -> writer.writeByte(value.toByte())
            Short::class -> writer.writeShort(value.toShort())
            Boolean::class -> writer.writeBoolean(value != 0)
            else -> return RWResult.failed(this, "int不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

}