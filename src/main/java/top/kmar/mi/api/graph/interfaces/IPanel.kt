package top.kmar.mi.api.graph.interfaces

import top.kmar.mi.api.graph.listener.IListener
import top.kmar.mi.api.graph.listener.IListenerData

/**
 * 所有控件的接口
 * @author EmptyDreams
 */
interface IPanel {

    /** 判断该控件是否是客户端对象 */
    fun isClient(): Boolean = false

    /** 判断该控件是否为服务端对象 */
    fun isService(): Boolean = !isClient()

    /**
     * 当控件被添加到一个布局管理器中时触发
     *
     * 该函数内可以修改管理器的数据
     *
     * 由布局管理器保证对于每一个布局管理器该函数仅调用一次
     */
    fun onAdd2Container(father: IPanelContainer)

    /**
     * 当控件从布局管理中移除时触发
     *
     * 该函数内可以修改管理器的数据
     */
    fun onRemoveFromContainer(father: IPanelContainer)

    /** 注册指定事件 */
    fun registryListener(listener: IListener)

    /** 取消某一种事件 */
    fun removeListener(clazz: Class<out IListener>)

    /**
     * 触发指定事件
     * @param clazz 要触发的事件的class
     * @param data 要出发的事件的附加信息
     */
    fun activeListener(clazz: Class<out IListener>, data: IListenerData)

}