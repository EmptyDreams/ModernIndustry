package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import top.kmar.mi.api.araw.AutoDataRW
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Map的读写器
 * @author EmptyDreams
 */
@Suppress("UNCHECKED_CAST")
@AutoRWType(AutoTypeRegister.GENERAL_TYPE shr 1)
object MapMachine : IAutoFieldRW, IAutoObjRW<Map<*, *>> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return Map::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field)
        val value = field[obj] as Map<Any?, Any?>? ?: return NBTTagByte(0)
        return write2Local(value, local)
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field).java
        val value = (field[obj] ?: local.newInstance()) as MutableMap<Any?, Any?>
        readHelper(reader, value)
    }

    override fun match(type: KClass<*>) = Map::class.java.isAssignableFrom(type.java)

    override fun write2Local(value: Map<*, *>, local: KClass<*>): NBTBase {
        if (!Map::class.java.isAssignableFrom(local.java))
            throw ClassCastException("Map<K, V>不能转化为${local.qualifiedName}")
        if (value.isEmpty()) return NBTTagByte(0)
        val result = NBTTagCompound()
        var index = 0
        value.forEach { (key, value) ->
            val tag = NBTTagCompound()
            if (key != null) {
                tag.setString("kn", key.javaClass.name)
                val data = AutoDataRW.write2Local(key, key::class)
                if (data != null) tag.setTag("k", data)
            }
            if (value != null) {
                tag.setString("vn", value.javaClass.name)
                val data = AutoDataRW.write2Local(value, value::class)
                if (data != null) tag.setTag("v", data)
            }
            result.setTag(index.toString(), tag)
            ++index
        }
        return result
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Map<*, *>) -> Unit) {
        val value = local.java.newInstance() as MutableMap<Any?, Any?>
        readHelper(reader, value)
        receiver(value)
    }

    private fun readHelper(reader: NBTBase, value: MutableMap<Any?, Any?>) {
        fun read(nbt: NBTTagCompound, key: String): Any? {
            val name = key + 'n'
            return if (nbt.hasKey(name)) {
                val clazz = Class.forName(nbt.getString(name)).kotlin
                var data: Any? = null
                if (nbt.hasKey(key)) AutoDataRW.read2Obj<Any>(nbt.getTag(key), clazz) { data = it }
                data
            } else null
        }

        if (reader is NBTTagByte) return
        val list = reader as NBTTagCompound
        list.keySet.forEach { key ->
            val tag = reader.getCompoundTag(key)
            val left = read(tag, "k")
            val right = read(tag, "v")
            value[left] = right
        }
    }

}