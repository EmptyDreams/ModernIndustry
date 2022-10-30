package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraftforge.common.util.INBTSerializable
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * [INBTSerializable]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.GENERAL_TYPE)
object SerializableMachine : IAutoFieldRW, IAutoObjRW<INBTSerializable<*>> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return INBTSerializable::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as INBTSerializable<*>? ?: return NBTTagByte(0)
        return write2Local(value, annotation.local(field))
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        var value = field[obj] as INBTSerializable<NBTBase>?
        if (value == null) {
            if (Modifier.isFinal(field.modifiers))
                throw UnsupportedOperationException("不支持对默认值为null且为final的属性进行读写")
            value = annotation.local(field).java.newInstance() as INBTSerializable<NBTBase>
            field[obj] = value
        }
        value.deserializeNBT(reader)
    }

    override fun match(type: KClass<*>) = INBTSerializable::class.java.isAssignableFrom(type.java)

    override fun write2Local(value: INBTSerializable<*>, local: KClass<*>): NBTBase {
        if (!local.java.isAssignableFrom(value::class.java))
            throw ClassCastException("INBTSerializable<*>不能转化为${local.qualifiedName}")
        return value.serializeNBT()
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (INBTSerializable<*>) -> Unit) {
        val value = local.java.newInstance() as INBTSerializable<NBTBase>
        value.deserializeNBT(reader)
        receiver(value)
    }

}