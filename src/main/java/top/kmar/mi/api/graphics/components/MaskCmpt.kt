package top.kmar.mi.api.graphics.components

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.modes.CodeSizeMode
import top.kmar.mi.api.graphics.utils.modes.PositionEnum
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.expands.clientPlayer

/**
 * 蒙版
 * @author EmptyDreams
 */
@AutoCmpt("mask")
class MaskCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = MaskClient()
    override fun buildNewObj() = MaskCmpt(attributes.copy())

    @SideOnly(Side.CLIENT)
    inner class MaskClient : CmptClient(this) {

        override fun defaultStyle() = StyleNode().apply {
            position = PositionEnum.FIXED
            backgroundColor = IntColor(0, 0, 0, 120)
            val container = (clientPlayer.openContainer as BaseGraphics).client
            width = CodeSizeMode { container.width }
            height = CodeSizeMode { container.height }
        }

    }

}