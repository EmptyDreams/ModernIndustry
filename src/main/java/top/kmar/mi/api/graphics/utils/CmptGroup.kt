package top.kmar.mi.api.graphics.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.modes.DisplayModeEnum
import top.kmar.mi.api.graphics.utils.modes.PositionEnum
import java.util.*

/**
 * 控件分组器。
 *
 * 该分组器会将控件按照控件的`display`进行自动分组，分组器的迭代器不会迭代控件中`abs`和`fixed`定位的控件。
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class CmptClientGroup(private val cmpt: Cmpt) : Iterable<CmptClientGroup.Line> {

    private var valid = false
    private lateinit var list: MutableList<Line>
    private lateinit var absolute: MutableList<CmptClient>
    private lateinit var fixed: MutableList<CmptClient>

    /** 获取最大宽度 */
    val width: Int
        get() {
            group()
            return list.stream().mapToInt {
                it.width
            }.max().orElse(0)
        }
    /** 获取高度之和 */
    val height: Int
        get() {
            group()
            return list.stream().mapToInt {
                it.height
            }.sum()
        }
    /** abs 定位的元素列表 */
    val absoluteList: List<CmptClient>
        get() {
            group()
            return absolute
        }
    /** fixed 定位的元素列表 */
    val fixedList: List<CmptClient>
        get() {
            group()
            return fixed
        }

    /** 清空分组信息 */
    fun clear() {
        valid = false
    }

    /** 获取分组 */
    private fun group() {
        if (valid) return
        list = LinkedList()
        absolute = LinkedList()
        fixed = LinkedList()
        var prevDisplay = DisplayModeEnum.NONE
        cmpt.childrenStream()
            .map { it.client }
            .forEachOrdered {
                val style = it.style
                val display = style.display
                if (!display.isDisplay()) return@forEachOrdered
                when (style.position) {
                    PositionEnum.STATIC -> {
                        if (!display.isInline() || display != prevDisplay) {
                            list.add(Line())
                            prevDisplay = display
                        }
                        list.last() += it
                    }
                    PositionEnum.ABSOLUTE -> absolute.add(it)
                    PositionEnum.FIXED -> fixed.add(it)
                }
            }
        valid = true
    }

    override fun iterator(): Iterator<Line> {
        group()
        return list.iterator()
    }

    class Line : Iterable<CmptClient> {

        private val list = LinkedList<CmptClient>()

        /** 获取宽度之和 */
        val width: Int
            get() = list.stream().mapToInt {
                it.spaceWidth
            }.sum()
        /** 获取最大高度 */
        val height: Int
            get() = list.stream().mapToInt {
                it.spaceHeight
            }.max().orElse(0)

        internal operator fun plusAssign(item: CmptClient) {
            list += item
        }

        override fun iterator(): Iterator<CmptClient> = list.iterator()

        fun flip() = Iterable {
            object : Iterator<CmptClient> {

                private val itor = list.listIterator(list.size)

                override fun hasNext(): Boolean = itor.hasPrevious()

                override fun next(): CmptClient = itor.previous()

            }
        }

    }

}