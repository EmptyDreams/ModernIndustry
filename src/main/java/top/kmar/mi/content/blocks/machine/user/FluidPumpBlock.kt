package top.kmar.mi.content.blocks.machine.user

import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.kmar.mi.api.register.block.AutoBlockRegister
import top.kmar.mi.api.utils.properties.MIProperty.Companion.WORKING
import top.kmar.mi.api.utils.properties.MIProperty.Companion.createAllDirection
import top.kmar.mi.content.blocks.base.MachineBlock
import top.kmar.mi.content.items.base.ItemBlockExpand
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

    private val item: ItemBlock by lazy(PUBLICATION) { ItemBlockExpand(this) }

    init {
        defaultState = blockState.baseState
            .withProperty(createAllDirection("front"), EnumFacing.NORTH)
            .withProperty(createAllDirection("panel"), EnumFacing.WEST)
            .withProperty(WORKING, false)
    }

    override fun getStateForPlacement(
        world: World, pos: BlockPos,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float,
        meta: Int,
        placer: EntityLivingBase, hand: EnumHand
    ): IBlockState = defaultState

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState

    override fun getMetaFromState(state: IBlockState) = 0

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(
            this,
            createAllDirection("front"),
            createAllDirection("panel"),
            WORKING)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return EUFluidPump()
    }

    override fun getBlockItem(): Item {
        return item
    }

    override fun initTileEntity(
        stack: ItemStack,
        player: EntityPlayer, world: World, pos: BlockPos,
        side: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        val result = EUFluidPump()
        
        return true
    }

}