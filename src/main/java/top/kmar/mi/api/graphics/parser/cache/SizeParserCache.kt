package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.ISizeMode
import top.kmar.mi.api.graphics.utils.PercentSizeMode
import top.kmar.mi.api.utils.removeAllSpace

/**
 * 尺寸表达式
 *
 * 支持以下格式：
 *
 * + `[ size ] = num`
 * + `[ size ] = percent%`
 * + `[ size ] = calc(percent% +/- num)`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class SizeParserCache(exp: String, isHeight: Boolean) : IParserCache {

    private val task: (ISizeMode) -> ISizeMode = exp.removeAllSpace().run {
        // 通过exp解析出一个生成ISideMode的函数
        // 通过此举避免每次生成ISizeMode时都重新解析一遍字符串
        if (endsWith('%')) {  // 如果字符串格式为“num%”
            val percent = substring(0 until length - 1).toDouble() * 0.01
            return@run { PercentSizeMode(percent, 0) { style -> it(style) } }
        } else if (this[0].isDigit()) {    // 如果字符串格式为“num”
            return@run { FixedSizeMode(toInt()) }
        } else {    // 如果字符串格式为“calc(num%[+-]num)”
            // 截取括号中间的部分
            val mid = substring(5 until length - 1)
            // 如果没有写+-号就直接返回，即格式为：“calc(num%)”
            if (mid.endsWith('%'))
                return@run { PercentSizeMode(mid.toDouble() * 0.01, 0) { style -> it(style) } }
            val index = mid.indexOfFirst { it == '-' || it == '+' }
            val percent = mid.substring(0 until index - 1).toDouble() * 0.01    // 获取百分号左边的数字
            val plus = mid.substring(index).toInt() // 获取百分号右边的数字
            return@run { PercentSizeMode(percent, plus) { style -> it(style) } }
        }
    }

    private val setter: (GraphicsStyle) -> Unit = task.run {
        if (isHeight) {
            return@run { it.heightCalculator = this(it.parentStyle.heightCalculator) }
        } else {
            return@run { it.widthCalculator = this(it.parentStyle.widthCalculator) }
        }
    }

    override fun invoke(style: GraphicsStyle) {
        setter(style)
    }

}