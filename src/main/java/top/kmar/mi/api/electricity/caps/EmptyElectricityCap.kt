package top.kmar.mi.api.electricity.caps

import top.kmar.mi.api.electricity.EleEnergy

/**
 * 不输出能量的`cap`
 * @author EmptyDreams
 */
object EmptyElectricityCap : IElectricityCap {

    override fun extract(maxEnergy: Int, loss: (EleEnergy) -> Int) = EleEnergy.empty

    override fun consumeEnergy(energy: Int) { }

    override fun checkEnergy(energy: Int, loss: (EleEnergy) -> Int) = EleEnergy.empty

}