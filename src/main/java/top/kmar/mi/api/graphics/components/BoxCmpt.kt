package top.kmar.mi.api.graphics.components

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.regedits.others.AutoCmpt

/**
 * 空的控件
 * @author EmptyDreams
 */
@AutoCmpt("div")
class BoxCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = BoxCmptClient()
    override fun buildNewObj() = BoxCmpt(attributes.copy())

    @SideOnly(Side.CLIENT)
    inner class BoxCmptClient : CmptClient {

        override val service = this@BoxCmpt
        override val style = GraphicsStyle(service)

    }

}