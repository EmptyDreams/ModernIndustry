package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.slots.IGraphicsSlot
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import top.kmar.mi.api.net.handler.MessageSender
import top.kmar.mi.api.net.message.graphics.GraphicsAddition
import top.kmar.mi.api.net.message.graphics.GraphicsMessage
import top.kmar.mi.api.utils.MISysInfo
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 控件的服务端接口
 *
 * 子类必须包含一个共有的接收一个[CmptAttributes]的构造函数
 *
 * @author EmptyDreams
 */
abstract class Cmpt(
    /** 控件ID，整个GUI中ID不能重复 */
    val id: String
) {

    /** 客户端对象，一个服务端对象对应且仅对应一个客户端对象 */
    @get:SideOnly(Side.CLIENT)
    val client by lazy(NONE) { initClientObj() }
    /** 子控件列表 */
    private val childrenList = LinkedList<Cmpt>()
    /** 事件列表 */
    private val eventMap = Object2ObjectRBTreeMap<String, LinkedList<IGraphicsListener<*>>>()
    /** 父节点 */
    var parent: Cmpt = EMPTY_CMPT
        private set(value) {
            if (value != field && isInstallParent) uninstallParent(field)
            field = value
        }
    /** 是否安装过父节点 */
    var isInstallParent = false
    /** 类名列表，内部元素不重复 */
    val classList = ObjectRBTreeSet<String>()

    /** 初始化客户端对象 */
    @SideOnly(Side.CLIENT)
    abstract fun initClientObj(): CmptClient

    /** 接收从客户端发送的信息 */
    open fun receive(message: IDataReader) {}

    /** 发送信息到客户端 */
    fun send2Client(player: EntityPlayer,  message: IDataReader) {
        val pack = GraphicsMessage.create(message, GraphicsAddition(id))
        MessageSender.send2Client(player as EntityPlayerMP, pack)
    }

    /** 网络通信接口，每Tick调用一次 */
    open fun networkEvent(player: EntityPlayer) {}

    /** 判断该控件是否有父节点 */
    fun hasParent(): Boolean = parent != EMPTY_CMPT

    /** 初始化父节点信息 */
    internal open fun installParent(parent: Cmpt) {}

    /** 移除父节点信息 */
    protected open fun uninstallParent(oldParent: Cmpt) {}

    /**
     * 添加一个Slot
     * @return Slot在GUI中的下标
     */
    protected open fun installSlot(slot: IGraphicsSlot): Int = parent.installSlot(slot)

    /** 移除一个Slot */
    protected open fun uninstallSlot(slot: IGraphicsSlot): Unit = parent.uninstallSlot(slot)

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

    /** 通过class名称获取控件列表 */
    fun getElementsByClass(clazz: String): LinkedList<Cmpt> {
        val list = LinkedList<Cmpt>()
        if (clazz in classList) list.add(this)
        eachAllChildren { list.addAll(it.getElementsByClass(clazz)) }
        return list
    }

    /**
     * 通过匹配表达式获取所有与之匹配的控件
     *
     * @param exp 匹配表达式
     *
     *   格式：
     *
     *   + `#id` - 通过id匹配
     *   + `.className` - 通过类名匹配
     *   + `tagName` - 通过标签名匹配
     *   + `tagName.className1.className2` - 匹配同时满足相连条件的控件
     *   + `.className1 .className2` - 匹配在`1`控件内的`2`控件
     * @param limit 数量限制
     * @return 所有匹配的控件（按控件出现顺序排序）
     */
    fun queryCmptLimit(exp: String, limit: Int): LinkedList<Cmpt> {
        val list = LinkedList<Cmpt>()
        if (limit == 0) return list
        val index = exp.indexOf(" ")
        val value = if (index == -1) exp else exp.substring(0 until index)
        val cmptExp = CmptSearchExp(value)
        val isEnd = index == -1
        val nextExp by lazy(NONE) { exp.substring(index + 1) }
        eachAllChildren {
            if (cmptExp.match(it)) {
                if (isEnd) list.add(it)
                else list.addAll(it.queryCmptLimit(nextExp, limit - list.size))
            }
            list.addAll(it.queryCmptLimit(exp, limit - list.size))
        }
        return list
    }

    /**
     * 通过匹配表达式匹配GUI中所有与该表达式相匹配的控件
     * @see queryCmptLimit
     */
    fun queryCmptAll(exp: String): LinkedList<Cmpt> = queryCmptLimit(exp, Int.MAX_VALUE)

    /**
     * 通过匹配表达式匹配GUI中第一个与该表达式相匹配的控件
     * @return 未查询到则返回`null`
     * @see queryCmptLimit
     */
    fun queryCmpt(exp: String): Cmpt? {
        val result = queryCmptLimit(exp, 1)
        return if (result.isEmpty()) null else result.first
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
        if (message.prohibitTransfer) activeEvent(this)
        else activeParentEvent(this)
    }

    /** 注册事件 */
    fun addEventListener(name: String, listener: IGraphicsListener<*>) {
        eventMap.computeIfAbsent(name) { LinkedList() }.add(listener)
    }

    /** 删除一个事件 */
    fun removeEventListener(name: String, listener: IGraphicsListener<*>) {
        eventMap[name]?.remove(listener)
    }

    /** 获取子控件的迭代器 */
    fun childrenIterator(reverse: Boolean = false): MutableIterator<Cmpt> =
        if (reverse) childrenList.descendingIterator() else childrenList.iterator()

    /** 包含所有子控件的流 */
    fun childrenStream() = childrenList.stream()

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
        val EMPTY_CMPT = object : Cmpt("null") {

            override fun initClientObj(): CmptClient {
                throw UnsupportedOperationException()
            }

            override fun installSlot(slot: IGraphicsSlot): Int {
                throw NullPointerException("该元素不包含父节点")
            }

        }

    }

}