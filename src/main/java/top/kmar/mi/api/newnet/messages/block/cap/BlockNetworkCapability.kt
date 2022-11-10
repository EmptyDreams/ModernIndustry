package top.kmar.mi.api.newnet.messages.block.cap

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import top.kmar.mi.api.regedits.others.AutoLoader

/**
 * 自动化网络通信的 cap
 * @author EmptyDreams
 */
@AutoLoader
object BlockNetworkCapability {

    @CapabilityInject(IAutoNetwork::class)
    @JvmStatic
    lateinit var capObj: Capability<IAutoNetwork>

    @JvmStatic
    private val srcObj = IAutoNetwork { _, _ -> throw AssertionError() }

    @JvmStatic
    private val srcStore = object : Capability.IStorage<IAutoNetwork> {

        override fun writeNBT(
            capability: Capability<IAutoNetwork>?,
            instance: IAutoNetwork?,
            side: EnumFacing?
        ): NBTBase? = null

        override fun readNBT(
            capability: Capability<IAutoNetwork>?,
            instance: IAutoNetwork?,
            side: EnumFacing?,
            nbt: NBTBase?
        ) { }
    }

    init {
        CapabilityManager.INSTANCE.register(
            IAutoNetwork::class.java, srcStore
        ) { srcObj }
    }

}