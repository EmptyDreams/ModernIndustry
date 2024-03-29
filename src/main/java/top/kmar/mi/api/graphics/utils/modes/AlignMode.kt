package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.CmptClientGroup
import top.kmar.mi.api.utils.expands.floorDiv2
import top.kmar.mi.api.utils.expands.times2
import kotlin.math.roundToInt

@SideOnly(Side.CLIENT)
sealed interface IAlignMode

/**
 * 水平排版
 * @author EmptyDreams
 */
enum class HorizontalAlignModeEnum : IAlignMode {

    /** 居中对齐 */
    CENTER {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            val width = line.width
            var x = (parent.contentWidth - width) / 2 + parent.style.paddingLeft - parent.scrollX
            for (item in line) {
                item.x = x + item.style.marginLeft
                x += item.spaceWidth
            }
        }

    },
    /** 左对齐 */
    START {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            var x = parent.style.paddingLeft - parent.scrollX
            for (item in line) {
                item.x = x + item.style.marginLeft
                x += item.spaceWidth
            }
        }
    },
    /** 右对齐 */
    END {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            var x = parent.contentWidth + parent.style.paddingLeft - parent.scrollX
            for (item in line.flip()) {
                x -= item.spaceWidth
                item.x = x + item.style.marginLeft
            }
        }

    },
    /** 两端对齐，若只有一个元素，则将元素放置在最左侧 */
    BETWEEN {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            val amount = line.size - 1
            if (amount == 0) {
                line.forEach { it.x = parent.style.paddingLeft - parent.scrollX }
            } else {
                val interval = (parent.contentWidth - line.width) / amount.toFloat()
                var x = (parent.style.paddingLeft - parent.scrollX).toFloat()
                line.forEach {
                    it.x = x.roundToInt() + it.style.marginLeft
                    x += it.spaceWidth + interval
                }
            }
        }
    },
    /** 均匀的排列元素，每个元素之间的间隔相同，若只有一个元素，则将该元素居中 */
    EVENLY {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            val amount = line.size + 1
            val interval = (parent.contentWidth - line.width) / amount.toFloat()
            var x = parent.style.paddingLeft + interval - parent.scrollX
            line.forEach {
                it.x = x.roundToInt() + it.style.marginLeft
                x += it.spaceWidth + interval
            }
        }
    },
    /** 均匀地排列元素，为每个元素左右分配相同的间隙，若只有一个元素，则将该元素居中 */
    AROUND {
        override fun typesetting(parent: CmptClient, line: CmptClientGroup.Line) {
            val amount = line.size.times2()
            val interval = (parent.contentWidth - line.width) / amount.toFloat()
            var x = (parent.style.paddingLeft - parent.scrollX).toFloat()
            line.forEach {
                x += interval
                it.x = x.roundToInt() + it.style.marginLeft
                x += it.spaceWidth + interval
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

        @JvmStatic
        fun of(name: String): HorizontalAlignModeEnum =
            when (name) {
                "center" -> CENTER
                "start", "flex-start" -> START
                "end", "flex-end" -> END
                "space-between" -> BETWEEN
                "space-around" -> AROUND
                "space-evenly" -> EVENLY
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}

/**
 * 垂直对齐方式
 * @author EmptyDreams
 */
enum class VerticalAlignModeEnum : IAlignMode {

    /** 居中排列 */
    CENTER {
        override fun invoke(parent: CmptClient, group: CmptClientGroup) {
            var y = (parent.contentHeight - group.height) / 2 + parent.style.paddingTop - parent.scrollY
            group.forEach { line ->
                line.forEach {
                    it.y = y + (line.height - it.spaceHeight).floorDiv2() + it.style.marginTop
                }
                y += line.height
            }
        }
    },
    /** 靠上排列 */
    START {
        override fun invoke(parent: CmptClient, group: CmptClientGroup) {
            var y = parent.style.paddingTop - parent.scrollY
            group.forEach { line ->
                line.forEach { it.y = y + it.style.marginTop }
                y += line.height
            }
        }
    },
    /** 靠下排列 */
    END {
        override fun invoke(parent: CmptClient, group: CmptClientGroup) {
            var y = parent.contentHeight + parent.style.paddingLeft - parent.scrollY
            group.forEach { line ->
                y -= line.height
                line.forEach { it.y = y + it.style.marginTop }
            }
        }
    },
    /** 两端排列，若只有一行元素，则将该行元素放置在顶部 */
    BETWEEN {
        override fun invoke(parent: CmptClient, group: CmptClientGroup) {
            val amount = group.size - 1
            if (amount == 0) {
                val y = parent.style.paddingTop - parent.scrollY
                group.forEach { line -> line.forEach { it.y = y } }
            } else {
                val interval = (parent.contentHeight - group.height) / amount.toFloat()
                var y = (parent.style.paddingTop - parent.scrollY).toFloat()
                group.forEach { line ->
                    val ty = y.roundToInt()
                    line.forEach {
                        it.y = ty + (line.height - it.spaceHeight).floorDiv2() + it.style.marginTop
                    }
                    y += line.height + interval
                }
            }
        }
    },
    /** 均匀地排列行，每行之间的间隔相同，若只有一行，则将该行居中 */
    EVENLY {
        override fun invoke(parent: CmptClient, group: CmptClientGroup) {
            val amount = group.size + 1
            val interval = (parent.contentHeight - group.height) / amount.toFloat()
            var y = parent.style.paddingTop + interval - parent.scrollY
            group.forEach { line ->
                val ty = y.roundToInt()
                line.forEach {
                    it.y = ty + (line.height - it.spaceHeight).floorDiv2() + it.style.marginTop
                }
                y += line.height + interval
            }
        }
    },
    /** 均匀地排列行，为每行上下分配相同的空隙，若只有一行，则将该行居中 */
    AROUND {
        override fun invoke(parent: CmptClient, group: CmptClientGroup) {
            val amount = group.size.times2()
            val interval = (parent.contentHeight - group.height) / amount.toFloat()
            var y = (parent.style.paddingTop - parent.scrollY).toFloat()
            group.forEach { line ->
                y += interval
                val ty = y.roundToInt()
                line.forEach {
                    it.y = ty + (line.height - it.spaceHeight).floorDiv2() + it.style.marginTop
                }
                y += line.height + interval
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

        @JvmStatic
        fun of(name: String): VerticalAlignModeEnum =
            when (name) {
                "center" -> CENTER
                "start", "flex-start" -> START
                "end", "flex-end" -> END
                "space-between" -> BETWEEN
                "space-around" -> AROUND
                "space-evenly" -> EVENLY
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}