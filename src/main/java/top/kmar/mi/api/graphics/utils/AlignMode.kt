package top.kmar.mi.api.graphics.utils

import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import java.util.*

/**
 * 水平对齐方式
 * @author EmptyDreams
 */
enum class HorizontalAlignModeEnum {

    /** 左对齐 */
    LEFT {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) =
            sortLeftOrTop(cmpt, cmpt.style.x, { it.spaceWidth }, callback)
    },
    /** 居中对齐 */
    MIDDLE {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) =
            sortMiddle(cmpt, cmpt.style.x, { it.spaceWidth }, { it.width }, callback)
    },
    /** 右对齐 */
    RIGHT {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) =
            sortRightOrBottom(cmpt, cmpt.style.endX, { it.spaceWidth }, callback)
    };

    /**
     * 对齐指定控件内的子控件
     * @param cmpt 要进行对齐的父控件对象
     * @param callback 回调函数（子控件对象，子控件左上角相对于窗体的X轴坐标）
     */
    abstract operator fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit)

}

/**
 *
 * @author EmptyDreams
 */
enum class VerticalAlignModeEnum {

    /** 靠上排列 */
    TOP {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) =
            sortLeftOrTop(cmpt, cmpt.style.y, { it.spaceHeight }, callback)
    },
    /** 居中排列 */
    MIDDLE {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) =
            sortMiddle(cmpt, cmpt.style.y, { it.spaceHeight }, {it.height}, callback)
    },
    /** 靠下排列 */
    BOTTOM {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) =
            sortRightOrBottom(cmpt, cmpt.style.endY, { it.spaceHeight }, callback)
    };

    /**
     * 对齐指定控件内的子控件
     * @param cmpt 要进行对齐的父控件对象
     * @param callback 回调函数（子控件对象，子控件左上角相对于窗体的X轴坐标）
     */
    abstract operator fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit)

}

/**
 * 左侧或上方排序
 * @param cmpt 父级控件
 * @param base 基准坐标
 * @param sizeGetter 获取控件大小
 * @param callback 回调
 */
private fun sortLeftOrTop(
    cmpt: CmptClient,
    base: Int,
    sizeGetter: (GraphicsStyle) -> Int,
    callback: (CmptClient, Int) -> Unit
) {
    var pos = base
    cmpt.service.childrenStream()
        .map { it.client }
        .filter {
            it.style.posChange = true
            it.style.position == PositionEnum.RELATIVE
        }
        .forEachOrdered {
            callback(it, pos)
            pos += sizeGetter(it.style)
        }
}

/**
 * 居中排序
 * @see sortLeftOrTop
 */
private fun sortMiddle(
    cmpt: CmptClient,
    base: Int,
    sizeGetter: (GraphicsStyle) -> Int,
    parentSizeGetter: (GraphicsStyle) -> Int,
    callback: (CmptClient, Int) -> Unit
) {
    var size = 0
    val list = LinkedList<CmptClient>().apply {
        cmpt.service.eachAllChildren {
            val style = it.client.style
            style.posChange = true
            if (style.position == PositionEnum.RELATIVE) {
                add(it.client)
                size += sizeGetter(style)
            }
        }
    }
    val relative = (parentSizeGetter(cmpt.style) - size) shr 1
    var pos = base + relative
    for (it in list) {
        callback(it, pos)
        pos += sizeGetter(it.style)
    }
}

/**
 * 右侧或下方排列
 * @see sortLeftOrTop
 */
private fun sortRightOrBottom(
    cmpt: CmptClient,
    base: Int,
    sizeGetter: (GraphicsStyle) -> Int,
    callback: (CmptClient, Int) -> Unit
) {
    var pos = base
    val iterator = cmpt.service.childrenIterator(true)
    for (it in iterator) {
        val client = it.client
        val style = client.style
        style.posChange = true
        if (style.position == PositionEnum.RELATIVE) {
            pos -= sizeGetter(style)
            callback(client, pos)
        }
    }
}