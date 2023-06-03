package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.CmptClientGroup
import top.kmar.mi.api.utils.expands.eachWith
import top.kmar.mi.api.utils.expands.floorDiv2
import top.kmar.mi.api.utils.expands.stream

@SideOnly(Side.CLIENT)
sealed interface IAlignMode


enum class HorizontalAlignModeEnum : IAlignMode {

    /** 左对齐 */
    LEFT {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            var x = parent.style.paddingLeft
            for (item in line) {
                item.x = x
                x += item.spaceWidth
            }
        }
    },

    /** 居中对齐 */
    MIDDLE {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            val width = line.width
            var x = (parent.contentWidth - width) / 2 + parent.style.paddingLeft
            for (item in line) {
                item.x = x
                x += item.spaceWidth
            }
        }

    },

    /** 右对齐 */
    RIGHT {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            var x = parent.contentWidth + parent.style.paddingLeft
            for (item in line.flip()) {
                x -= item.spaceWidth
                item.x = x
            }
        }

    };

    protected abstract fun typesetting(parent: CmptClient, line: CmptClientGroup.Line)

    /**
     * 排序指定列表中的控件
     * @param parent 父控件
     * @param group 要排序的控件列表
     */
    operator fun invoke(parent: CmptClient, group: CmptClientGroup) {
        group.forEach { typesetting(parent, it) }
    }

    companion object {

        fun from(name: String): HorizontalAlignModeEnum =
            when (name) {
                "left" -> LEFT
                "middle" -> MIDDLE
                "right" -> RIGHT
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}

/**
 * 垂直对齐方式
 * @author EmptyDreams
 */
enum class VerticalAlignModeEnum : IAlignMode {

    /** 靠上排列 */
    TOP {
        override fun invoke(
            parent: CmptClient, group: CmptClientGroup
        ) {
            var y = parent.style.paddingTop
            group.forEach { line ->
                line.forEach { it.y = y }
                y += line.height
            }
        }
    },

    /** 居中排列 */
    MIDDLE {
        override fun invoke(
            parent: CmptClient, group: CmptClientGroup
        ) {
            val heightList = group.stream().mapToInt {
                it.height
            }.toArray()
            val sum = heightList.sum()
            var y = (parent.contentHeight - sum) / 2 + parent.style.paddingTop
            for ((height, line) in heightList eachWith group) {
                line.forEach {
                    it.y = y + (height - it.height).floorDiv2()
                }
                y += height
            }
        }
    },

    /** 靠下排列 */
    BOTTOM {
        override fun invoke(
            parent: CmptClient, group: CmptClientGroup
        ) {
            var y = parent.contentHeight + parent.style.paddingLeft
            group.forEach { line ->
                y -= line.height
                line.forEach { it.y = y }
            }
        }
    };

    /**
     * 对齐指定控件内的子控件
     * @param parent 父控件
     * @param group 要排序的控件列表
     */
    abstract operator fun invoke(
        parent: CmptClient, group: CmptClientGroup
    )

    companion object {

        fun from(name: String): VerticalAlignModeEnum =
            when (name) {
                "top" -> TOP
                "middle" -> MIDDLE
                "bottom" -> BOTTOM
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}