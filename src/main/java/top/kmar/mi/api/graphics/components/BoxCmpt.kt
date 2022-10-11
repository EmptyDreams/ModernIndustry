package top.kmar.mi.api.graphics.components

import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.InheritSizeMode
import top.kmar.mi.api.register.others.AutoCmpt

/**
 * 空的控件
 * @author EmptyDreams
 */
@AutoCmpt("div")
class BoxCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = BoxCmptClient()
    override fun buildNewObj() = BoxCmpt(attributes.copy())

    inner class BoxCmptClient : CmptClient {

        override val service = this@BoxCmpt
        override val style = GraphicsStyle(service).apply {
            width = InheritSizeMode { parent.client.style.width() }
            height = InheritSizeMode { parent.client.style.height() }
        }

    }

}