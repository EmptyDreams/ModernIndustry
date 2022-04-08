package top.kmar.mi.api.electricity.info

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import top.kmar.mi.api.auto.interfaces.IDorSerialize
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

/**
 * 存储一个能量的具体值
 * @author EmptyDreams
 */
class EleEnergy(var current: Int, var voltage: Int) :
    INBTSerializable<NBTTagCompound>, IDorSerialize, Comparable<EleEnergy?> {

    /**
     *
     * 获取能量值
     *
     * 计算公式：`voltage * current`
     */
    val capacity: Int
        get() = voltage * current
    val isEmpty: Boolean
        get() = current == 0 || voltage == 0

    fun notEmpty(): Boolean {
        return !isEmpty
    }

    /** 将当前能量和输入的能量相比，并返回能量小的一方  */
    fun min(that: EleEnergy): EleEnergy {
        return if (capacity < that.capacity) this else that
    }

    /** 拷贝当前对象  */
    fun copy(): EleEnergy {
        return EleEnergy(current, voltage)
    }

    /** 拷贝当前对象并修改`current`  */
    fun copy(current: Int): EleEnergy {
        return EleEnergy(current, voltage)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val eleEnergy = other as EleEnergy
        return if (voltage != eleEnergy.voltage) false else current == eleEnergy.current
    }

    override fun hashCode(): Int {
        var result = voltage
        result = 31 * result + current
        return result
    }

    override fun toString(): String {
        return "voltage=$voltage;current=$current"
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = NBTTagCompound()
        data.setInteger("v", voltage)
        data.setInteger("c", current)
        return data
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        voltage = nbt.getInteger("v")
        current = nbt.getInteger("c")
    }

    override fun serializeDor(writer: IDataWriter) {
        writer.writeVarInt(voltage)
        writer.writeVarInt(current)
    }

    override fun deserializedDor(reader: IDataReader) {
        voltage = reader.readVarInt()
        current = reader.readVarInt()
    }

    override operator fun compareTo(other: EleEnergy?): Int {
        val dif = capacity - other!!.capacity
        return dif.compareTo(0)
    }

    companion object {

        const val ZERO = 0
        const val COMMON = 200

    }
}