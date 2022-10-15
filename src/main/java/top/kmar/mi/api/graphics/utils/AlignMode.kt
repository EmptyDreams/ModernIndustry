@file:Suppress("unused")

package top.kmar.mi.api.graphics.utils

import top.kmar.mi.api.utils.floorDiv2
import java.util.*

/**
 * 水平对齐方式
 * @author EmptyDreams
 */
enum class HorizontalAlignModeEnum {

    /** 左对齐 */
    LEFT {
        override fun invoke(
            parentStyle: GraphicsStyle, list: LinkedList<GraphicsStyle>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            callbackHelper(parentStyle.x + parentStyle.paddingLeft, list, callback)
        }
    },
    /** 居中对齐 */
    MIDDLE {
        override fun invoke(
            parentStyle: GraphicsStyle, list: LinkedList<GraphicsStyle>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            val size = list.stream().mapToInt {
                it.spaceWidth
            }.sum()
            val base = parentStyle.x + parentStyle.paddingLeft +
                            (parentStyle.contentWidth - size).floorDiv2()
            callbackHelper(base, list, callback)
        }

    },
    /** 右对齐 */
    RIGHT {
        override fun invoke(
            parentStyle: GraphicsStyle, list: LinkedList<GraphicsStyle>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            var pos = parentStyle.endX - parentStyle.paddingRight
            for (style in list.descendingIterator()) {
                pos -= style.spaceWidth
                callback(style, pos)
            }
        }

    };

    /**
     * 排序指定列表中的控件
     * @param parentStyle 父控件样式表
     * @param list 要排序的控件列表
     * @param callback 为指定控件设置X轴坐标
     */
    abstract operator fun invoke(
        parentStyle: GraphicsStyle, list: LinkedList<GraphicsStyle>,
        callback: (GraphicsStyle, Int) -> Unit
    )

    companion object {

        private fun callbackHelper(
            base: Int, list: Collection<GraphicsStyle>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            var pos = base
            for (style in list) {
                callback(style, pos)
                pos += style.spaceWidth
            }
        }

    }

}

/**
 * 垂直对齐方式
 * @author EmptyDreams
 */
enum class VerticalAlignModeEnum {

    /** 靠上排列 */
    TOP {
        override fun invoke(
            parentStyle: GraphicsStyle,
            list: LinkedList<out Collection<GraphicsStyle>>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            val heightList = list.stream().mapToInt { style ->
                style.stream().mapToInt { it.spaceHeight }.max().orElse(0)
            }.toArray()
            callbackHelper(parentStyle.y + parentStyle.paddingTop, list, heightList, callback)
        }
    },
    /** 居中排列 */
    MIDDLE {
        override fun invoke(
            parentStyle: GraphicsStyle,
            list: LinkedList<out Collection<GraphicsStyle>>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            val heightList = list.stream().mapToInt { style ->
                style.stream().mapToInt { it.spaceHeight }.max().orElse(0)
            }.toArray()
            val size = heightList.sum()
            val base = parentStyle.y + parentStyle.paddingTop +
                            (parentStyle.contentHeight - size).floorDiv2()
            callbackHelper(base, list, heightList, callback)
        }
    },
    /** 靠下排列 */
    BOTTOM {
        override fun invoke(
            parentStyle: GraphicsStyle,
            list: LinkedList<out Collection<GraphicsStyle>>,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            var pos = parentStyle.endY - parentStyle.paddingRight
            for (collection in list.descendingIterator()) {
                pos -= collection.stream().mapToInt { it.spaceHeight }.max().orElse(0)
                collection.forEach {
                    callback(it, pos)
                    it.markYChange()
                }
            }
        }
    };

    /**
     * 对齐指定控件内的子控件
     * @param parentStyle 父控件样式表
     * @param list 要排序的控件列表
     * @param callback 回调函数（子控件对象，子控件左上角相对于窗体的X轴坐标）
     */
    abstract operator fun invoke(
        parentStyle: GraphicsStyle,
        list: LinkedList<out Collection<GraphicsStyle>>,
        callback: (GraphicsStyle, Int) -> Unit
    )

    companion object {

        private fun callbackHelper(
            base: Int,
            list: LinkedList<out Collection<GraphicsStyle>>,
            heightList: IntArray,
            callback: (GraphicsStyle, Int) -> Unit
        ) {
            var pos = base
            for ((index, collection) in list.withIndex()) {
                val height = heightList[index]
                collection.forEach {
                    callback(it, pos + (height - it.spaceHeight).floorDiv2())
                    it.markYChange()
                }
                pos += height
            }
        }

    }

}