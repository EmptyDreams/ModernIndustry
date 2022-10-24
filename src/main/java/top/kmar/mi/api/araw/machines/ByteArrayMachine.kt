package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByteArray
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Byte数组的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_ARRAY_TYPE)
object ByteArrayMachine : IAutoFieldRW, IAutoObjRW<ByteArray> {

    @JvmStatic fun instance() = ByteArrayMachine

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == ByteArray::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as ByteArray?) ?: return null
        return when (val local = annotation.local(field)) {
            ByteArray::class -> NBTTagByteArray(value)
            else -> throw ClassCastException("byte[]不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val array = (reader as NBTTagByteArray).byteArray
        val value = field[obj] as ByteArray?
        if (value == null || value.size < array.size) {
            if (Modifier.isFinal(field.modifiers))
                throw UnsupportedOperationException("不支持对值为空或长度不足且为final的属性进行读写")
            field.set(obj, array)
        } else {
            System.arraycopy(array, 0, value, 0, array.size)
        }
    }

    override fun match(type: KClass<*>) = type == ByteArray::class

    override fun write2Local(value: ByteArray, local: KClass<*>): NBTBase {
        if (local != ByteArray::class)
            throw ClassCastException("byte[]不能转化为${local.qualifiedName}")
        return NBTTagByteArray(value)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (ByteArray) -> Unit) {
        val array = (reader as NBTTagByteArray).byteArray
        receiver(array)
    }
}