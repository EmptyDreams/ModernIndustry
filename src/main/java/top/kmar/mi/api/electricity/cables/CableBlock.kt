@file:Suppress("OVERRIDE_DEPRECATION")

package top.kmar.mi.api.electricity.cables

import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.regedits.block.BlockItemHelper
import top.kmar.mi.api.utils.StringUtil
import top.kmar.mi.api.utils.expands.random
import top.kmar.mi.api.utils.expands.whatFacing
import top.kmar.mi.content.items.base.ItemBlockExpand
import java.util.*

/**
 * 导线方块
 * @author EmptyDreams
 */
class CableBlock(name: String) : BlockContainer(Material.CIRCUITS), BlockItemHelper {

    private val item: Item

    init {
        unlocalizedName = StringUtil.getUnlocalizedName(name)
        soundType = SoundType.SNOW
        setHardness(0.35F)
        setCreativeTab(ModernIndustry.TAB_WIRE)
        setRegistryName(ModernIndustry.MODID, name)
        defaultState = blockState.baseState.withProperty(UP, false)
            .withProperty(DOWN, false)
            .withProperty(WEST, false)
            .withProperty(EAST, false)
            .withProperty(NORTH, false)
            .withProperty(SOUTH, false)
        item = ItemBlockExpand(this)
    }

    // 根据 TE 数据获取 `state`
    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val cable = worldIn.getTileEntity(pos) as EleCableEntity
        return state.withProperty(UP, cable.isLink(EnumFacing.UP))
            .withProperty(DOWN, cable.isLink(EnumFacing.DOWN))
            .withProperty(EAST, cable.isLink(EnumFacing.EAST))
            .withProperty(WEST, cable.isLink(EnumFacing.WEST))
            .withProperty(NORTH, cable.isLink(EnumFacing.NORTH))
            .withProperty(SOUTH, cable.isLink(EnumFacing.SOUTH))
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos
    ): AxisAlignedBB? = Block.NULL_AABB

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75)
    }

    override fun addCollisionBoxToList(
        state: IBlockState,
        worldIn: World, pos: BlockPos,
        entityBox: AxisAlignedBB,
        collidingBoxes: MutableList<AxisAlignedBB>,
        entityIn: Entity?, isActualState: Boolean
    ) {
        val real = if (isActualState) state else getActualState(state, worldIn, pos)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boxPoint)
        if (real.getValue(SOUTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, boxSouth)
        if (real.getValue(NORTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, boxNorth)
        if (real.getValue(WEST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, boxWest)
        if (real.getValue(EAST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, boxEast)
    }

    // 玩家使用剪刀右键点击的时候拆除导线
    override fun onBlockActivated(
        worldIn: World, pos: BlockPos, state: IBlockState,
        playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        val heldItem = playerIn.getHeldItem(hand)
        if (heldItem.item == Items.SHEARS) {
            heldItem.damageItem(1, playerIn)
            playerIn.dropItem(ItemStack(getItemDropped(state, random, 0), quantityDropped(random)), false)
            worldIn.setBlockToAir(pos)
            return true
        }
        return false
    }

    // 附近方块更新时自动连接/断开
    override fun neighborChanged(
        state: IBlockState, worldIn: World, pos: BlockPos,
        blockIn: Block, fromPos: BlockPos
    ) {
        val facing = pos.whatFacing(fromPos)
        val entity = worldIn.getTileEntity(pos) as EleCableEntity
        entity.updateLinkData(facing)
    }

    // 放置时优先连接右键点击的方块
    override fun initTileEntity(
        stack: ItemStack, player: EntityPlayer,
        world: World, pos: BlockPos, side: EnumFacing,
        hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        val facing = side.opposite
        val thatEntity = world.getTileEntity(pos.offset(facing))
        if (thatEntity !is EleCableEntity) return false
        val entity = EleCableEntity()
        entity.linkCable(facing, thatEntity)
        putBlock(world, pos, defaultState, entity, player, stack)
        return true
    }

    override fun createBlockState(): BlockStateContainer =
        BlockStateContainer(this, UP, DOWN, SOUTH, NORTH, WEST, EAST)

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int) = item

    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

    override fun createNewTileEntity(worldIn: World, meta: Int) = EleCableEntity()

    override fun getBlockItem() = item

    override fun isOpaqueCube(state: IBlockState) = false

    override fun isFullCube(state: IBlockState) = false

    override fun getItem(worldIn: World, pos: BlockPos, state: IBlockState) = ItemStack(item)

    override fun getMetaFromState(state: IBlockState) = 0

    companion object {

        val SOUTH: PropertyBool = PropertyBool.create("south")
        val NORTH: PropertyBool = PropertyBool.create("north")
        val WEST: PropertyBool = PropertyBool.create("west")
        val EAST: PropertyBool = PropertyBool.create("east")
        val UP: PropertyBool = PropertyBool.create("up")
        val DOWN: PropertyBool = PropertyBool.create("down")

        val boxPoint = AxisAlignedBB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625)
        val boxEast = AxisAlignedBB(0.625, 0.375, 0.375, 1.0, 0.625, 0.625)
        val boxWest = AxisAlignedBB(0.0, 0.375, 0.375, 0.375, 0.625, 0.625)
        val boxSouth = AxisAlignedBB(0.375, 0.375, 0.625, 0.625, 0.625, 1.0)
        val boxNorth = AxisAlignedBB(0.375, 0.375, 0.0, 0.625, 0.625, 0.375)

    }

}