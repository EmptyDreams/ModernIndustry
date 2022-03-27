package top.kmar.mi.api.auto

import top.kmar.mi.api.auto.interfaces.IAutoRW
import top.kmar.mi.api.auto.interfaces.RWResult
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * 自动独写数据
 * @author EmptyDreams
 */
@Suppress("DuplicatedCode")
object AutoDataRW {

    /** 写入数据到[IDataWriter] */
    fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val machine = AutoTypeRegister.match(field) ?: return RWResult.failedUnsupport()
        val check = checkField(field, machine)
        if (!check.isSuccessful()) return check
        return machine.write2Local(writer, field, obj)
    }

    /** 从[IDataReader]读取数据 */
    fun readFromLocal(reader: IDataReader, field: Field, obj: Any): RWResult {
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

}