package top.kmar.mi.content.blocks.machine.user

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import top.kmar.mi.api.regedits.block.annotations.AutoBlockRegister
import top.kmar.mi.api.utils.expands.getPlacingDirection
import top.kmar.mi.api.utils.expands.whatFacing
import top.kmar.mi.content.blocks.BlockGuiList
import top.kmar.mi.content.blocks.CommonUtil
import top.kmar.mi.content.blocks.base.MachineBlock
import top.kmar.mi.content.items.base.ItemBlockExpand
import top.kmar.mi.content.tileentity.user.EUFluidPump
import top.kmar.mi.data.properties.MIProperty.Companion.WORKING
import top.kmar.mi.data.properties.MIProperty.Companion.createAllDirection
import top.kmar.mi.data.properties.MIProperty.Companion.createRelativeDirection
import top.kmar.mi.data.properties.RelativeDirectionEnum
import kotlin.LazyThreadSafetyMode.PUBLICATION

@AutoBlockRegister(registryName = FluidPumpBlock.NAME, field = "innerInstance")
open class FluidPumpBlock : MachineBlock(Material.IRON) {

    companion object {

        const val NAME = "fluid_pump"

        @JvmStatic
        private var innerInstance: FluidPumpBlock? = null
        val INSTANCE: FluidPumpBlock
            get() = innerInstance!!

        val PROPERTY_SIDE = createRelativeDirection("front")
        val PROPERTY_PANEL = createAllDirection("panel")

    }

    init {
        defaultState = blockState.baseState
            .withProperty(PROPERTY_SIDE, RelativeDirectionEnum.LEFT)
            .withProperty(PROPERTY_PANEL, EnumFacing.WEST)
            .withProperty(WORKING, false)
    }

    override fun getStateForPlacement(
        world: World, pos: BlockPos,
        facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float,
        meta: Int,
        placer: EntityLivingBase, hand: EnumHand
    ): IBlockState = defaultState

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getStateFromMeta(meta: Int): IBlockState = defaultState

    override fun getMetaFromState(state: IBlockState) = 0

    override fun createBlockState() = BlockStateContainer(this, PROPERTY_SIDE, PROPERTY_PANEL, WORKING)

    override fun createNewTileEntity(worldIn: World, meta: Int) = EUFluidPump()

    @Suppress("OVERRIDE_DEPRECATION")
    override fun isOpaqueCube(state: IBlockState): Boolean = false

    @Suppress("OVERRIDE_DEPRECATION")
    override fun isFullCube(state: IBlockState): Boolean = false

    @Suppress("OVERRIDE_DEPRECATION")
    override fun neighborChanged(
        state: IBlockState, world: World, pos: BlockPos,
        blockIn: Block, fromPos: BlockPos
    ) {
        @Suppress("DEPRECATION")
        super.neighborChanged(state, world, pos, blockIn, fromPos)
        val facing = pos.whatFacing(fromPos)
        val pump = world.getTileEntity(pos) as EUFluidPump
        pump.linkFluidBlock(facing)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val te = worldIn.getTileEntity(pos)
        @Suppress("DEPRECATION")
        if (te !is EUFluidPump) return super.getActualState(state, worldIn, pos)
        return defaultState.withProperty(PROPERTY_SIDE, te.calculateFront())
            .withProperty(PROPERTY_PANEL, te.panelFacing)
            .withProperty(WORKING, te.working)
    }

    override fun onBlockActivated(
        worldIn: World, pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ) = CommonUtil.openGui(playerIn, BlockGuiList.fluidPump, pos)

    private val item: ItemBlock by lazy(PUBLICATION) { ItemBlockExpand(this) }

    override fun getBlockItem() = item

    override fun initTileEntity(
        stack: ItemStack,
        player: EntityPlayer, world: World, pos: BlockPos,
        side: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        val result = EUFluidPump().apply {
            this.world = world
        }
        result.panelFacing = player.getPlacingDirection(pos).opposite
        result.side = when (result.panelFacing) {
            EnumFacing.DOWN -> result.side
            EnumFacing.UP -> player.horizontalFacing.rotateY()
            else -> result.panelFacing.rotateY()
        }
        putBlock(world, pos, defaultState, result, player, stack)
        return true
    }

}