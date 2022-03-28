package top.kmar.mi.api.auto.machines

import net.minecraft.util.math.BlockPos
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [BlockPos]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
class BlockPosMachine : IAutoFieldRW, IAutoObjRW<BlockPos> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        return annotation.source(field) == BlockPos::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val value = (field[obj] as BlockPos?) ?: return RWResult.skipNull()
        when (val local = annotation.local(field)) {
            BlockPos::class -> writer.writeBlockPos(value)
            else -> return RWResult.failed("BlockPos不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field[obj] = reader.readBlockPos()
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == BlockPos::class

    override fun write2Local(writer: IDataWriter, value: BlockPos, local: KClass<*>): RWResult {
        if (local != BlockPos::class) return RWResult.failed("BlockPos不能转化为${local.qualifiedName}")
        writer.writeBlockPos(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (BlockPos) -> Unit): RWResult {
        receiver(reader.readBlockPos())
        return RWResult.success()
    }
}