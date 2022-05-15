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

/**
 * 不可见的按钮
 * @author EmptyDreams
 */
open class GeneralButtonPanel(
    /** 鼠标点击时触发 */
    private var action: (Float, Float) -> Unit
) : GeneralPanel() {

    init {
        @Suppress("LeakingThis")
        registryListener(IMouseActionListener { x, y ->
            action(x, y)
            MouseData.EMPTY_DATA
        })
    }

    fun setAction(action: (Float, Float) -> Unit) {
        this.action = action
    }

}

@Suppress("LeakingThis")
open class GeneralButtonPanelClient(
    x: Int, y: Int, width: Int, height: Int,
    action: (Float, Float) -> Unit,
    private val painter: (Size2D, ButtonState, GuiPainter) -> Unit
) : InvisibleButtonPanelClient(x, y, width, height, action) {

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
        val state = if (isMouseIn) ButtonState.COVERED else ButtonState.GENERAL
        this.painter(size, state, painter)
    }

    companion object {

        private const val GENERAL_KEY = "general"
        private const val COVERED_KEY = "covered"

        val generalCacheManager = TextureCacheManager { size, graphics ->
            val (srcImage, rect) = GuiTextureJsonRegister[MODID, GENERAL_KEY]
            TODO("等待补充")
        }

    }

}

enum class ButtonState {

    /** 被鼠标覆盖 */
    COVERED,
    /** 普通情况 */
    GENERAL

}