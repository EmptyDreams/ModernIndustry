package top.kmar.mi.api.graph.interfaces

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.net.message.panel.PanelAddition

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
     * @param writer 要发送到对方端的数据
     */
    fun activeListener(clazz: Class<out IListener>, `data`: IListenerData, writer: IDataWriter)

    // ---------- 网络通信 ---------- //

    /**
     * 向对方端发送数据
     *
     * **如果没有写入数据到writer请勿返回true，如果返回false请勿修改writer**
     *
     * @param writer 写入器，把要同步的数据写入到写入器中
     *
     * @return 是否需要进行同步
     */
    fun send(writer: IDataWriter): Boolean = false

    /** 接受来自对方端的数据 */
    fun receive(type: PanelAddition.Type, reader: IDataReader) {}

}