package top.kmar.mi.api.graphics.components

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import top.kmar.mi.api.utils.MISysInfo
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
    private val childrenList = LinkedList<Cmpt>()
    /** 事件列表 */
    protected val eventMap = Object2ObjectRBTreeMap<String, LinkedList<IGraphicsListener<*>>>()
    /** 父节点 */
    var parent: Cmpt = EMPTY_CMPT

    /** 初始化客户端对象 */
    @SideOnly(Side.CLIENT)
    abstract fun initClientObj(): CmptClient

    fun hasParent(): Boolean = parent != EMPTY_CMPT

    /** 向控件添加一个子控件 */
    fun addChild(cmpt: Cmpt) {
        childrenList.add(cmpt)
        cmpt.parent = this
    }

    /** 从控件中移除一个子控件 */
    fun removeChild(cmpt: Cmpt) {
        childrenList.remove(cmpt)
        cmpt.parent = EMPTY_CMPT
    }

    /** 通过ID获取控件，不存在则返回`null` */
    fun getElementByID(id: String): Cmpt? {
        if (id == this.id) return this
        return eachChildren { it.getElementByID(id) }
    }

    /**
     * 发布事件
     *
     * 如果某一个事件执行过程中发生了异常，不会影响其它事件的执行
     *
     * 该函数不会抛出异常
     */
    fun dispatchEvent(name: String, message: ListenerData) {
        message.target = this
        /** 触发指定控件的事件 */
        fun activeEvent(it: Cmpt) {
            try {
                val listeners = it.eventMap[name] ?: return
                for (listener in listeners) {
                    listener.activeObj(message)
                    if (message.cancel) break
                }
            } catch (e: Exception) {
                MISysInfo.err("触发事件过程中发生异常：\n\tname=$name\n\tmessage=$message\n\tit=$it", e)
            }
        }
        /** 触发指定事件及其父控件的事件 */
        fun activeParentEvent(it: Cmpt) {
            if (!message.reverse) activeEvent(it)
            if (it.hasParent() && !message.cancel) activeParentEvent(it.parent)
            if (message.reverse && !message.cancel) activeEvent(it)
        }
        activeParentEvent(this)
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