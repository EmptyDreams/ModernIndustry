package top.kmar.mi.api.utils.data.math

import kotlin.math.max
import kotlin.math.min

data class Rect2D(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {

    val endX = x + width
    val endY = y + height
    /** 是否是一个有效矩形 */
    val isEffective = width > 0 && height > 0

    /** 求两个矩形的交集 */
    fun intersect(that: Rect2D): Rect2D {
        val x = max(this.x, that.x)
        val y = max(this.y, that.y)
        val endX = min(this.endX, that.endX)
        val endY = min(this.endY, that.endY)
        return if (x < endX && y < endY)
            Rect2D(x, y, endX - x, endY - y)
        else Rect2D(x, y, 0, 0)
    }

    operator fun contains(point: Point2D): Boolean =
        !(point.x < x || point.y < y || point.x > endX || point.y > endY)

}

data class Rect3D(
    val x: Int,
    val y: Int,
    val z: Int,
    val width: Int,
    val height: Int,
    val length: Int
) {

    val endX = x + width
    val endY = y + height
    val endZ = z + length

    operator fun contains(point: Point3D): Boolean =
        !(point.x < x || point.y < y || point.z < z || point.x > endX || point.y > endY || point.z > endZ)

}