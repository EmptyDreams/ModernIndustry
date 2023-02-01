package top.kmar.mi.api.graphics.utils.modes

import top.kmar.mi.api.utils.expands.equalsIgnoreCase

/**
 * 控件定位方法
 * @author EmptyDreams
 */
enum class PositionEnum {

    /**
     * 相对定位
     *
     * 可通过`top`、`right`、`bottom`、`left`参数基于当前位置移动控件
     *
     * 定位顺序：自动定位 -> 参数调整
     */
    RELATIVE,
    /**
     * 绝对定位
     *
     * 可通过`top`、`right`、`bottom`、`left`参数调整控件相对于父级控件的位置
     *
     * 定位顺序：参数调整
     */
    ABSOLUTE,
    /**
     * 固定定位
     *
     * 可通过`top`、`right`、`bottom`、`left`参数调整控件相对于窗体的位置
     *
     * 定位顺序：参数调整
     */
    FIXED;

    fun isRelative() = this == RELATIVE

    fun isAbs() = this == ABSOLUTE

    fun isFixed() = this == FIXED

    companion object {

        @JvmStatic
        fun of(name: String): PositionEnum {
            for (value in values()) {
                if (value.name.equalsIgnoreCase(name)) return value
            }
            throw IllegalArgumentException("指定名称[$name]不存在")
        }

    }

}