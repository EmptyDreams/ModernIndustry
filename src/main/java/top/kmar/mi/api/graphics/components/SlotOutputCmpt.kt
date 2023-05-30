package top.kmar.mi.api.graphics.components

import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.utils.modes.FixedSizeMode
import top.kmar.mi.api.regedits.others.AutoCmpt

/**
 * 仅允许输出的Slot控件
 * @author EmptyDreams
 */
@AutoCmpt("output")
class SlotOutputCmpt(attributes: CmptAttributes) : SlotCmpt(attributes) {

    init {
        slotAttributes.forbidInput = true
    }

    override fun initClientObj() = SlotOutputCmptClient()

    override fun buildNewObj() = SlotOutputCmpt(attributes.copy())

    inner class SlotOutputCmptClient : SlotCmptClient() {

        override fun defaultStyle() = super.defaultStyle().apply {
            width = sizeMode
            height = sizeMode
        }

    }

    companion object {

        @JvmStatic
        private val sizeMode = FixedSizeMode(26)

    }

}