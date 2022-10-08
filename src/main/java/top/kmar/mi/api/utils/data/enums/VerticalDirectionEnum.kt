package top.kmar.mi.api.utils.data.enums

/**
 * 竖直方向
 * @author EmptyDreams
 */
enum class VerticalDirectionEnum {

    UP, DOWN, CENTER;

    fun isUp() = this == UP

    fun isDown() = this == DOWN

    fun isCenter() = this == CENTER

}