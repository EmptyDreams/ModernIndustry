package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.AutoDataRW
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Map的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.GENERAL_TYPE shr 1)
class MapMachine : IAutoFieldRW, IAutoObjRW<Map<*, *>> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return Map::class.java.isAssignableFrom(annotation.source(field).java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field)
        val value = field[obj] as Map<Any, Any>? ?: return RWResult.skipNull()
        return write2Local(writer, value, local)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field).java
        var value = field[obj] as MutableMap<Any, Any>?
        if (value == null) {
            try {
                value = local.newInstance() as MutableMap<Any, Any>
            } catch (e: Throwable) {
                return RWResult.failedWithException("构建Map时发生了异常", e)
            }
        }
        return readHelper(reader, value)
    }

    override fun match(type: KClass<*>) = Map::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: Map<*, *>, local: KClass<*>): RWResult {
        if (!Map::class.java.isAssignableFrom(local.java))
            return RWResult.failed("Map<K, V>不能转化为${local.qualifiedName}")
        if (value.isEmpty()) return RWResult.skipNull()
        writer.writeString(value.keys::class.qualifiedName)
        writer.writeString(value.values::class.qualifiedName)
        val check = AutoDataRW.write2Local(writer, value.keys)
        if (!check.isSuccessful()) return check
        return AutoDataRW.write2Local(writer, value.values)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Map<*, *>) -> Unit): RWResult {
        val value: MutableMap<Any, Any>
        try {
            value = local.java.newInstance() as MutableMap<Any, Any>
        } catch (e: Throwable) {
            return RWResult.failedWithException("构建Map时发生了异常", e)
        }
        val check = readHelper(reader, value)
        if (check.isSuccessful()) receiver(value)
        return check
    }

    private fun readHelper(reader: IDataReader, value: MutableMap<Any, Any>): RWResult {
        val keyName = reader.readString()
        val valueName = reader.readString()
        var keys: Collection<Any>? = null
        var values: Collection<Any>? = null
        try {
            val keyCheck = AutoDataRW.read2Obj<Collection<Any>>(
                reader, Class.forName(keyName).kotlin) { keys = it }
            if (!keyCheck.isSuccessful()) return keyCheck
            val valueCheck = AutoDataRW.read2Obj<Collection<Any>>(
                reader, Class.forName(valueName).kotlin) { values = it }
            if (!valueCheck.isSuccessful()) return valueCheck
        } catch (e: ClassNotFoundException) {
            return RWResult.failedWithException("构建Map时内部Collection类缺失", e)
        }
        val keyIter = keys!!.iterator()
        val valueIter = values!!.iterator()
        while (keyIter.hasNext()) value[keyIter.next()] = valueIter.next()
        return RWResult.success()
    }

}