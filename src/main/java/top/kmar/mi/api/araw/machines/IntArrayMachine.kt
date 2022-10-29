package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagByteArray
import net.minecraft.nbt.NBTTagIntArray
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * 整型数组的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_ARRAY_TYPE)
object IntArrayMachine : IAutoFieldRW, IAutoObjRW<IntArray> {

    @JvmStatic fun instance() = IntArrayMachine

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == IntArray::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as IntArray?) ?: return NBTTagByte(0)
        return write2Local(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        if (reader is NBTTagByte) field[obj] = null
        else {
            val array = readHelper(reader, annotation.local(field))
            if (Modifier.isFinal(field.modifiers)) {
                val value = field[obj] as IntArray?
                if (value == null || value.size < array.size)
                    throw UnsupportedOperationException("不支持对值为空或长度过小且为final的属性进行读写")
                System.arraycopy(array, 0, value, 0, array.size)
            } else field[obj] = array
        }
    }

    override fun match(type: KClass<*>) = type == IntArray::class

    override fun write2Local(value: IntArray, local: KClass<*>): NBTBase {
        return when (local) {
            IntArray::class -> NBTTagIntArray(value)
            ByteArray::class -> NBTTagByteArray(ByteArray(value.size) { value[it].toByte() })
            else -> throw ClassCastException("int[]不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (IntArray) -> Unit) {
        receiver(readHelper(reader, local))
    }

    private fun readHelper(reader: NBTBase, local: KClass<*>): IntArray {
        return when (local) {
            IntArray::class -> (reader as NBTTagIntArray).intArray
            ByteArray::class -> {
                val array = (reader as NBTTagByteArray).byteArray
                return IntArray(array.size) { array[it].toInt() }
            }
            else -> throw ClassCastException("${local.qualifiedName}不能转化为int[]")
        }
    }

}