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
import top.kmar.mi.api.capabilities.fluid.FluidCapability
import top.kmar.mi.api.capabilities.fluid.IFluid
import top.kmar.mi.api.register.block.AutoBlockRegister
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.getPlacingDirection
import top.kmar.mi.content.blocks.CommonUtil
import top.kmar.mi.content.blocks.base.MachineBlock
import top.kmar.mi.content.gui.FluidPumpFrame
import top.kmar.mi.content.items.base.ItemBlockExpand
import top.kmar.mi.content.tileentity.user.EUFluidPump
import top.kmar.mi.data.info.MIProperty.Companion.WORKING
import top.kmar.mi.data.info.MIProperty.Companion.createAllDirection
import top.kmar.mi.data.info.MIProperty.Companion.createRelativeDirection
import top.kmar.mi.data.info.RelativeDirectionEnum
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

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState

    override fun getMetaFromState(state: IBlockState) = 0

    override fun createBlockState() = BlockStateContainer(this, PROPERTY_SIDE, PROPERTY_PANEL, WORKING)

    override fun createNewTileEntity(worldIn: World, meta: Int) = EUFluidPump()

    override fun isOpaqueCube(state: IBlockState?) = false

    override fun isFullCube(state: IBlockState?) = false

    override fun neighborChanged(
        state: IBlockState, world: World, pos: BlockPos,
        blockIn: Block, fromPos: BlockPos
    ) {
        @Suppress("DEPRECATION")
        super.neighborChanged(state, world, pos, blockIn, fromPos)
        neighborChangedHelper(world, pos, fromPos)
    }

    protected fun neighborChangedHelper(world: World, pos: BlockPos, fromPos: BlockPos) {
        val facing = WorldUtil.whatFacing(pos, fromPos)
        val pump = world.getTileEntity(pos) as EUFluidPump
        val fromTE = world.getTileEntity(fromPos)
        if (fromTE === null) pump.unlink(facing)
        else {
            val fluid = fromTE.getCapability(FluidCapability.TRANSFER, null) ?: return
            linkBoth(pump, fluid, facing)
            fromTE.markDirty()
        }
        pump.markDirty()
    }

    protected fun linkBoth(pump: EUFluidPump, from: IFluid, facing: EnumFacing) {
        if (pump.isLinked(facing)) return
        if (pump.linkFluid(facing)) {
            if (!from.linkFluid(facing.opposite)) pump.unlink(facing)
        }
    }

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
    ) = CommonUtil.openGui(playerIn, FluidPumpFrame.NAME, worldIn, pos)

    private val item: ItemBlock by lazy(PUBLICATION) { ItemBlockExpand(this) }

    override fun getBlockItem() = item

    override fun initTileEntity(
        stack: ItemStack,
        player: EntityPlayer, world: World, pos: BlockPos,
        side: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        val result = EUFluidPump()
        result.panelFacing = player.getPlacingDirection(pos).opposite
        if (result.panelFacing.axis === EnumFacing.Axis.Y) {
            result.side = EUFluidPump.maySide(player.horizontalFacing)[0]
        }
        putBlock(world, pos, defaultState, result, player, stack)
        return true
    }

}