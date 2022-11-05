package top.kmar.mi.content.tileentity.maker

import net.minecraft.util.EnumFacing
import top.kmar.mi.api.electricity.EleEnergy
import top.kmar.mi.api.electricity.EleTileEntity
import top.kmar.mi.api.electricity.caps.IElectricityCap
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity

/**
 * 永恒发电机
 * @author EmptyDreams
 */
@AutoTileEntity("perpetual")
class EMPerpetual : EleTileEntity() {

    private val cap by lazy(LazyThreadSafetyMode.NONE) {
        object : IElectricityCap {
            override fun consumeEnergy(energy: Int) { }

            override fun checkEnergy(energy: Int, loss: (EleEnergy) -> Int) =
                EleEnergy(energy, EleEnergy.COMMON)
        }
    }

    override fun buildCap(facing: EnumFacing): IElectricityCap = cap

}