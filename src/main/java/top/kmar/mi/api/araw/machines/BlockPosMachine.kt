package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.util.math.BlockPos
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [BlockPos]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object BlockPosMachine : IAutoFieldRW, IAutoObjRW<BlockPos> {

    @JvmStatic fun instance() = BlockPosMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == BlockPos::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as BlockPos?) ?: return null
        return when (val local = annotation.local(field)) {
            BlockPos::class -> NBTTagIntArray(intArrayOf(value.x, value.y, value.z))
            else -> throw ClassCastException("BlockPos不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val array = (reader as NBTTagIntArray).intArray
        field[obj] = BlockPos(array[0], array[1], array[2])
    }

    override fun match(type: KClass<*>) = type == BlockPos::class

    override fun write2Local(value: BlockPos, local: KClass<*>): NBTBase {
        if (local != BlockPos::class) throw ClassCastException("BlockPos不能转化为${local.qualifiedName}")
        return NBTTagIntArray(intArrayOf(value.x, value.y, value.z))
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (BlockPos) -> Unit) {
        val array = (reader as NBTTagIntArray).intArray
        receiver(BlockPos(array[0], array[1], array[2]))
    }
}