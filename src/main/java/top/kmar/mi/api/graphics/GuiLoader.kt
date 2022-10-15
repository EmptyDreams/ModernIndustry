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
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.parser.GuiFileParser
import top.kmar.mi.api.graphics.parser.GuiStyleParser
import top.kmar.mi.api.graphics.utils.GuiRegedit
import top.kmar.mi.api.register.others.AutoLoader

/**
 * GUI加载器
 * @author EmptyDreams
 */
@AutoLoader
object GuiLoader : IGuiHandler {

    val regedit: GuiRegedit

    init {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this)
        GuiFileParser.printCount()
        val tmpRegedit = GuiRegedit()
        MinecraftForge.EVENT_BUS.post(MIGuiRegistryEvent(tmpRegedit))
        regedit = tmpRegedit
    }

    /** 构建一个服务端的GUI对象 */
    override fun getServerGuiElement(ID: Int,
                                     player: EntityPlayer, world: World,
                                     x: Int, y: Int, z: Int
    ): BaseGraphics {
        val key = regedit.getKey(ID)
        val gui = regedit.buildGui(key, player)
        regedit.invokeInitTask(key, gui, player, BlockPos(x, y, z))
        gui.document.installParent(Cmpt.EMPTY_CMPT)
        return gui
    }

    /** 构建一个客户端的GUI对象 */
    @SideOnly(Side.CLIENT)
    override fun getClientGuiElement(
        ID: Int,
        player: EntityPlayer, world: World,
        x: Int, y: Int, z: Int
    ): BaseGraphicsClient {
        val client = getServerGuiElement(ID, player, world, x, y, z).client
        client.addInitTask { GuiStyleParser.initStyle(regedit.getKey(ID), client.service) }
        return client
    }

    fun getID(key: ResourceLocation) = regedit.getID(key)

    /** @see GuiRegedit.registryLoopTask */
    fun registryLoopTask(key: ResourceLocation, task: (BaseGraphics) -> Unit) {
        regedit.registryLoopTask(key, task)
    }

    class MIGuiRegistryEvent(private val regedit: GuiRegedit?) : Event() {

        constructor() : this(null)

        /** 注册一个客户端服务端通用的GUI，注册阶段过后不能调用该函数 */
        fun registry(key: ResourceLocation, root: BaseGraphics.DocumentCmpt) {
            regedit!!.registryGui(key, root)
        }

        /** 注册一个客户端的GUI，该函数可在注册事件完毕后继续注册 */
        @SideOnly(Side.CLIENT)
        fun registryClient(key: ResourceLocation, root: BaseGraphics.DocumentCmpt) {
            regedit!!.registryClientGui(key, root)
        }

        /** @see GuiRegedit.registryLoopTask */
        fun registryLoopTask(key: ResourceLocation, task: (BaseGraphics) -> Unit) {
            regedit!!.registryLoopTask(key, task)
        }

        /** @see GuiRegedit.registryInitTask */
        fun registryInitTask(key: ResourceLocation, task: GuiRegedit.InitTask) {
            regedit!!.registryInitTask(key, task)
        }

    }

}