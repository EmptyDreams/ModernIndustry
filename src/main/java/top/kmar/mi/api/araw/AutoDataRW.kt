package top.kmar.mi.api.araw

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.apache.commons.lang3.tuple.MutablePair
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
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
    fun write2LocalAll(writer: IDataWriter, obj: Any) {
        var clazz = obj::class.java
        while (clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                val annotation = field.getAnnotation(AutoSave::class.java) ?: continue
                val operator = ByteDataOperator()
                val check = write2Local(operator, field, obj)
                writer.writeBoolean(check.isSuccessful())
                if (check.isSuccessful()) {
                    val key = annotation.value(field)
                    writer.writeString(key)
                    writer.writeData(operator)
                } else if (check.isFailed()) printErr(obj, field, check)
            }
            clazz = clazz.superclass
        }
    }

    /**
     * 将指定对象中所有需要进行存储的数据读取到对象中
     *
     * 该函数会处理输入的对象（包括其父类）中的所有需要处理的数据
     */
    fun read2ObjAll(reader: IDataReader, obj: Any) {
        var clazz = obj::class.java
        val map = Object2ObjectOpenHashMap<String, MutablePair<IDataReader?, Field?>>()
        while (clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                val annotation = field.getAnnotation(AutoSave::class.java) ?: continue
                try {
                    if (!reader.readBoolean()) continue
                    val localName = reader.readString()
                    val codeName = annotation.value(field)
                    if (map.isNotEmpty() || codeName != localName) {
                        map.computeIfAbsent(localName) { MutablePair(null, null) }.left = reader.readData()
                        map.computeIfAbsent(codeName) { MutablePair(null, null) }.right = field
                    } else read2ObjAndPrintErr(reader.readData(), field, obj)
                } catch (e: Exception) {
                    MISysInfo.err("读取存档时发生异常，异常位置：${field.name}", e)
                    break
                }
            }
            clazz = clazz.superclass
        }
        map.forEach { (_, pair) -> read2ObjAndPrintErr(pair.left!!, pair.right!!, obj) }
    }

    private fun read2ObjAndPrintErr(reader: IDataReader, field: Field, obj: Any) {
        val check = read2Obj(reader, field, obj)
        if (check.isFailed()) printErr(obj, field, check)
        else if (!reader.isEnd)
            printErr(obj, field, RWResult.failed(message = "本地信息没有读取完毕就结束了处理"))
    }

    /** 写入数据到[IDataWriter] */
    fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val machine = AutoTypeRegister.match(field) ?: return RWResult.failedUnsupport()
        val check = checkField(field, machine)
        if (!check.isSuccessful()) return check
        return machine.write2Local(writer, field, obj)
    }

    /** 写入数据到[IDataWriter] */
    fun write2Local(writer: IDataWriter, data: Any, local: KClass<*> = Any::class): RWResult {
        val machine = AutoTypeRegister.match(data::class) ?: return RWResult.failedUnsupport()
        return if (local == Any::class) machine.write2Local(writer, data, data::class)
                else machine.write2Local(writer, data, local)
    }

    /** 从[IDataReader]读取数据 */
    fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val machine = AutoTypeRegister.match(field) ?: return RWResult.failedUnsupport()
        val check = checkField(field, machine)
        if (!check.isSuccessful()) return check
        return machine.read2Obj(reader, field, obj)
    }

    /** 从[IDataReader]读取数据 */
    fun <T> read2Obj(reader: IDataReader, local: KClass<*>, receiver: (T) -> Unit): RWResult {
        @Suppress("UNCHECKED_CAST")
        val machine = AutoTypeRegister.match(local) as IAutoObjRW<T>? ?: return RWResult.failedUnsupport()
        return machine.read2Obj(reader, local, receiver)
    }

    private fun checkField(field: Field, machine: IAutoFieldRW): RWResult {
        val mode = field.modifiers
        if (Modifier.isStatic(mode)) return RWResult.failedStatic(machine)
        if (Modifier.isFinal(mode) && !machine.allowFinal()) return RWResult.failedFinal(machine)
        if (!Modifier.isPublic(mode)) field.isAccessible = true
        return RWResult.success()
    }

    private fun printErr(obj: Any, field: Field, result: RWResult) {
        if (result.hasException()) MISysInfo.err(result.buildString(obj, field), result.exception)
        else MISysInfo.err(result.buildString(obj, field))
    }

}