package top.kmar.mi.api.graphics.components

import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.InheritSizeMode
import top.kmar.mi.api.graphics.utils.PositionEnum
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.WorldUtil
import java.awt.Color

/**
 * 蒙版
 * @author EmptyDreams
 */
@AutoCmpt("mask")
class MaskCmpt(attributes: CmptAttributes) : Cmpt(attributes.id) {

    override fun initClientObj() = MaskClient()

    inner class MaskClient : CmptClient {

        override val service = this@MaskCmpt
        override val style = GraphicsStyle(service).apply {
            backgroundColor = Color(0, 0, 0, 120)
            position = PositionEnum.FIXED
            val container = (WorldUtil.getClientPlayer().openContainer as BaseGraphics).client
            width = InheritSizeMode { container.width }
            height = InheritSizeMode { container.height }
        }

    }

}