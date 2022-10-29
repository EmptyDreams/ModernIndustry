package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import top.kmar.mi.api.araw.AutoDataRW
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * [Collection]的读写器
 *
 * 如果不存在缺省值，则会创建[ArrayList<T>]作为缺省值
 *
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.GENERAL_TYPE shr 1)
object CollectionMachine : IAutoFieldRW, IAutoObjRW<Collection<*>> {

    @JvmStatic fun instance() = CollectionMachine

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return Collection::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field).java
        if (!Collection::class.java.isAssignableFrom(local))
            throw ClassCastException("Collection<?>不能转化为${local.name}")
        val value = (field[obj] as Collection<*>?) ?: return null
        if (value.isEmpty()) return null
        return writeHelper(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field).java
        var value = field[obj] as MutableCollection<Any>?
        if (value == null) {
            if (Modifier.isFinal(field.modifiers))
                throw UnsupportedOperationException("不支持对默认值为null且为final的属性进行读写")
            value = local.newInstance() as MutableCollection<Any>
            field[obj] = value
        }
        readHelper(reader as NBTTagCompound, value)
    }

    override fun match(type: KClass<*>) = Collection::class.java.isAssignableFrom(type.java)

    override fun write2Local(value: Collection<*>, local: KClass<*>): NBTBase {
        if (!Collection::class.java.isAssignableFrom(local.java))
            throw ClassCastException("${local.qualifiedName}不能转化为Collection<?>")
        return writeHelper(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Collection<*>) -> Unit) {
        val value = local.java.newInstance() as MutableCollection<Any>
        return readHelper(reader as NBTTagCompound, value)
    }

    private fun readHelper(reader: NBTTagCompound, value: MutableCollection<in Any>) {
        reader.keySet.forEach { key ->
            if ("size" == key) return@forEach
            val tag = reader.getCompoundTag(key)
            val name = tag.getString("name")
            val `data` = tag.getTag("value")
            AutoDataRW.read2Obj<Any>(`data`, Class.forName(name).kotlin) { value.add(it) }
        }
    }

    private fun writeHelper(value: Collection<*>): NBTTagCompound {
        val result = NBTTagCompound()
        for ((index, it) in value.withIndex()) {
            if (it == null) continue
            val name = it::class.qualifiedName ?:
                throw UnsupportedOperationException("CollectionMachine读写器不支持对匿名类进行读写")
            val `data` = AutoDataRW.write2Local(it) ?: continue
            val tag = NBTTagCompound()
            tag.setTag("name", NBTTagString(name))
            tag.setTag("value", `data`)
            result.setTag(index.toString(), tag)
        }
        return result
    }

}