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
 * [IDorSerialize]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.GENERAL_TYPE)
object DorSerializeMachine : IAutoFieldRW, IAutoObjRW<IDorSerialize> {

    @JvmStatic fun instance() = DorSerializeMachine

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return IDorSerialize::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as IDorSerialize? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as IDorSerialize?
        return if (value == null) {
            if (Modifier.isFinal(field.modifiers)) return RWResult.failedFinal(this)
            read2Obj(reader, annotation.local(field)) { field[obj] = it }
        } else {
            value.deserializedDor(reader.readData())
            RWResult.success()
        }
    }

    override fun match(type: KClass<*>) = IDorSerialize::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: IDorSerialize, local: KClass<*>): RWResult {
        if (!local.java.isAssignableFrom(value::class.java))
            return RWResult.failed(this, "IDorSerialize不能转化为${local.qualifiedName}")
        value.serializeDor(writer)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (IDorSerialize) -> Unit): RWResult {
        val value = try {
            local.java.newInstance() as IDorSerialize
        } catch (e: Throwable) {
            return RWResult.failedWithException(this, "DorSerializeMachine构建对象过程中发生了异常", e)
        }
        value.deserializedDor(reader.readData())
        receiver(value)
        return RWResult.success()
    }

}