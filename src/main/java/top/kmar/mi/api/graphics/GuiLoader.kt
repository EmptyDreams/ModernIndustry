package top.kmar.mi.api.graphics

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.event.PlayerOpenGraphicsEvent
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.parser.GuiFileParser
import top.kmar.mi.api.register.others.AutoLoader
import java.util.concurrent.atomic.AtomicInteger

/**
 * GUI加载器
 * @author EmptyDreams
 */
@AutoLoader
object GuiLoader : IGuiHandler {

    /** 存储数字注册列表 */
    private val numRegisters = Int2ObjectRBTreeMap<BaseGraphics.DocumentCmpt>()
    /** 存储通用注册表 */
    private val registers = Object2IntRBTreeMap<ResourceLocation>().apply {
        defaultReturnValue(Int.MIN_VALUE)
    }
    /** 注册下标 */
    private val registryIndex = AtomicInteger(0)
    /** 客户端注册下标 */
    @SideOnly(Side.CLIENT)
    private val registryIndexClient = AtomicInteger(0)
    var registryFinish = false
        private set

    init {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this)
        GuiFileParser.printCount()
        MinecraftForge.EVENT_BUS.post(MIGuiRegistryEvent())
        registryFinish = true
    }

    /** 构建一个服务端的GUI对象 */
    override fun getServerGuiElement(ID: Int,
                                     player: EntityPlayer, world: World,
                                     x: Int, y: Int, z: Int
    ): BaseGraphics {
        val root = numRegisters[ID] ?: throw IllegalArgumentException("指定ID[$ID]的GUI不存在")
        return BaseGraphics(root).apply {
            init(player, BlockPos(x, y, z))
            MinecraftForge.EVENT_BUS.post(PlayerOpenGraphicsEvent(player, this, ID, x, y, z))
            document.installParent(Cmpt.EMPTY_CMPT)
        }
    }

    /** 构建一个客户端的GUI对象 */
    @SideOnly(Side.CLIENT)
    override fun getClientGuiElement(
        ID: Int,
        player: EntityPlayer, world: World,
        x: Int, y: Int, z: Int
    ) = getServerGuiElement(ID, player, world, x, y, z).client

    fun getID(key: ResourceLocation): Int {
        val result = registers.getInt(key)
        if (result == Int.MIN_VALUE) throw IndexOutOfBoundsException("未找到指定key[$key]值")
        return result
    }

    class MIGuiRegistryEvent : Event() {

        /** 检查此时是否可以注册 */
        fun check() = !registryFinish

        /** 注册一个客户端服务端通用的GUI，注册阶段过后不能调用该函数 */
        fun registry(key: ResourceLocation, root: BaseGraphics.DocumentCmpt): Int {
            if (registryFinish) throw AssertionError("GUI必须通过事件注册")
            if (key in registers) throw AssertionError("注册的Key[$key]在注册表中已存在")
            val id = registryIndex.incrementAndGet()
            registers[key] = id
            numRegisters[id] = root
            return id
        }

        /** 注册一个客户端的GUI，该函数可在注册事件完毕后继续注册 */
        @SideOnly(Side.CLIENT)
        fun registryClient(key: ResourceLocation, root: BaseGraphics.DocumentCmpt): Int {
            if (key in registers) throw AssertionError("注册的Key[$key]在注册表中已存在")
            val id = registryIndexClient.decrementAndGet()
            registers[key] = id
            numRegisters[id] = root
            return id
        }

    }

}