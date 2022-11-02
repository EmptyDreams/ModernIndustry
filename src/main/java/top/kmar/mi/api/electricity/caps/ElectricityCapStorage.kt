package top.kmar.mi.api.electricity.caps

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * 电力`cap`的存储器
 * @author EmptyDreams
 */
object ElectricityCapStorage : Capability.IStorage<IElectricityCap> {

    override fun writeNBT(
        capability: Capability<IElectricityCap>?,
        instance: IElectricityCap?,
        side: EnumFacing?
    ): NBTBase? = null

    override fun readNBT(
        capability: Capability<IElectricityCap>?,
        instance: IElectricityCap?,
        side: EnumFacing?,
        nbt: NBTBase?
    ) { }

}