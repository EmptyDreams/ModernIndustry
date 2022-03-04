package top.kmar.mi.content.blocks.machine.user

import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import top.kmar.mi.api.register.block.AutoBlockRegister
import top.kmar.mi.api.utils.properties.MIProperty.ALL_FACING
import top.kmar.mi.api.utils.properties.MIProperty.WORKING
import top.kmar.mi.content.blocks.CommonUtil
import top.kmar.mi.content.blocks.base.MachineBlock
import top.kmar.mi.content.tileentity.user.EUFluidPump
import kotlin.LazyThreadSafetyMode.PUBLICATION

@AutoBlockRegister(registryName = FluidPumpBlock.NAME, field = "innerInstance")
open class FluidPumpBlock : MachineBlock(Material.IRON) {

    companion object {
        const val NAME = "fluid_pump"

        @JvmStatic
        private var innerInstance: FluidPumpBlock? = null

        val INSTANCE: FluidPumpBlock
            get() = innerInstance!!
    }

    private val item: ItemBlock by lazy(PUBLICATION) { ItemBlock(this) }

    init {
        defaultState = blockState.baseState
            .withProperty(ALL_FACING, EnumFacing.NORTH)
            .withProperty(WORKING, false)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return CommonUtil.getStateFromMeta(this, meta)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return CommonUtil.getMetaFromState(state)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, ALL_FACING, WORKING)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return EUFluidPump()
    }

    override fun getBlockItem(): Item {
        return item
    }
}