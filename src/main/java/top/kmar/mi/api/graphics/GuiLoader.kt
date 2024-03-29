package top.kmar.mi.api.graphics

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
import top.kmar.mi.api.event.GuiRegistryFinishedEvent
import top.kmar.mi.api.graphics.parser.GuiFileParser
import top.kmar.mi.api.graphics.utils.GuiRegedit
import top.kmar.mi.api.regedits.others.AutoLoader
import top.kmar.mi.api.utils.MISysInfo
import java.util.function.Consumer

/**
 * GUI加载器
 * @author EmptyDreams
 */
@AutoLoader
object GuiLoader : IGuiHandler {

    val regedit: GuiRegedit

    init {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this)
        val tmpRegedit = GuiRegedit()
        val event = MIGuiRegistryEvent(tmpRegedit)
        GuiFileParser.registryAll(event)
        try {
            MinecraftForge.EVENT_BUS.post(event)
        } catch (e: Throwable) {
            MISysInfo.err("注册GUI时发生了意料之外的错误", e)
        }
        regedit = tmpRegedit.sort()
        try {
            MinecraftForge.EVENT_BUS.post(GuiRegistryFinishedEvent())
        } catch (e: Throwable) {
            MISysInfo.err("GUI注册完成阶段发生了意料之外的错误", e)
        }
    }

    /** 构建一个服务端的GUI对象 */
    override fun getServerGuiElement(
        id: Int,
        player: EntityPlayer, world: World,
        x: Int, y: Int, z: Int
    ): BaseGraphics {
        val key = regedit.getKey(id)
        val pos = BlockPos(x, y, z)
        val gui = regedit.buildGui(key, player, pos)
        regedit.invokeInitTask(gui)
        gui.installParent()
        return gui
    }

    /** 构建一个客户端的GUI对象 */
    @SideOnly(Side.CLIENT)
    override fun getClientGuiElement(
        id: Int,
        player: EntityPlayer, world: World,
        x: Int, y: Int, z: Int
    ): BaseGraphicsClient {
        return getServerGuiElement(id, player, world, x, y, z).client
    }

    fun getID(key: ResourceLocation) = regedit.getID(key)

    /** 注册一个客户端GUI，该函数不能在事件注册阶段调用 */
    fun registryClientGui(key: ResourceLocation, root: DocumentCmpt) {
        regedit.registryClientGui(key, root)
    }

    /** @see GuiRegedit.invokeLoopTask */
    fun invokeLoopTask(gui: BaseGraphics) {
        regedit.invokeLoopTask(gui)
    }

    /** @see GuiRegedit.invokeClientLoopTask */
    @SideOnly(Side.CLIENT)
    fun invokeClientLoopTask(gui: BaseGraphics) {
        regedit.invokeClientLoopTask(gui)
    }

    class MIGuiRegistryEvent(private val regedit: GuiRegedit?) : Event() {

        constructor() : this(null)

        /** 注册一个客户端服务端通用的GUI，注册阶段过后不能调用该函数 */
        fun registry(key: ResourceLocation, root: DocumentCmpt) {
            regedit!!.registryGui(key, root)
        }

        /** 注册一个客户端GUI */
        fun registryClient(key: ResourceLocation, root: DocumentCmpt) {
            regedit!!.registryClientGui(key, root)
        }

        /** @see GuiRegedit.registryLoopTask */
        fun registryLoopTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
            regedit!!.registryLoopTask(key, task)
        }

        /** @see GuiRegedit.registryInitTask */
        fun registryInitTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
            regedit!!.registryInitTask(key, task)
        }

        /** @see GuiRegedit.registryClientLoopTask */
        fun registryClientLoopTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
            regedit!!.registryClientLoopTask(key, task)
        }

        /** 注册一个双端都会运行的循环任务 */
        fun registryCommonLoopTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
            regedit!!.registryLoopTask(key, task)
            regedit.registryClientLoopTask(key, task)
        }

    }

}