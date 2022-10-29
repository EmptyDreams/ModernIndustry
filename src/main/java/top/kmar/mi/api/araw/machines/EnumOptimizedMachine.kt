package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagInt
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.araw.interfaces.IAutoFieldRW
import top.kmar.mi.api.araw.interfaces.local
import top.kmar.mi.api.araw.interfaces.source
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field

/**
 * 带存储优化的枚举类的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object EnumOptimizedMachine : IAutoFieldRW {

    @JvmStatic fun instance() = EnumOptimizedMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val source = annotation.source(field).java
        return field.type == source && annotation.local(field) == source &&
                Enum::class.java.isAssignableFrom(source)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val value = field[obj] as Enum<*>? ?: return null
        return NBTTagInt(value.ordinal)
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val index = (reader as NBTTagInt).int
        field[obj] = (annotation.source(field).java.getDeclaredField("values")[null] as Array<*>)[index]
    }

}