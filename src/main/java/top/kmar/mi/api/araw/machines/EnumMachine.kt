package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 通用枚举类读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE shl 1)
object EnumMachine : IAutoFieldRW, IAutoObjRW<Enum<*>> {

    @JvmStatic fun instance() = EnumMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return Enum::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field)
        val value = field[obj] as Enum<*>? ?: return null
        return write2Local(value, local)
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field)
        read2Obj(reader, local) { field[obj] = it }
    }

    override fun match(type: KClass<*>) = Enum::class.java.isAssignableFrom(type.java)

    override fun write2Local(value: Enum<*>, local: KClass<*>): NBTBase {
        if (local != value::class) throw ClassCastException("Enum不能转化为${local.qualifiedName}")
        return NBTTagCompound().apply {
            setString("class", value::class.qualifiedName!!)
            setString("name", value.name)
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Enum<*>) -> Unit) {
        val `data` = reader as NBTTagCompound
        val className = `data`.getString("class")
        val valueName = `data`.getString("name")
        val enumClass = Class.forName(className)
        val method = Enum::class.java.getMethod("valueOf", Class::class.java, String::class.java)
        receiver(method(null, enumClass, valueName) as Enum<*>)
    }

}