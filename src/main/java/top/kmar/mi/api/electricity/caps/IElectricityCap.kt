package top.kmar.mi.api.electricity.caps

import top.kmar.mi.api.electricity.EleEnergy
import top.kmar.mi.api.utils.expands.ceilDiv2

/**
 * 电力系统的`cap`接口
 *
 * 除导线外所有能与导线连接的方块均需包含该`cap`
 *
 * @author EmptyDreams
 */
interface IElectricityCap {

    /**
     * 从该方块取出能量
     *
     * 方法使用二分查找法查找可以输出的最大能量值，在进行二分查找前会先对最大输出进行尝试
     *
     * @param maxEnergy 需要的能量
     * @param loss 电损计算
     * @return 成功取出的能量，[EleEnergy.capacity] <= [maxEnergy]
     */
    fun extract(maxEnergy: Int, loss: (EleEnergy) -> Int): EleEnergy {
        var energy = checkEnergy(maxEnergy, loss)
        if (energy.isNotEmpty()) return energy.copy(voltage = energy.voltage)
        var left = 0
        var right = maxEnergy
        do {
            val mid = (left + right).ceilDiv2()
            energy = checkEnergy(mid, loss)
            if (energy.isEmpty) right = mid - 1
            else left = mid + 1
        } while (left <= right)
        if (left == 0) return EleEnergy.empty
        consumeEnergy(energy.capacity)
        return energy.copy(left - 1)
    }

    /** 消耗指定数额的能量，当方块内部能量不足以消耗时应当抛出异常 */
    fun consumeEnergy(energy: Int)

    /**
     * 检查指定数额的能量是否可以被取出
     * @param energy 需要取出的能量
     * @param loss 电损计算公式
     * @return 不能取出返回 [EleEnergy.empty]，否则返回实际消耗的电能
     */
    fun checkEnergy(energy: Int, loss: (EleEnergy) -> Int): EleEnergy

}