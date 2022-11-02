package top.kmar.mi.api.electricity.caps

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import top.kmar.mi.api.regedits.others.AutoLoader

/**
 * 电力系统的cap
 * @author EmptyDreams
 */
@AutoLoader
object ElectricityCapability {

    @JvmStatic
    @CapabilityInject(IElectricityCap::class)
    lateinit var capObj: Capability<IElectricityCap>

    init {
        CapabilityManager.INSTANCE.register(
            IElectricityCap::class.java, ElectricityCapStorage
        ) { EmptyElectricityCap }
    }

}