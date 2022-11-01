package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagShort
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.araw.interfaces.IAutoFieldRW
import top.kmar.mi.api.araw.interfaces.IAutoObjRW
import top.kmar.mi.api.araw.interfaces.local
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import top.kmar.mi.api.utils.container.IntBitSet
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * [IntBitSet]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object IntBitSetMachine : IAutoFieldRW, IAutoObjRW<IntBitSet> {

    override fun allowFinal() = true

    override fun match(field: Field) = field.type == IntBitSet::class.java

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as IntBitSet? ?: return null
        return write2Local(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val value = when (reader) {
            is NBTTagByte -> reader.int
            is NBTTagShort -> reader.int
            is NBTTagInt -> reader.int
            else -> throw AssertionError()
        }
        if (Modifier.isFinal(field.modifiers)) {
            val bitset = field[obj] as IntBitSet?
                ?: throw UnsupportedOperationException("不支持对值为 null 且为 final 的属性进行读写")
            bitset.value = value
        } else {
            field[obj] = IntBitSet(value)
        }
    }

    override fun match(type: KClass<*>) = type == IntBitSet::class

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (IntBitSet) -> Unit) {
        val value = when (reader) {
            is NBTTagByte -> reader.int
            is NBTTagShort -> reader.int
            is NBTTagInt -> reader.int
            else -> throw AssertionError()
        }
        receiver(IntBitSet(value))
    }

    override fun write2Local(value: IntBitSet, local: KClass<*>): NBTBase {
        if (local != IntBitSet::class)
            throw ClassCastException("IntBitSet不能转换为 ${local.qualifiedName}")
        val num = value.value
        return when {
            num ushr 8 == 0 -> NBTTagByte(num.toByte())
            num ushr 16 == 0 -> NBTTagShort(num.toShort())
            else -> NBTTagInt(num)
        }
    }
}