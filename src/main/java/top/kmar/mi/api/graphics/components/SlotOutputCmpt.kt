package top.kmar.mi.api.graphics.components

import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.register.others.AutoCmpt

/**
 * 仅允许输出的Slot控件
 * @author EmptyDreams
 */
@AutoCmpt("output")
class SlotOutputCmpt(attributes: CmptAttributes) : SlotCmpt(attributes) {

    override fun initClientObj() = super.initClientObj().apply {
        with(style) {
            widthCalculator = FixedSizeMode(26)
            heightCalculator = widthCalculator
        }
    }

    override fun buildNewObj() = SlotOutputCmpt(attributes.copy())

}