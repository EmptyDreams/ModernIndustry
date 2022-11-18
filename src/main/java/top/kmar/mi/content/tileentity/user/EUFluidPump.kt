package top.kmar.mi.content.tileentity.user

import net.minecraft.client.resources.I18n
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagShort
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.electricity.EleEnergy
import top.kmar.mi.api.graphics.GuiLoader
import top.kmar.mi.api.graphics.components.ButtonCmpt
import top.kmar.mi.api.graphics.components.ProgressBarCmpt
import top.kmar.mi.api.graphics.components.TextCmpt
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.net.messages.block.BlockMessage
import top.kmar.mi.api.net.messages.block.cap.BlockNetworkCapability
import top.kmar.mi.api.pipes.FluidPipeEntity
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.FrontTileEntity
import top.kmar.mi.api.utils.*
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.expands.*
import top.kmar.mi.content.blocks.BlockGuiList
import top.kmar.mi.content.blocks.machine.user.FluidPumpBlock
import top.kmar.mi.data.properties.MIProperty
import top.kmar.mi.data.properties.RelativeDirectionEnum
import java.util.*

/**
 * 水泵方块的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity(FluidPumpBlock.NAME)
@EventBusSubscriber
open class EUFluidPump : FrontTileEntity(), ITickable {
    
    /** 一次性最多运送的流体量 */
    var maxCapability = 500
    /** 电能消耗 */
    var energyRequirement = 150
    /** 水泵面板方向 */
    @field:AutoSave
    internal var panelFacing = EnumFacing.WEST
        set(value) {
            field = value
            val list = maySide(value)
            if (!list.contains(side)) side = list[0]
            send()
        }
    /** 水泵输出方向 */
    @field:AutoSave
    internal var side = EnumFacing.NORTH
        set(value) {
            field = value
            val list = mayPanel(value)
            if (!list.contains(panelFacing)) panelFacing = list[0]
            send()
        }
    /** 是否工作 */
    @field:AutoSave
    private var start = false
        set(value) {
            if (value) addTickable()
            field = value
        }
    /** 存储连接的方块的数量 */
    @field:AutoSave
    private var linkedData = IndexEnumMap(EnumFacing.values())
    /** 是否正在工作 */
    var working = false
        get() = field && start
        private set
    private var consume = 0

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === BlockNetworkCapability.capObj) {
            return BlockNetworkCapability.capObj.cast { data, _ ->
                val value = (data as NBTTagShort).int
                side = EnumFacing.values()[value ushr 8]
                panelFacing = EnumFacing.values()[value and 0xFF]
                world.markBlockRangeForRenderUpdate(pos, pos)
            }
        }
        if (facing?.axis === side.axis) return null
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === BlockNetworkCapability.capObj) return true
        if (capability === FLUID_HANDLER_CAPABILITY && facing?.axis === side.axis) return true
        return super.hasCapability(capability, facing)
    }

    /** 连接一个流体方块 */
    fun linkFluidBlock(entity: TileEntity,  facing: EnumFacing): Boolean {
        if (linkedData[facing]) return true
        if (
            entity.hasCapability(FLUID_HANDLER_CAPABILITY, facing.opposite) &&
            entity !is EUFluidPump
        ) {
            if (side.axis !== facing.axis) {
                if (!linkedData.isInit()) return false
                side = facing
                markDirty()
            }
            linkedData[facing] = true
            if (entity is FluidPipeEntity) entity.linkFluidBlock(this, facing.opposite)
            return true
        }
        return false
    }
    
    /** 断开与指定方向的连接 */
    fun unlinkFluidBlock(facing: EnumFacing) {
        linkedData[facing] = false
        world.markChunkDirty(pos, this)
    }
    
    override fun update() {
        if (world.isClient()) return removeTickable()
        if (!start) {
            working = false
            updateShow(false)
            return removeTickable()
        }
        val energy = requestEnergy(energyRequirement)
        checkEnergy(
            energy, minVoltage, maxVoltage,
            { return updateShow(false) },
            { return updateShow(true) },
            { return explode(1.5F, true) }
        )
        if (!working) updateShow(true)
        work()
    }

    /**
     * 执行水泵的工作逻辑
     * @return 消耗多少电能
     */
    private fun work() {
        val from = side
        val fromEntity = world.getBlockEntity(pos.offset(from)) ?: return
        val toEntity = world.getBlockEntity(pos.offset(from.opposite)) ?: return
        val stack = fromEntity.exportFluid(
            maxCapability, from.opposite, FluidPipeEntity.maxPower, false
        ) ?: return
        val amount = toEntity.insertFluid(stack, from, true)
        if (amount != 0)
            fromEntity.exportFluid(amount, from.opposite, FluidPipeEntity.maxPower, true)
    }
    
    private fun updateShow(working: Boolean) {
        this.working = working
        val old = world.getBlockState(pos)
        val new = old.withProperty(MIProperty.WORKING, working)
        world.setBlockWithMark(pos, new)
    }

    /** 计算出水口在面板的哪一个方向 */
    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    fun calculateFront() = when (panelFacing.axis) {
        EnumFacing.Axis.X -> {
            val result = when (side) {
                EnumFacing.NORTH -> RelativeDirectionEnum.LEFT
                EnumFacing.SOUTH -> RelativeDirectionEnum.RIGHT
                else -> throw AssertionError("内部错误")
            }
            if (panelFacing == EnumFacing.WEST) result else result.opposite()
        }
        EnumFacing.Axis.Y -> {
            val result = when (side) {
                EnumFacing.SOUTH -> RelativeDirectionEnum.UP
                EnumFacing.NORTH -> RelativeDirectionEnum.DOWN
                EnumFacing.EAST -> RelativeDirectionEnum.LEFT
                EnumFacing.WEST -> RelativeDirectionEnum.RIGHT
                else -> throw AssertionError("内部错误")
            }
            if (panelFacing == EnumFacing.DOWN) result else result.opposite()
        }
        EnumFacing.Axis.Z -> {
            val result = when (side) {
                EnumFacing.WEST -> RelativeDirectionEnum.LEFT
                EnumFacing.EAST -> RelativeDirectionEnum.RIGHT
                else -> throw AssertionError("内部错误")
            }
            if (panelFacing == EnumFacing.SOUTH) result else result.opposite()
        }
    }

    override fun getFront() = panelFacing
    
    override fun getUpdateTag(): NBTTagCompound = super.getUpdateTag().apply {
        setByte("side", side.ordinal.toByte())
        setByte("fac", panelFacing.ordinal.toByte())
    }
    
    override fun handleUpdateTag(tag: NBTTagCompound) {
        super.handleUpdateTag(tag)
        if (tag.hasKey("fac")) {
            val cap = getCapability(BlockNetworkCapability.capObj, null)!!
            cap.receive(tag, null)
        }
    }
    
    private var _sendFlag = false
    
    private fun send() {
        if (_sendFlag || world == null || world.isClient()) return
        _sendFlag = true
        TickHelper.addServerTask {
            _sendFlag = false
            val data = NBTTagShort((side.ordinal.shl(8) or panelFacing.ordinal).toShort())
            BlockMessage.sendToClient(this, data)
            true
        }
    }
    
    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    companion object {
        
        const val maxVoltage = EleEnergy.COMMON + 100
        const val minVoltage = EleEnergy.COMMON - 30
        
        private val MAY_SIDE_X = listOf(EnumFacing.NORTH, EnumFacing.SOUTH)
        private val MAY_SIDE_Y = listOf(*EnumFacing.HORIZONTALS)
        private val MAY_SIDE_Z = listOf(EnumFacing.WEST, EnumFacing.EAST)

        private val MAY_PANEL_X =
            listOf(EnumFacing.NORTH, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.DOWN)
        private val MAY_PANEL_Y = listOf<EnumFacing>()
        private val MAY_PANEL_Z =
            listOf(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.UP)
        
        /** 判断指定的面板方向与管道方向是否匹配 */
        fun match(panel: EnumFacing, side: EnumFacing) = maySide(panel).contains(side)

        /**
         * 获取指定面板朝向下出水口管道可能在哪些方向
         * @return 一个不可修改的列表
         */
        fun maySide(panel: EnumFacing) = when (panel.axis) {
            EnumFacing.Axis.X -> MAY_SIDE_X
            EnumFacing.Axis.Y -> MAY_SIDE_Y
            EnumFacing.Axis.Z -> MAY_SIDE_Z
        }

        /**
         * 获取指定出/入水口方向下面板可能在哪些方向
         * @return 一个不可修改的列表
         */
        fun mayPanel(side: EnumFacing) = when (side.axis) {
            EnumFacing.Axis.X -> MAY_PANEL_X
            EnumFacing.Axis.Z -> MAY_PANEL_Z
            EnumFacing.Axis.Y -> MAY_PANEL_Y
        }

        @JvmStatic
        @SubscribeEvent
        fun registryGuiInfo(event: GuiLoader.MIGuiRegistryEvent) {
            event.registryInitTask(BlockGuiList.fluidPump) { gui ->
                val button = gui.queryCmpt("button") as ButtonCmpt
                val pump = gui.tileEntity as EUFluidPump
                button.addEventListener(IGraphicsListener.mouseClick) {
                    pump.start = !pump.start
                    pump.markDirty()
                    if (it != null) it.send2Service = true
                }
            }
            event.registryLoopTask(BlockGuiList.fluidPump) { gui ->
                val consume = gui.getElementByID("consume") as ProgressBarCmpt
                val pump = gui.tileEntity as EUFluidPump
                // 更新瞬时能量消耗数据
                consume.max = pump.maxCapability
                consume.value = pump.consume
            }
            event.registryClientLoopTask(BlockGuiList.fluidPump) { gui ->
                val consume = gui.getElementByID("consume") as ProgressBarCmpt
                val pump = gui.tileEntity as EUFluidPump
                val texts = gui.queryCmptAll("p")
                    .map { it.client as TextCmpt.TextCmptClient }
                texts[1].text = I18n.format("mi.gui.fluid_pump.consume",
                    "${consume.value}/${consume.max}")
                val button = gui.queryCmpt("button")
                button!!.attributes["value"] = I18n.format(if (pump.start) "mi.gui.open" else "mi.gui.close")
            }
        }

    }

}