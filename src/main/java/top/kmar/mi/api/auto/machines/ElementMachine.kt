package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.craftguide.ItemElement
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [ItemElement]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
class ElementMachine : IAutoFieldRW, IAutoObjRW<ItemElement> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == ItemElement::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as ItemElement? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field[obj] = ItemElement.instance(reader)
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == ItemElement::class

    override fun write2Local(writer: IDataWriter, value: ItemElement, local: KClass<*>): RWResult {
        if (local != ItemElement::class)
            return RWResult.failed("ItemElement不能转化为${local.qualifiedName}")
        value.writeToData(writer)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (ItemElement) -> Unit): RWResult {
        receiver(ItemElement.instance(reader))
        return RWResult.success()
    }

}