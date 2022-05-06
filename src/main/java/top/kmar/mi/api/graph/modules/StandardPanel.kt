package top.kmar.mi.api.graph.modules

import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.utils.GeneralPanel
import top.kmar.mi.api.graph.utils.PanelBuilder
import top.kmar.mi.api.utils.WorldUtil

private typealias Builder = PanelBuilder

object StandardPanel {

    @JvmStatic
    fun buildBackground(builder: Builder): IPanel =
        if (WorldUtil.isServer()) GeneralPanel()
        else BackgroundPanelClient(builder.x, builder.y, builder.width, builder.height)

    @JvmStatic
    fun buildString(builder: Builder): IPanel =
        if (WorldUtil.isServer()) GeneralPanel()
        else StringPanelClient(builder.x, builder.y, builder.width, builder.height)

}