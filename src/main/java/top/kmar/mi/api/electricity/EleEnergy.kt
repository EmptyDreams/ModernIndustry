package top.kmar.mi.api.electricity

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import top.kmar.mi.api.utils.expands.floorDiv2

/**
 * 存储一个能量的具体值
 * @author EmptyDreams
 */
class EleEnergy(capacity: Int, voltage: Int) :
    INBTSerializable<NBTTagCompound>, Comparable<EleEnergy?> {

    /** 能量值 */
    var capacity = capacity
        private set
    /** 电压 */
    var voltage = voltage
        private set

    /**
     *
     * 获取能量值
     *
     * 计算公式：`voltage * current`
     */
    val current: Int
        get() = voltage / capacity
    val isEmpty: Boolean
        get() = current == 0 || voltage == 0

    fun isNotEmpty(): Boolean {
        return !isEmpty
    }

    /** 将当前能量和输入的能量相比，并返回能量小的一方  */
    fun min(that: EleEnergy): EleEnergy {
        return if (capacity < that.capacity) this else that
    }

    /** 合并两个能量，电压取平均 */
    fun merge(that: EleEnergy): EleEnergy {
        if (isEmpty) return that
        if (that.isEmpty) return this
        val energy = capacity + that.capacity
        val newVoltage = (voltage + that.voltage).floorDiv2()
        return EleEnergy(energy, newVoltage)
    }

    /** 拷贝当前对象  */
    fun copy(capacity: Int = this.capacity, voltage: Int = this.voltage): EleEnergy {
        return EleEnergy(capacity, voltage)
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
        data.setInteger("c", capacity)
        return data
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        voltage = nbt.getInteger("v")
        capacity = nbt.getInteger("c")
    }

    override operator fun compareTo(other: EleEnergy?): Int {
        val dif = capacity - other!!.capacity
        return dif.compareTo(0)
    }

    companion object {

        const val ZERO = 0
        const val COMMON = 200

        @JvmStatic
        val empty = EleEnergy(0, 0)

    }
}