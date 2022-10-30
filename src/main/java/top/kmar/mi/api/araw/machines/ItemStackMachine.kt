package top.kmar.mi.api.araw.machines

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import top.kmar.mi.api.araw.interfaces.IAutoFieldRW
import top.kmar.mi.api.araw.interfaces.IAutoObjRW
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [ItemStack]类型的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object ItemStackMachine : IAutoFieldRW, IAutoObjRW<ItemStack> {

    @JvmStatic fun instance() = this

    override fun allowFinal() = false

    override fun match(field: Field): Boolean =
        field.type == ItemStack::class.java

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val stack = field[obj] as ItemStack? ?: return NBTTagByte(1)
        return write2Local(stack, ItemStack::class)
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        if (reader is NBTTagByte) {
            field[obj] = when (reader.int) {
                0 -> ItemStack.EMPTY
                1 -> null
                else -> throw AssertionError("未知参数：${reader.int}")
            }
        } else {
            reader as NBTTagCompound
            field[obj] = ItemStack(reader)
        }
    }

    override fun match(type: KClass<*>): Boolean = type == ItemStack::class

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (ItemStack) -> Unit) {
        if (reader is NBTTagCompound) receiver(ItemStack(reader))
        else receiver(ItemStack.EMPTY)
    }

    override fun write2Local(value: ItemStack, local: KClass<*>): NBTBase {
        if (local != ItemStack::class)
            throw UnsupportedOperationException("ItemStack不支持转换为[${local.qualifiedName}]")
        if (value.isEmpty) return NBTTagByte(0)
        return value.serializeNBT()
    }

}