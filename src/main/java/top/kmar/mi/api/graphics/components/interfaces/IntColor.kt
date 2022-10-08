package top.kmar.mi.api.graphics.components.interfaces

import java.awt.Color

/**
 * 使用一个整型表示颜色
 * @author EmptyDreams
 */
@JvmInline
value class IntColor(val value: Int) {

    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) :
            this((alpha shl 24) or (red shl 16) or (green shl 8) or blue)

    val red: Int
        get() = (value shr 16) and 0xFF

    val green: Int
        get() = (value shr 8) and 0xFF

    val blue: Int
        get() = value and 0xFF

    val alpha: Int
        get() = (value shr 24) and 0xFF

    fun toColor() = Color(red, green, blue, alpha)

    companion object {

        @JvmStatic
        val black = IntColor(0, 0, 0)
        @JvmStatic
        val white = IntColor(255, 255, 255)
        @JvmStatic
        val gray = IntColor(139, 139, 139)
        @JvmStatic
        val shadow = IntColor(0, 0, 0, 135)
        @JvmStatic
        val transparent = IntColor(0)

    }

}