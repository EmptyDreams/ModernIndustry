package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * 显示模式
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
enum class DisplayModeEnum {

    /** 不显示 */
    NONE,
    /** 独自一行 */
    BLOCK,
    /** 行内显示 */
    INLINE;

    /** 是否在一行内部显示 */
    fun isInline() = this == INLINE

    /** 是否进行绘制 */
    fun isDisplay() = this != NONE

    companion object {

        @JvmStatic
        fun of(name: String): DisplayModeEnum = when (name) {
            "block" -> BLOCK
            "inline" -> INLINE
            "none" -> NONE
            else -> throw IllegalArgumentException("未知的名称：$name")
        }

    }

}