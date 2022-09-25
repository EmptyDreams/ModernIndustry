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
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) {
            var x = cmpt.style.x
            cmpt.service.childrenStream()
                .filter { it.client.style.position == PositionEnum.RELATIVE }
                .map { it.client }
                .forEachOrdered {
                    callback(it, x)
                    x += it.style.spaceWidth
                }
        }
    },
    /** 居中对齐 */
    MIDDLE {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) {
            var width = 0
            val list = LinkedList<CmptClient>().apply {
                cmpt.service.eachAllChildren {
                    val style = it.client.style
                    if (style.position == PositionEnum.RELATIVE) {
                        add(it.client)
                        width += style.spaceWidth
                    }
                }
            }
            val style = cmpt.style
            val relativeX = (style.width - width) shr 1
            var x = style.x + relativeX
            for (it in list) {
                callback(it, x)
                x += it.style.spaceWidth
            }
        }
    },
    /** 右对齐 */
    RIGHT {
        override fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit) {
            var x = cmpt.style.endX
            val iterator = cmpt.service.childrenIterator(true)
            for (it in iterator) {
                val client = it.client
                x -= client.style.spaceWidth
                callback(client, x)
            }
        }
    };

    /**
     * 对齐指定控件内的子控件
     * @param cmpt 要进行对齐的父控件对象
     * @param callback 回调函数（子控件对象，子控件左上角相对于窗体的X轴坐标）
     */
    abstract operator fun invoke(cmpt: CmptClient, callback: (CmptClient, Int) -> Unit)

}