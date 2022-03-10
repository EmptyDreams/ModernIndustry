package top.kmar.mi.data.info.properties

import net.minecraft.util.IStringSerializable

/**
 * 相对方向
 * @author EmptyDreams
 */
enum class RelativeDirectionEnum: IStringSerializable {

    LEFT,
    RIGHT,
    UP,
    DOWN;

    /** 顺时针旋转方向 */
    fun rotate() = when(this) {
        LEFT -> UP
        RIGHT -> DOWN
        UP -> RIGHT
        DOWN -> LEFT
    }

    /** 逆时针旋转方向 */
    fun rotateCCW() = when(this) {
        LEFT -> DOWN
        RIGHT -> UP
        UP -> LEFT
        DOWN -> RIGHT
    }

    /** 反转方向 */
    fun opposite() = when(this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
        UP -> DOWN
        DOWN -> UP
    }

    override fun getName() = name.lowercase()

}