package top.kmar.mi.data.properties

import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis

/**
 * 放置常用的Property
 * @author EmptyDreams
 */
@Suppress("MemberVisibilityCanBePrivate")
class MIProperty {

    companion object {

        /** 构建一个相对方向的property */
        fun createRelativeDirection(name: String) =
            object : PropertyEnum<RelativeDirectionEnum>(
                name, RelativeDirectionEnum::class.java, listOf(*RelativeDirectionEnum.values())
            ) {}

        /** 构建一个所有axis的property */
        fun createAxis(name: String) =
            object : PropertyEnum<Axis>(name, Axis::class.java, listOf(*Axis.values())) {}

        /** 构建一个水平方向上的property */
        fun createHorizontal(name: String = "horizontal"): PropertyDirection =
            PropertyDirection.create(name, EnumFacing.Plane.HORIZONTAL)

        /** 构建一个所有方向的property */
        fun createAllDirection(name: String = "all_facing"): PropertyDirection = PropertyDirection.create(name)

        /** 构建一个垂直方向的property */
        fun createVertical(name: String = "vertical"): PropertyDirection =
            PropertyDirection.create(name, EnumFacing.Plane.VERTICAL)

        /** 标志是否正在工作的property */
        @JvmStatic val working: PropertyBool = PropertyBool.create("working")
        /** 标志是否为空的property */
        @JvmStatic val empty: PropertyBool = PropertyBool.create("empty")
        /** 水平方向上的property */
        @JvmStatic val horizontal: PropertyDirection = createHorizontal()
        /** 所有方向的property */
        @JvmStatic val allFacing: PropertyDirection = createAllDirection()
        /** 垂直方向的property */
        @JvmStatic val vertical: PropertyDirection = createVertical()
        /** 所有axis的property */
        @JvmStatic val axis: PropertyEnum<Axis> = createAxis("axis")

    }

}