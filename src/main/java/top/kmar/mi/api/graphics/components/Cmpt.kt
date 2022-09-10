package top.kmar.mi.api.graphics.components

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import java.util.*

/**
 * 控件的服务端接口
 * @author EmptyDreams
 */
abstract class Cmpt {

    /** 客户端对象，一个服务端对象对应且仅对应一个客户端对象 */
    @get:SideOnly(Side.CLIENT)
    val client by lazy(LazyThreadSafetyMode.NONE) { initClientObj() }
    /** 控件ID，整个GUI中ID不能重复 */
    abstract val id: String
    /** 子控件列表 */
    protected val childrenList = LinkedList<Cmpt>()
    /** 事件列表 */
    protected val eventMap = Object2ObjectRBTreeMap<String, LinkedList<IGraphicsListener<*>>>()

    /** 初始化客户端对象 */
    @SideOnly(Side.CLIENT)
    abstract fun initClientObj(): CmptClient

    /** 向控件添加一个子控件 */
    open fun addChild(cmpt: Cmpt) {
        childrenList.add(cmpt)
    }

    /**
     * 从控件中移除一个子控件
     * @return 是否移除成功
     */
    open fun removeChild(cmpt: Cmpt): Boolean = childrenList.remove(cmpt)

    /** 通过ID获取控件，不存在则返回`null` */
    fun getElementByID(id: String): Cmpt? {
        if (id == this.id) return this
        return eachChildren { it.getElementByID(id) }
    }

    /**
     * 发布事件
     *
     * 如果某一个事件执行过程中发生了异常，则所有事件的执行都将被阻断
     */
    fun dispatchEvent(name: String, message: ListenerData) {
        val listeners = eventMap[name] ?: return
        for (listener in listeners) {
            listener.activeObj(message)
            if (message.cancel) return
        }
        val transfer = message.transfer ?: return
        eachChildren {
            val newMessage = transfer(it)
            it.dispatchEvent(name, newMessage)
            message.cancel = newMessage.cancel
            if (newMessage.cancel) it else null
        }
    }

    /** 注册事件 */
    fun addEventListener(name: String, listener: IGraphicsListener<*>) {
        eventMap.computeIfAbsent(name) { LinkedList() }.add(listener)
    }

    /** 删除一个事件 */
    fun removeEventListener(name: String, listener: IGraphicsListener<*>) {
        eventMap[name]?.remove(listener)
    }

    /**
     * 遍历子控件
     * @param function 返回非`null`值时会使循环退出
     * @return 返回`consumer`的结果
     */
    fun eachChildren(function: (Cmpt) -> Cmpt?): Cmpt? {
        for (cmpt in childrenList) {
            return function(cmpt) ?: continue
        }
        return null
    }

    /** 遍历所有子控件 */
    fun eachAllChildren(consumer: (Cmpt) -> Unit) = childrenList.forEach(consumer)

    companion object {

        /** 无效的控件对象 */
        val EMPTY_CMPT = object : Cmpt() {
            override val id: String
                get() = throw UnsupportedOperationException()

            override fun initClientObj(): CmptClient {
                throw UnsupportedOperationException()
            }

        }

    }

}