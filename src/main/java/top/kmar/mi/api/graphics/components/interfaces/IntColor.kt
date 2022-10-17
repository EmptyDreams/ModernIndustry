package top.kmar.mi.api.graphics.components.interfaces

import java.awt.Color
import kotlin.streams.toList

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

/**
 * 通过字符串解析出一个颜色值
 *
 * 支持以下格式：
 *
 * + `#RGB`
 * + `#RGBA`
 * + `#RrGgBb`
 * + `#RrGbBbAa`
 * + `rgb(r,g,b)`
 * + `rgba(a,g,b,a)`
 * + `rgb(r g b)`
 * + `rgba(r g b a)`
 */
fun IntColor(exp: String): IntColor {
    if (exp.startsWith('#')) {
        when (exp.length) {
            4 -> {
                val r = "${exp[1]}${exp[1]}".toInt(16)
                val g = "${exp[2]}${exp[2]}".toInt(16)
                val b = "${exp[3]}${exp[3]}".toInt(16)
                return IntColor(r, g, b)
            }
            5 -> {
                val r = "${exp[1]}${exp[1]}".toInt(16)
                val g = "${exp[2]}${exp[2]}".toInt(16)
                val b = "${exp[3]}${exp[3]}".toInt(16)
                val a = "${exp[4]}${exp[4]}".toInt(16)
                return IntColor(r, g, b, a)
            }
            7 -> return IntColor(exp.substring(1).toInt(16))
            9 -> {
                val color = exp.substring(1 until 7).toInt(16)
                val a = exp.substring(7).toInt(16)
                return IntColor((a shl 24) or color)
            }
        }
    } else {
        if (exp.startsWith("rgba")) {
            val index = exp.indexOf('(', 4)
            val rgba = exp
                .substring(index + 1 until exp.length - 1)
                .split(Regex("""(\s)|,"""))
                .stream()
                .filter { it.isNotBlank() }
                .toList()
            if (rgba.size == 4) {
                return if (rgba[3].contains('.'))
                    IntColor(
                        rgba[0].toInt(), rgba[1].toInt(), rgba[2].toInt(),
                        (rgba[3].toFloat() * 255).toInt()
                    )
                else IntColor(rgba[0].toInt(), rgba[1].toInt(), rgba[2].toInt(), rgba[3].toInt())
            }
        } else if (exp.startsWith("rgb")) {
            val index = exp.indexOf('(', 3)
            val rgb = exp
                .substring(index + 1 until exp.length - 1)
                .split(Regex("""(\s)|,"""))
                .stream()
                .filter { it.isNotBlank() }
                .toList()
            if (rgb.size == 3)
                return IntColor(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt())
        }
    }
    throw IllegalArgumentException("不合法的颜色表达式：$exp")
}