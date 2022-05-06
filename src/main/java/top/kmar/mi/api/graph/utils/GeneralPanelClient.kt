package top.kmar.mi.api.graph.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPaneClient
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.listener.IListener
import top.kmar.mi.api.graph.listener.IListenerData

/**
 * 客户端通用Panel
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
abstract class GeneralPanelClient protected constructor(
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int
) : IPaneClient {

    /** 存储事件列表 */
    private val listenerList = ListenerManager()

    override fun onAdd2Container(father: IPanelContainer) {}

    override fun onRemoveFromContainer(father: IPanelContainer) {}

    override fun registryListener(listener: IListener) {
        listenerList.registryListener(listener)
    }

    override fun removeListener(clazz: Class<out IListener>) {
        listenerList.removeListener(clazz)
    }

    override fun activeListener(clazz: Class<out IListener>, data: IListenerData) {
        listenerList(clazz, data)
    }

}