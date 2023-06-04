package top.kmar.mi.api.utils.data.enums

/**
 * 二维方向
 * @author EmptyDreams
 */
enum class Direction2DEnum {

    UP,
    DOWN,
    LEFT,
    RIGHT;

    /** 获取与该方向相反的方向 */
    fun opposite() = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    fun isRight() = this == RIGHT

    fun isLeft() = this == LEFT

    fun isDown() = this == DOWN

    fun isUp() = this == UP

    fun isVertical() = this == UP || this == DOWN

    fun isHorizontal() = !isVertical()

    companion object {

        @JvmStatic
        fun of(name: String): Direction2DEnum =
            when (name) {
                "top", "up" -> UP
                "right" -> RIGHT
                "bottom", "down" -> DOWN
                "left" -> LEFT
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}