package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 字符串的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object StringMachine : IAutoFieldRW, IAutoObjRW<String> {

    @JvmStatic fun instance() = StringMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == String::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as String?) ?: return RWResult.skipNull()
        when (val local = annotation.local(field)) {
            String::class -> writer.writeString(value)
            else -> return RWResult.failed(this, "String不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field[obj] = reader.readString()
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == String::class

    override fun write2Local(writer: IDataWriter, value: String, local: KClass<*>): RWResult {
        if (local != String::class)
            return RWResult.failed(this, "String不能转化为${local.qualifiedName}")
        writer.writeString(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (String) -> Unit): RWResult {
        receiver(reader.readString())
        return RWResult.success()
    }
}