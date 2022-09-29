package top.kmar.mi.api.graphics.utils

/**
 * 显示模式
 * @author EmptyDreams
 */
enum class DisplayModeEnum {

    /** 不显示 */
    NONE,
    /** 独自一行 */
    DEF,
    /** 行内显示 */
    INLINE;

    /** 是否在一行内部显示 */
    fun isInline() = this == INLINE

    /** 是否进行绘制 */
    fun isDisplay() = this != NONE

}