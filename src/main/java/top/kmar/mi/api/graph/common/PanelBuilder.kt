package top.kmar.mi.api.graph.common

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPaneClient
import top.kmar.mi.api.graph.interfaces.IPanel

/**
 * 用于双端构建[IPanel]
 *
 * @param creater 通过构建器创建对象的函数，客户端必须返回继承自[IPaneClient]的对象
 *
 * @author EmptyDreams
 */
class PanelBuilder(private val creater: (PanelBuilder) -> IPanel) {

    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var x: Int = 0
        private set
    var y: Int = 0
        private set
    var init: ((IPanel) -> Unit)? = null
        private set

    fun builder(): IPanel {
        val result = creater(this)
        init?.invoke(result)
        return result
    }

    @SideOnly(Side.CLIENT)
    fun builderClient(): IPaneClient = builder() as IPaneClient

    fun setLocation(x: Int, y: Int): PanelBuilder {
        this.x = x
        this.y = y
        return this
    }

    fun setX(value: Int): PanelBuilder {
        x = value
        return this
    }

    fun setY(value: Int): PanelBuilder {
        y = value
        return this
    }

    fun setSize(width: Int, height: Int): PanelBuilder {
        this.width = width
        this.height = height
        return this
    }

    fun setWidth(value: Int): PanelBuilder {
        width = value
        return this
    }

    fun setHeight(value: Int): PanelBuilder {
        height = value
        return this
    }

    /** 设置初始化器，在调用[builder]时会调用 */
    fun setInitFun(value: (IPanel) -> Unit) {
        init = value
    }

}