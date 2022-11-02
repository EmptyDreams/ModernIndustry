package top.kmar.mi.api.electricity.caps

import top.kmar.mi.api.electricity.info.EleEnergy

/**
 * 电力系统的`cap`接口
 *
 * 除导线外所有能与导线连接的方块均需包含该`cap`
 *
 * @author EmptyDreams
 */
fun interface IElectricityCap {

    /**
     * 从该方块取出能量
     * @param maxEnergy 需要的能量
     * @param loss 电损计算
     * @return 成功取出的能量，[EleEnergy.capacity] <= [maxEnergy]
     */
    fun extract(maxEnergy: Int, loss: (EleEnergy) -> Int): EleEnergy

}