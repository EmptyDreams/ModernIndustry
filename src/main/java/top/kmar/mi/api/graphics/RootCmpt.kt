package top.kmar.mi.api.graphics

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.modes.CodeSizeMode
import java.util.*

/**
 * 根节点
 * @author EmptyDreams
 */
class DocumentCmpt(
    attributes: CmptAttributes = CmptAttributes().apply {
        id = "document"
        this["level"] = "-1"
    }
) : Cmpt(attributes) {

    @SideOnly(Side.CLIENT)
    override fun initClientObj() = DocumentCmptClient(gui!!)

    override fun installParent(parent: Cmpt, gui: BaseGraphics) {
        super.installParent(parent, gui)
        val list = LinkedList<Cmpt>()
        list.add(gui.document)
        do {
            val node = list.pop()
            node.eachAllChildren {
                if (!it.isInstallParent) {
                    it.isInstallParent = true
                    it.installParent(node, gui)
                }
                list.add(it)
            }
        } while (list.isNotEmpty())
        isInstallParent = true
    }

    override fun buildNewObj() = DocumentCmpt(attributes.copy())

}

/**
 * 客户端根节点
 * @author EmptyDreams
 */
class DocumentCmptClient(gui: BaseGraphics) : CmptClient(gui.document) {

    val client = BaseGraphicsClient(gui)

    init {
        style.width = CodeSizeMode { client.width }
        style.height = CodeSizeMode { client.height }
    }

}