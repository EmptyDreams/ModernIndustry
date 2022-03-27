package top.kmar.mi.api.auto

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.apache.commons.lang3.tuple.MutablePair
import top.kmar.mi.api.auto.interfaces.AutoSave
import top.kmar.mi.api.auto.interfaces.IAutoRW
import top.kmar.mi.api.auto.interfaces.RWResult
import top.kmar.mi.api.auto.interfaces.value
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.utils.MISysInfo
import java.lang.reflect.Field
import java.lang.reflect.Modifier

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
        val clazz = obj::class.java
        val map = Object2ObjectOpenHashMap<String, MutablePair<IDataReader?, Field?>>()
        while (clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                val annotation = field.getAnnotation(AutoSave::class.java) ?: continue
                val localName = reader.readString()
                val codeName = annotation.value(field)
                if (map.isNotEmpty() || codeName != localName) {
                    map.computeIfAbsent(localName) { MutablePair(null, null) }.left = reader.readData()
                    map.computeIfAbsent(codeName) { MutablePair(null, null) }.right = field
                } else read2ObjAndPrintErr(reader.readData(), field, obj)
            }
        }
        map.forEach { (_, pair) -> read2ObjAndPrintErr(pair.left!!, pair.right!!, obj) }
    }

    private fun read2ObjAndPrintErr(reader: IDataReader, field: Field, obj: Any) {
        val check = read2Obj(reader, field, obj)
        if (check.isFailed()) printErr(obj, field, check)
    }

    /** 写入数据到[IDataWriter] */
    fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val machine = AutoTypeRegister.match(field) ?: return RWResult.failedUnsupport()
        val check = checkField(field, machine)
        if (!check.isSuccessful()) return check
        return machine.write2Local(writer, field, obj)
    }

    /** 从[IDataReader]读取数据 */
    fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val machine = AutoTypeRegister.match(field) ?: return RWResult.failedUnsupport()
        val check = checkField(field, machine)
        if (!check.isSuccessful()) return check
        return machine.readFromLocal(reader, field, obj)
    }

    private fun checkField(field: Field, machine: IAutoRW): RWResult {
        val mode = field.modifiers
        if (Modifier.isStatic(mode)) return RWResult.failedStatic()
        if (Modifier.isFinal(mode) && !machine.allowFinal()) return RWResult.failedFinal()
        if (!Modifier.isPublic(mode)) field.isAccessible = true
        return RWResult.success()
    }

    private fun printErr(obj: Any, field: Field, result: RWResult) {
        val text = "在进行数据读写时出现了错误：" +
                "\n\t\t类名：${obj::class.qualifiedName}" +
                "\n\t\t属性：${field.name}" +
                "\n\t\t信息：${result.message}" +
                "\n\t\t异常：${result.hasException()}"
        if (result.hasException()) MISysInfo.err(text, result.exception)
        else MISysInfo.err(text)
    }

}