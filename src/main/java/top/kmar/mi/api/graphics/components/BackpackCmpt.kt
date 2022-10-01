package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.PositionEnum
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 玩家背包
 * @author EmptyDreams
 */
class BackpackCmpt(private val attribute: CmptAttributes) : Cmpt(attribute.id) {

    override fun initClientObj() = BackpackCmptClient()

    var player: EntityPlayer? = null
        set(value) {
            if (field == null) field = value
        }

    val slots by lazy(NONE) {

    }

    inner class BackpackCmptClient : CmptClient {

        override val service = this@BackpackCmpt
        override val style = GraphicsStyle(service).apply {
            position = PositionEnum.FIXED
            bottom = 10
        }

    }

}