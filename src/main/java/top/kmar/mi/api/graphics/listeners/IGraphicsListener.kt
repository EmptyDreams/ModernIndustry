package top.kmar.mi.api.graphics.listeners

/**
 * 事件接口
 * @author EmptyDreams
 */
fun interface IGraphicsListener<T : ListenerData> {

    companion object {

        /** 鼠标左键点击 */
        const val mouseClick = "click"
        /** 鼠标右键点击 */
        const val mouseRightClick = "rightClick"
        /** 鼠标中键点击 */
        const val mouseMiddleClick = "middleClick"
        /** 鼠标释放 */
        const val mouseReleased = "mouseReleased"
        /** 鼠标滚轮滚动 */
        const val mouseScroll = "scroll"
        /** 鼠标进入 */
        const val mouseEnter = "mouseEnter"
        /** 鼠标离开 */
        const val mouseExit = "mouseExit"
        /** 键盘按下 */
        const val keyboardPressed = "keyboardPressed"
        /** 键盘释放 */
        const val keyboardReleased = "keyboardReleased"

    }

    /** 触发事件 */
    fun active(`data`: T)

    @Suppress("UNCHECKED_CAST")
    fun activeObj(`data`: ListenerData) = active(`data` as T)

}