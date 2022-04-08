package top.kmar.mi.api.auto.machines

import net.minecraft.nbt.NBTBase
import net.minecraftforge.common.util.INBTSerializable
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * [INBTSerializable]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.GENERAL_TYPE shr 1)
object SerializableMachine : IAutoFieldRW, IAutoObjRW<INBTSerializable<*>> {

    @JvmStatic fun instance() = SerializableMachine

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return INBTSerializable::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as INBTSerializable<*>? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        var value = field[obj] as INBTSerializable<NBTBase>?
        if (value == null) {
            if (Modifier.isFinal(field.modifiers)) return RWResult.failedFinal()
            try {
                value = annotation.local(field).java.newInstance() as INBTSerializable<NBTBase>
                field[obj] = value
            } catch (e: Throwable) {
                return RWResult.failedWithException("构建INBTSerializable<*>对象的过程中发生异常", e)
            }
        }
        value.deserializeNBT(reader.readTag())
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = INBTSerializable::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: INBTSerializable<*>, local: KClass<*>): RWResult {
        if (!local.java.isAssignableFrom(value::class.java))
            return RWResult.failed("INBTSerializable<*>不能转化为${local.qualifiedName}")
        val data = value.serializeNBT()
        writer.writeTag(data)
        return RWResult.success()
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(
        reader: IDataReader,
        local: KClass<*>,
        receiver: (INBTSerializable<*>) -> Unit
    ): RWResult {
        val value: INBTSerializable<NBTBase>?
        try {
            value = local.java.newInstance() as INBTSerializable<NBTBase>
        } catch (e: Throwable) {
            return RWResult.failedWithException("构建INBTSerializable<*>对象的过程中发生异常", e)
        }
        value.deserializeNBT(reader.readTag())
        receiver(value)
        return RWResult.success()
    }

}