package top.kmar.mi.api.graphics.utils

/**
 * 控件尺寸
 * @author EmptyDreams
 */
interface ISizeMode {

    /** 获取尺寸 */
    operator fun invoke(): Int

}

/**
 * 固定尺寸
 * @author EmptyDreams
 */
class FixedSizeMode(
    val value: Int
) : ISizeMode {

    override fun invoke() = value

    companion object {

        val defaultValue = FixedSizeMode(0)

    }

}

/**
 * 百分比尺寸
 * @author EmptyDreams
 */
class PercentSizeMode(
    val value: Double,
    val plus: Int,
    val parentSize: () -> Int
): ISizeMode {

    override fun invoke() = (value * parentSize()).toInt() + plus

}