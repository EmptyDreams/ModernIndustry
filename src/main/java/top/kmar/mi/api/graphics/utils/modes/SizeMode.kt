@file:Suppress("unused")

package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.utils.interfaces.Obj2IntFunction
import java.util.function.IntSupplier
import kotlin.math.roundToInt

/**
 * 控件尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
sealed interface ISizeMode {

    /** 尺寸计算是否依赖父节点 */
    val relyOnParent: Boolean
    /** 尺寸计算是否依赖子节点 */
    val relyOnChild: Boolean

    /**
     * 获取尺寸
     * @param dist 当前节点对象
     */
    operator fun invoke(dist: Cmpt): Int

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
    override fun invoke(dist: Cmpt) = value

}

/**
 * 百分比尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class PercentSizeMode(
    val value: Double,
    val plus: Int,
    val parentSize: Obj2IntFunction<GraphicsStyle>
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: Cmpt) = (value * parentSize(dist.parent.client.style)).roundToInt() + plus

}

/**
 * 相对尺寸
 * @author EmptyDreams
 */
class RelativeSizeMode(
    val plus: Int,
    val parentSize: Obj2IntFunction<GraphicsStyle>
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: Cmpt) = parentSize(dist.parent.client.style) + plus

}

/**
 * 继承尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class InheritSizeMode(
    val parentSize: Obj2IntFunction<GraphicsStyle>
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: Cmpt) = parentSize(dist.parent.client.style)

}

/**
 * 根据子节点确定该节点尺寸
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class AutoSizeMode(
    val isHeight: Boolean
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = true

    override fun invoke(dist: Cmpt): Int = dist.client.style.run {
        if (isHeight) childrenHeight else childrenWidth
    }

}

/** 由代码计算控件尺寸，不得依赖其它控件的尺寸 */
class CodeSizeMode(
    val calculator: IntSupplier
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = false

    override fun invoke(dist: Cmpt) = calculator.asInt

}