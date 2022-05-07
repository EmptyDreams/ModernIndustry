package top.kmar.mi.api.graph.utils.builders

import top.kmar.mi.api.graph.interfaces.IPanelClient
import top.kmar.mi.api.graph.interfaces.IPanel

/**
 * 不支持设置尺寸的构建器
 * @author EmptyDreams
 */
abstract class FixedPanelBuilder<T : IPanel> {

    var x: Int = 0
        private set
    var y: Int = 0
        private set

    /** 构建对象，客户端调用返回的对象一定从[IPanelClient]派生 */
    abstract fun build(): T

    /**
     * 构建对象并调用初始化语句
     * @see build
     */
    fun buildAndInit(init: (T) -> Unit): T {
        val result = build()
        init(result)
        return result
    }

    fun setX(value: Int): FixedPanelBuilder<T> {
        x = value
        return this
    }

    fun setY(value: Int): FixedPanelBuilder<T> {
        y = value
        return this
    }

    fun setPos(x: Int, y: Int): FixedPanelBuilder<T> {
        this.x = x
        this.y = y
        return this
    }

}