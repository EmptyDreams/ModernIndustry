@file:Suppress("unused")

package top.kmar.mi.api.graphics.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt

/**
 * 控件尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
interface ISizeMode {

    /** 尺寸计算是否依赖父节点 */
    val relyOnParent: Boolean
    /** 尺寸计算是否依赖子节点 */
    val relyOnChild: Boolean

    /** 获取尺寸 */
    operator fun invoke(dist: GraphicsStyle): Int

}

/**
 * 固定尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class FixedSizeMode(
    val value: Int
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = false

    /**
     * @param dist 当前节点的父节点样式表
     */
    override fun invoke(dist: GraphicsStyle) = value

}

/**
 * 百分比尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class PercentSizeMode(
    val value: Double,
    val plus: Int,
    val parentSize: (GraphicsStyle) -> Int
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: GraphicsStyle) = (value * parentSize(dist)).toInt() + plus

}

/**
 * 继承尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class InheritSizeMode(
    val parentSize: (GraphicsStyle) -> Int
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: GraphicsStyle) = parentSize(dist)

}

/**
 * 根据子节点确定该节点尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class AutoSizeMode(
    val cmpt: Cmpt,
    val getSize: (GraphicsStyle) -> Int
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = true

    override fun invoke(dist: GraphicsStyle) =
        cmpt.childrenStream().map { it.client.style }.mapToInt { getSize(it) }.sum()

}