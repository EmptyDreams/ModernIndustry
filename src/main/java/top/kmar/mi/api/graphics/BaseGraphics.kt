package top.kmar.mi.api.graphics

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import kotlin.LazyThreadSafetyMode.NONE

/**
 *
 * @author EmptyDreams
 */
abstract class BaseGraphics : Container() {

    /** 容器对象 */
    val document = DocumentCmpt()
    @get:SideOnly(Side.CLIENT)
    val client by lazy(NONE) { document.client as BaseGraphicsClient }

    /** 是否可以被指定玩家打开 */
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    /**
     * 初始化GUI
     * @param player 打开GUI的玩家
     * @param pos 触发GUI的位置
     */
    abstract fun init(player: EntityPlayer, pos: BlockPos)

    /** 添加一个控件 */
    fun addChild(cmpt: Cmpt) = document.addChild(cmpt)
    /** 移除一个控件 */
    fun removeChild(cmpt: Cmpt) = document.removeChild(cmpt)
    /** 注册一个事件 */
    fun addEventListener(
        name: String,
        listener: IGraphicsListener<*>
    ) = document.addEventListener(name, listener)
    /** 移除一个事件 */
    fun removeEventListener(
        name: String,
        listener: IGraphicsListener<*>
    ) = document.removeEventListener(name, listener)
    /** 发布一个事件 */
    fun dispatchEvent(name: String, message: ListenerData) = document.dispatchEvent(name, message)
    /** 通过ID获取元素 */
    fun getElementByID(id: String) = document.getElementByID(id)

    inner class DocumentCmpt : Cmpt("document") {

        @SideOnly(Side.CLIENT)
        override fun initClientObj(): CmptClient =
            BaseGraphicsClient(this@BaseGraphics)

    }

}