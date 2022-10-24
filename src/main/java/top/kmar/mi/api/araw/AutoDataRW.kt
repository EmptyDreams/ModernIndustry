package top.kmar.mi.api.araw

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.utils.MISysInfo
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * 自动独写数据
 * @author EmptyDreams
 */
@Suppress("DuplicatedCode")
object AutoDataRW {

    /**
     * 将指定对象中所有需要进行存储的数据按顺序写入到`writer`中
     *
     * 该函数会处理输入的对象（包括其父类）中的所有需要处理的数据
     */
    fun writeAll(obj: Any): NBTTagCompound {
        var clazz = obj::class.java
        val result = NBTTagCompound()
        while (clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                try {
                    val annotation = field.getAnnotation(AutoSave::class.java) ?: continue
                    val `data` = write2Local(field, obj) ?: continue
                    val key = annotation.value(field)
                    result.setTag(key, `data`)
                } catch (e: Exception) {
                    MISysInfo.err("在写入信息时发生异常\n\t异常对象：$obj" +
                            "\n\t异常位置：${clazz.name}, ${field.name}", e)
                }
            }
            clazz = clazz.superclass
        }
        return result
    }

    /**
     * 将指定对象中所有需要进行存储的数据读取到对象中
     *
     * 该函数会处理输入的对象（包括其父类）中的所有需要处理的数据
     */
    fun read2ObjAll(reader: NBTTagCompound, obj: Any) {
        var clazz = obj::class.java
        while (clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                val annotation = field.getAnnotation(AutoSave::class.java) ?: continue
                try {
                    val localName = annotation.value(field)
                    val value = reader.getTag(localName) ?: continue
                    read2Obj(value, field, obj)
                } catch (e: Exception) {
                    MISysInfo.err("在读取信息时发生异常\n\t异常对象：$obj" +
                            "\n\t异常位置：${clazz.name}#${field.name}", e)
                }
            }
            clazz = clazz.superclass
        }
    }

    /** 写入数据到[NBTTagCompound] */
    fun write2Local(field: Field, obj: Any): NBTBase? {
        val machine = AutoTypeRegister.match(field)
        checkField(field, machine)
        return machine.write2Local(field, obj)
    }

    /** 写入数据到[NBTTagCompound] */
    fun write2Local(data: Any, local: KClass<*> = Any::class): NBTBase? {
        val machine = AutoTypeRegister.match(data::class)
        return if (local == Any::class) machine.write2Local(data, data::class)
            else machine.write2Local(data, local)
    }

    /** 从[NBTTagCompound]读取数据 */
    fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val machine = AutoTypeRegister.match(field)
        checkField(field, machine)
        machine.read2Obj(reader, field, obj)
    }

    /** 从[NBTTagCompound]读取数据 */
    fun <T> read2Obj(reader: NBTBase, local: KClass<*>, receiver: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val machine = AutoTypeRegister.match(local) as IAutoObjRW<T>
        machine.read2Obj(reader, local, receiver)
    }

    private fun checkField(field: Field, machine: IAutoFieldRW) {
        val mode = field.modifiers
        if (Modifier.isStatic(mode)) throw UnsupportedOperationException("不支持静态类型的读写")
        if (Modifier.isFinal(mode) && !machine.allowFinal())
            throw UnsupportedOperationException("指定类型[${field.type}]不支持对final对象进行读写")
        if (!Modifier.isPublic(mode)) field.isAccessible = true
    }

}