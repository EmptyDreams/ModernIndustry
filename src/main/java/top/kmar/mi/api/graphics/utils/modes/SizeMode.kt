package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.utils.interfaces.Obj2IntFunction
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
    operator fun invoke(dist: CmptClient): Int

    companion object {

        /** 查找第一个不依赖子节点的父亲节点 */
        @JvmStatic
        fun findParent(dist: CmptClient): CmptClient {
            var parent = dist.parent
            while (parent.style.width.relyOnChild)
                parent = parent.parent
            return parent
        }

    }

}

/**
 * 固定尺寸
 * @author EmptyDreams
 */
class FixedSizeMode(
    val value: Int
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = false

    /**
     * @param dist 当前节点的父节点样式表
     */
    override fun invoke(dist: CmptClient) = value

}

/**
 * 百分比尺寸
 * @author EmptyDreams
 */
class PercentSizeMode(
    val value: Double,
    val plus: Int,
    val isHeight: Boolean
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: CmptClient): Int {
        val parent = ISizeMode.findParent(dist)
        val parentSize = if (isHeight) parent.contentHeight else parent.contentWidth
        return (value * parentSize).roundToInt() + plus
    }

}

/**
 * 相对尺寸
 * @author EmptyDreams
 */
class RelativeSizeMode(
    val plus: Int,
    val isHeight: Boolean
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: CmptClient): Int {
        val parent = ISizeMode.findParent(dist)
        val parentSize = if (isHeight) parent.contentHeight else parent.contentWidth
        return parentSize + plus
    }

}

/**
 * 继承尺寸
 * @author EmptyDreams
 */
class InheritSizeMode private constructor(
    val isHeight: Boolean
): ISizeMode {

    override val relyOnParent = true
    override val relyOnChild = false

    override fun invoke(dist: CmptClient): Int {
        val parent = ISizeMode.findParent(dist)
        return if (isHeight) parent.contentHeight else parent.contentWidth
    }

    companion object {

        @JvmStatic
        val height = InheritSizeMode(true)
        @JvmStatic
        val width = InheritSizeMode(false)

        @JvmStatic
        fun instance(isHeight: Boolean) =
            if (isHeight) height else width

    }

}

/**
 * 根据子节点确定该节点尺寸
 * @author EmptyDreams
 */
class AutoSizeMode private constructor(
    val isHeight: Boolean
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = true

    override fun invoke(dist: CmptClient): Int {
        val style = dist.style
        return if (isHeight)
            dist.group.height + style.paddingTop + style.paddingBottom
        else
            dist.group.width + style.paddingLeft + style.paddingRight
    }

    companion object {

        @JvmStatic
        val height = AutoSizeMode(true)
        @JvmStatic
        val width = AutoSizeMode(false)

        @JvmStatic
        fun instance(isHeight: Boolean) =
            if (isHeight) height else width

    }

}

/** 由代码计算控件尺寸，不得依赖其它控件的尺寸 */
class CodeSizeMode(
    val calculator: Obj2IntFunction<CmptClient>
) : ISizeMode {

    override val relyOnParent = false
    override val relyOnChild = false

    override fun invoke(dist: CmptClient) = calculator(dist)

}