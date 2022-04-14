package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * NBT的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object NbtMachine : IAutoFieldRW, IAutoObjRW<NBTBase> {

    @JvmStatic fun instance() = NbtMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return NBTBase::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as NBTBase? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return read2Obj(reader, annotation.local(field)) { field[obj] = it }
    }

    override fun match(type: KClass<*>) = NBTBase::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: NBTBase, local: KClass<*>): RWResult {
        if (local != value::class)
            return RWResult.failed(this,
                "${value::class.qualifiedName}不能转化为${local.qualifiedName}")
        writer.writeTag(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (NBTBase) -> Unit): RWResult {
        receiver(reader.readTag())
        return RWResult.success()
    }

}