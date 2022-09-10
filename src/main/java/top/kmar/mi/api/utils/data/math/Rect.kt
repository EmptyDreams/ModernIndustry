package top.kmar.mi.api.utils.data.math

data class Rect2D(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {

    val endX = x + width
    val endY = y + height

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