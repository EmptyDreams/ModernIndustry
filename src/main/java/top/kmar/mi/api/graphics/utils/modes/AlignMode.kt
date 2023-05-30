package top.kmar.mi.api.graphics.utils.modes

import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.utils.expands.eachWith
import top.kmar.mi.api.utils.expands.flip

sealed interface IAlignMode

/**
 * 水平对齐方式
 * @author EmptyDreams
 */
enum class HorizontalAlignModeEnum : IAlignMode {

    /** 左对齐 */
    LEFT {
        override fun typesetting(parent: CmptClient, list: List<CmptClient>) {
            var x = 0
            for (item in list) {
                item.x = x
                x += item.spaceWidth
            }
        }
    },

    /** 居中对齐 */
    MIDDLE {
        override fun typesetting(parent: CmptClient, list: List<CmptClient>) {
            var width = 0
            list.forEach { width += it.spaceWidth }
            var x = (parent.contentWidth - width) / 2
            for (item in list) {
                item.x = x
                x += item.spaceWidth
            }
        }

    },

    /** 右对齐 */
    RIGHT {
        override fun typesetting(parent: CmptClient, list: List<CmptClient>) {
            var x = parent.contentWidth
            for (item in list.flip()) {
                x -= item.spaceWidth
                item.x = x
            }
        }

    };

    protected abstract fun typesetting(parent: CmptClient, list: List<CmptClient>)

    /**
     * 排序指定列表中的控件
     * @param parent 父控件
     * @param list 要排序的控件列表
     */
    operator fun invoke(parent: CmptClient, list: List<List<CmptClient>>) {
        list.forEach { typesetting(parent, it) }
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
            parent: CmptClient, list: List<Collection<CmptClient>>
        ) {
            val heightList = list.stream().mapToInt { line ->
                line.stream().mapToInt { it.spaceHeight }.max().orElse(0)
            }.toArray()
            var y = 0
            for ((height, line) in heightList eachWith list) {
                line.forEach { it.y = y }
                y += height
            }
        }
    },

    /** 居中排列 */
    MIDDLE {
        override fun invoke(
            parent: CmptClient, list: List<Collection<CmptClient>>
        ) {
            val heightList = list.stream().mapToInt { line ->
                line.stream().mapToInt { it.spaceHeight }.max().orElse(0)
            }.toArray()
            val sum = heightList.sum()
            var y = (parent.contentHeight - sum) / 2
            for ((height, line) in heightList eachWith list) {
                line.forEach { it.y = y }
                y += height
            }
        }
    },

    /** 靠下排列 */
    BOTTOM {
        override fun invoke(
            parent: CmptClient, list: List<Collection<CmptClient>>
        ) {
            val heightList = list.stream().mapToInt { line ->
                line.stream().mapToInt { it.spaceHeight }.max().orElse(0)
            }.toArray()
            var y = parent.contentHeight
            for ((height, line) in heightList eachWith list) {
                y -= height
                line.forEach { it.y = y }
            }
        }
    };

    /**
     * 对齐指定控件内的子控件
     * @param parent 父控件
     * @param list 要排序的控件列表
     */
    abstract operator fun invoke(
        parent: CmptClient, list: List<Collection<CmptClient>>
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