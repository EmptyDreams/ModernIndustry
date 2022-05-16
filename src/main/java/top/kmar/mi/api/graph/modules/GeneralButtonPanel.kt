@file:Suppress("LeakingThis")

package top.kmar.mi.api.graph.modules

import top.kmar.mi.ModernIndustry.MODID
import top.kmar.mi.api.graph.listeners.MouseData
import top.kmar.mi.api.graph.listeners.mouse.IMouseActionListener
import top.kmar.mi.api.graph.listeners.mouse.IMouseEnteredListener
import top.kmar.mi.api.graph.listeners.mouse.IMouseExitedListener
import top.kmar.mi.api.graph.utils.GeneralPanel
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.graph.utils.json.GuiTextureJsonRegister
import top.kmar.mi.api.graph.utils.managers.TextureCacheManager
import top.kmar.mi.api.utils.data.math.Size2D
import java.awt.Graphics

/**
 * 按钮的通用服务端实现
 * @author EmptyDreams
 */
open class GeneralButtonPanel(
    /** 鼠标左键点击时触发的任务 */
    private var action: (Float, Float) -> Unit
) : GeneralPanel() {

    init {
        //保持客户端服务端事件序列一致
        registryListener(IMouseActionListener { mouseX, mouseY ->
            action(mouseX, mouseY)
            MouseData.EMPTY_DATA
        })
        registryListener(IMouseEnteredListener { _, _ -> MouseData.EMPTY_DATA })
        registryListener(IMouseExitedListener { MouseData.EMPTY_DATA })
    }

    fun setAction(action: (Float, Float) -> Unit) {
        this.action = action
    }

}

/**
 * 通用按钮的客户端实现
 * @author EmptyDreams
 */
open class GeneralButtonPanelClient(
    x: Int, y: Int, width: Int, height: Int,
    /** 鼠标事件是否同步到服务端 */
    isSync: Boolean,
    /** 鼠标左键点击时触发的任务 */
    action: (Float, Float) -> Unit,
    /** 普通材质管理器 */
    private val generalImage: TextureCacheManager = generalCacheManager,
    /** 鼠标覆盖时的材质管理器 */
    private val coveredImage: TextureCacheManager = coveredCacheManager
) : InvisibleButtonPanelClient(x, y, width, height, isSync, action) {

    /** 鼠标是否在按钮上方 */
    var isMouseIn = false
        private set

    init {
        registryListener(IMouseEnteredListener { _, _ ->
            isMouseIn = true
            MouseData.EMPTY_DATA
        })
        registryListener(IMouseExitedListener {
            isMouseIn = false
            MouseData.EMPTY_DATA
        })
    }

    override fun paint(painter: GuiPainter) {
        val manager = if (isMouseIn) coveredImage else generalImage
        val texture = manager[size].bindTexture()
        painter.drawTexture(0, 0, width, height, texture)
    }

    companion object {

        private const val GENERAL_KEY = "general"
        private const val COVERED_KEY = "covered"

        val generalCacheManager =
            TextureCacheManager { size, graphics -> drawTexture(GENERAL_KEY, size, graphics) }

        val coveredCacheManager =
            TextureCacheManager { size, graphics -> drawTexture(COVERED_KEY, size, graphics) }

        private fun drawTexture(key: String, size: Size2D, graphics: Graphics) {
            val (srcImage, rect) = GuiTextureJsonRegister[MODID, key]
            for (x in 0 until size.width step rect.width) {
                for (y in 0 until size.height step rect.height) {
                    srcImage.draw(graphics, x, y)
                }
            }
        }

    }

}