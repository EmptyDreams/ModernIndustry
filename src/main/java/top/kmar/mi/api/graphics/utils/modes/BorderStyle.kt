package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.IntColor

/**
 * 边框样式
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
data class BorderStyle(
    var color: IntColor = IntColor.transparent,
    var weight: Int = 1
)