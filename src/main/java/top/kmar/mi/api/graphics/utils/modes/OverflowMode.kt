package top.kmar.mi.api.graphics.utils.modes

/**
 * 组件内的内容超过 content 区域后的处理方式
 * @author EmptyDreams
 */
enum class OverflowMode {

    /** 正常显示 */
    VISIBLE,
    /** 隐藏 */
    HIDDEN,
    /** 显示滚动条 */
    SCROLL;

    /** 是否裁剪 */
    val isClip: Boolean
        get() = this != VISIBLE
    /** 是否滚动 */
    val isScroll: Boolean
        get() = this == SCROLL

    companion object {

        @JvmStatic
        fun of(name: String): OverflowMode =
            when (name) {
                "visible" -> VISIBLE
                "hidden" -> HIDDEN
                "scroll" -> SCROLL
                else -> throw IllegalArgumentException("不存在的名称：$name")
            }

    }

}