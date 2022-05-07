package top.kmar.mi.api.graph.utils.builders

import top.kmar.mi.api.graph.interfaces.IPanel

/**
 * [IPanel]的构造器
 * @author EmptyDreams
 */
abstract class GeneralPanelBuilder<T : IPanel> : FixedPanelBuilder<T>() {

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    fun setWidth(value: Int): GeneralPanelBuilder<T> {
        width = value
        return this
    }

    fun setHeight(value: Int): GeneralPanelBuilder<T> {
        height = value
        return this
    }

    fun setSize(width: Int, height: Int): GeneralPanelBuilder<T> {
        this.width = width
        this.height = height
        return this
    }

}