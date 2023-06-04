package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * 控件定位方法
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
enum class PositionEnum {

    /**
     * 静态定位
     *
     * 在该定位模式下`top`、`right`、`bottom`、`left`将不可用
     *
     * 定位顺序：自动定位
     */
    STATIC,
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

    fun isStatic() = this == STATIC

    fun isAbs() = this == ABSOLUTE

    fun isFixed() = this == FIXED

    companion object {

        @JvmStatic
        fun of(name: String): PositionEnum =
            when (name) {
                "static" -> STATIC
                "absolute" -> ABSOLUTE
                "fixed" -> FIXED
                else -> throw IllegalArgumentException("指定名称[$name]不存在")
            }

    }

}