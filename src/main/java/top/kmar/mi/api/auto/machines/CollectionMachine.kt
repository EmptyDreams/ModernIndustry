package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.AutoDataRW
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
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
class CollectionMachine : IAutoFieldRW, IAutoObjRW<Collection<*>> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        return Collection::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val local = annotation.local(field).java
        if (Collection::class.java.isAssignableFrom(local))
            return RWResult.failed("Collection<?>不能转化为${local.name}")
        val value = (field[obj] as Collection<*>?) ?: return RWResult.skipNull()
        if (value.isEmpty()) return RWResult.skipNull()
        return writeHelper(writer, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val local = annotation.local(field).java
        var value = field[obj] as MutableCollection<Any>?
        if (value == null) {
            if (Modifier.isFinal(field.modifiers)) return RWResult.failedFinal()
            try {
                value = local.newInstance() as MutableCollection<Any>
                field[obj] = value
            } catch (e: Throwable) {
                return RWResult.failedWithException("构建容器（Collection）时出现了异常", e)
            }
        }
        return readHelper(reader, value)
    }

    override fun match(type: KClass<*>) = Collection::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: Collection<*>, local: KClass<*>): RWResult {
        if (Collection::class.java.isAssignableFrom(local.java))
            return RWResult.failed("${local.qualifiedName}不能转化为Collection<?>")
        return writeHelper(writer, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Collection<*>) -> Unit): RWResult {
        val value: MutableCollection<Any>
        try {
            value = local.java.newInstance() as MutableCollection<Any>
        } catch (e: Throwable) {
            return RWResult.failedWithException("构建容器过程中出现了异常", e)
        }
        return readHelper(reader, value)
    }

    private fun readHelper(reader: IDataReader, value: MutableCollection<in Any>): RWResult {
        val size = reader.readVarInt()
        for (i in 0 until size) {
            var clazz: KClass<*>? = null
            val classCheck = AutoDataRW.read2Obj<KClass<*>>(reader, KClass::class) { clazz = it }
            if (!classCheck.isSuccessful()) return classCheck
            var obj: Any? = null
            val check = AutoDataRW.read2Obj<Any>(reader, clazz!!) { obj = it }
            if (!check.isSuccessful()) return check
            value.add(obj!!)
        }
        return RWResult.success()
    }

    private fun writeHelper(writer: IDataWriter, value: Collection<*>): RWResult {
        writer.writeVarInt(value.size)
        for (it in value) {
            if (it == null) {
                writer.writeBoolean(false)
                continue
            }
            if (it::class.qualifiedName == null)
                return RWResult.failed("CollectionMachine读写器不支持对匿名类进行读写")
            writer.writeBoolean(true)
            val classCheck = AutoDataRW.write2Local(writer, it::class)
            if (!classCheck.isSuccessful()) return RWResult.failed("KClass<*>的读写器丢失")
            @Suppress("UNCHECKED_CAST")
            val machine = AutoTypeRegister.match(it::class) as IAutoObjRW<in Any>? ?:
            return RWResult.failed("没有找到与${it::class.qualifiedName}匹配的读写器")
            val check = machine.write2Local(writer, it, it::class)
            if (!check.isSuccessful()) return check
        }
        return RWResult.success()
    }

}