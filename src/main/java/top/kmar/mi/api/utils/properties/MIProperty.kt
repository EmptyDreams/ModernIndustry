package top.kmar.mi.api.utils.properties

import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.util.EnumFacing

/**
 * 放置常用的Property
 * @author EmptyDreams
 */
@Suppress("MemberVisibilityCanBePrivate")
class MIProperty {

    companion object {

        /** 构建一个水平方向上的property */
        fun createHorizontal(name: String = "horizontal"): PropertyDirection =
            PropertyDirection.create(name, EnumFacing.Plane.HORIZONTAL)

        /** 构建一个所有方向的property */
        fun createAllDirection(name: String = "all_facing"): PropertyDirection = PropertyDirection.create(name)

        /** 构建一个垂直方向的property */
        fun createVertical(name: String = "vertical"): PropertyDirection =
            PropertyDirection.create(name, EnumFacing.Plane.VERTICAL)

        /** 标志是否正在工作的property */
        @JvmStatic val WORKING: PropertyBool = PropertyBool.create("working")
        /** 标志是否为空的property */
        @JvmStatic val EMPTY: PropertyBool = PropertyBool.create("empty")
        /** 水平方向上的property */
        @JvmStatic val HORIZONTAL: PropertyDirection = createHorizontal()
        /** 所有方向的property */
        @JvmStatic val ALL_FACING: PropertyDirection = createAllDirection()
        /** 垂直方向的property */
        @JvmStatic val VERTICAL: PropertyDirection = createVertical()
        /** 所有axis的property */
        @JvmStatic val AXIS: PropertyAxis = PropertyAxis.createAll("axis")

    }

}