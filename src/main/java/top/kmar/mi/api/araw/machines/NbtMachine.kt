package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * NBT的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object NbtMachine : IAutoFieldRW, IAutoObjRW<NBTBase> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return NBTBase::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as NBTBase? ?: return null
        return write2Local(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return read2Obj(reader, annotation.local(field)) { field[obj] = it }
    }

    override fun match(type: KClass<*>) = NBTBase::class.java.isAssignableFrom(type.java)

    override fun write2Local(value: NBTBase, local: KClass<*>): NBTBase {
        if (local != value::class)
            throw ClassCastException("${value::class.qualifiedName}不能转化为${local.qualifiedName}")
        return value
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (NBTBase) -> Unit) {
        receiver(reader)
    }

}