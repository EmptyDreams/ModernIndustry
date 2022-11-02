package top.kmar.mi.api.electricity.caps

import top.kmar.mi.api.electricity.info.EleEnergy

/**
 * 不输出能量的`cap`
 * @author EmptyDreams
 */
object EmptyElectricityCap : IElectricityCap {

    override fun extract(maxEnergy: Int): EleEnergy {
        return EleEnergy.empty
    }

}