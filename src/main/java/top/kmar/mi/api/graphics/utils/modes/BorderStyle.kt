package top.kmar.mi.api.graphics.utils.modes

import top.kmar.mi.api.graphics.components.interfaces.IntColor

/**
 * 边框样式
 * @author EmptyDreams
 */
data class BorderStyle(
    var color: IntColor = IntColor.transparent,
    var weight: Int = 1
)