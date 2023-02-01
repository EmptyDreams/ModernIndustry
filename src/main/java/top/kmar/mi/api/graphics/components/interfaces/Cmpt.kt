package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.utils.exps.ComplexCmptExp
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.ListenerData
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.exps.ICmptExp
import top.kmar.mi.api.net.messages.GraphicsMessage
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.expands.isClient
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
    /** 属性列表 */
    val attributes: CmptAttributes
) {

    /** 客户端对象，一个服务端对象对应且仅对应一个客户端对象 */
    @get:SideOnly(Side.CLIENT)
    val client by lazy(NONE) { initClientObj() }
    /** 控件ID，整个GUI中ID不能重复 */
    val id: String
        get() = attributes.id
    /** 子控件列表 */
    private val childrenList = LinkedList<Cmpt>()
    /** 事件列表 */
    private val eventMap = Object2ObjectRBTreeMap<String, LinkedList<IGraphicsListener<*>>>()
    /** 父节点 */
    var parent: Cmpt = EMPTY_CMPT
        private set(value) {
            if (field != EMPTY_CMPT && value != EMPTY_CMPT && field != value)
                throw AssertionError("不允许在已经添加父节点的情况下重新设置父节点")
            field = value
        }
    var gui: BaseGraphics? = null
        private set
    /** 是否安装过父节点 */
    var isInstallParent = false
    /** 类名列表，内部元素不重复 */
    val classList = ObjectRBTreeSet<String>()

    /** 初始化客户端对象 */
    @SideOnly(Side.CLIENT)
    abstract fun initClientObj(): ICmptClient

    /** 构建一个新的对象，属性列表拷贝当前属性列表 */
    abstract fun buildNewObj(): Cmpt

    /** 深度拷贝自身 */
    fun copy(): Cmpt {
        val result = buildNewObj()
        eachAllChildren {
            result.addChild(it.copy())
        }
        return result
    }

    /** 在给GUI统一设置handler时触发 */
    internal open fun initHandler(handler: ItemStackHandler) {}

    /** 接收从客户端发送的信息 */
    protected open fun receive(message: NBTBase) {}

    /**
     * 发送信息到客户端
     * @param player 接收信息的玩家对象
     * @param message 要传输的信息
     */
    fun send2Client(player: EntityPlayer, message: NBTBase) {
        GraphicsMessage.sendToClient(message, id, player)
    }

    /** 接收网络通信 */
    fun receiveNetworkMessage(message: NBTTagCompound) {
        val isEvent = message.getBoolean("event")
        if (!isEvent) return receive(message.getTag("data"))
        val name = message.getString("data")
        eventMap[name]?.forEach { it.active(null) }
    }

    /** 网络通信接口，每Tick调用一次 */
    open fun networkEvent(player: EntityPlayer) {}

    /** 判断该控件是否有父节点 */
    fun hasParent(): Boolean = parent != EMPTY_CMPT

    /** 初始化父节点信息 */
    internal open fun installParent(parent: Cmpt, gui: BaseGraphics) {
        this.gui = gui
    }

    /** 移除父节点信息 */
    protected open fun uninstallParent(oldParent: Cmpt, gui: BaseGraphics) {
        this.parent = EMPTY_CMPT
    }

    /** 向控件添加一个子控件 */
    fun addChild(cmpt: Cmpt) {
        childrenList.add(cmpt)
        cmpt.parent = this
    }

    /**
     * 从控件中移除一个子控件
     * 该函数只能移除与当前控件为直接父子关系的控件
     */
    fun removeChild(cmpt: Cmpt) {
        childrenList.remove(cmpt)
        cmpt.uninstallParent(this, gui!!)
    }

    /** 从控件中移除指定控件 */
    fun deleteCmpt(cmpt: Cmpt): Boolean {
        if (childrenList.remove(cmpt)) return true
        for (child in childrenList) {
            if (child.deleteCmpt(cmpt)) return true
        }
        return false
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
     * @param exp 匹配表达式
     * @param limit 数量限制
     * @return 所有匹配的控件（按控件出现顺序排序）
     */
    fun queryCmptLimit(exp: ICmptExp, limit: Int): LinkedList<Cmpt> {
        val list = LinkedList<Cmpt>()
        if (limit == 0) return list
        eachChildren {
            if (exp.matchFirst(it)) {
                if (exp.size == 1) list.add(it)
                else list.addAll(it.queryCmptLimit(exp.removeFirst(), limit - list.size))
            }
            list.addAll(it.queryCmptLimit(exp, limit - list.size))
            if (list.size == limit) it else null
        }
        return list
    }

    /**
     * 通过匹配表达式匹配GUI中所有与该表达式相匹配的控件
     * @see queryCmptLimit
     */
    fun queryCmptAll(exp: ICmptExp): LinkedList<Cmpt> = queryCmptLimit(exp, Int.MAX_VALUE)

    /**
     * 通过匹配表达式匹配GUI中所有与该表达式相匹配的控件
     * @see queryCmptLimit
     */
    fun queryCmptAll(exp: String) = queryCmptAll(ComplexCmptExp(exp))

    /**
     * 通过匹配表达式匹配GUI中第一个与该表达式相匹配的控件
     * @return 未查询到则返回`null`
     * @see queryCmptLimit
     */
    fun queryCmpt(exp: ICmptExp): Cmpt? {
        val result = queryCmptLimit(exp, 1)
        return if (result.isEmpty()) null else result.first
    }

    /**
     * 通过匹配表达式匹配GUI中第一个与该表达式相匹配的控件
     * @return 未查询到则返回`null`
     * @see queryCmptLimit
     */
    fun queryCmpt(exp: String) = queryCmpt(ComplexCmptExp(exp))

    /**
     * 发布事件
     *
     * 如果某一个事件执行过程中发生了异常，不会影响其它事件的执行
     *
     * @param name 事件名称
     * @param message 触发事件传送的数据
     */
    fun dispatchEvent(name: String, message: ListenerData) {
        /** 触发指定控件的事件 */
        fun activeEvent(it: Cmpt, network: Boolean) {
            try {
                val listeners = it.eventMap[name] ?: return
                for (listener in listeners) {
                    listener.activeObj(message)
                    if (message.cancel) break
                }
                if (message.send2Service && network && isClient()) {
                    client.send2Service(NBTTagString(name), true)
                }
            } catch (e: Exception) {
                MISysInfo.err("触发事件过程中发生异常：\n\tname=$name\n\tmessage=$message\n\tit=$it", e)
            }
        }
        /** 触发指定事件及其父控件的事件 */
        fun activeParentEvent(it: Cmpt) {
            if (!message.reverse) activeEvent(it, this == it)
            if (it.hasParent() && !message.cancel) activeParentEvent(it.parent)
            if (message.reverse && !message.cancel) activeEvent(it, this == it)
        }
        if (message.prohibitTransfer) activeEvent(this, true)
        else activeParentEvent(this)
    }

    /**
     * 注册事件
     * @param name 事件的名称
     * @param listener 事件对象
     */
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
        val EMPTY_CMPT = object : Cmpt(CmptAttributes.valueOfID("null")) {

            override fun initClientObj() = EmptyClient(this)

            override fun buildNewObj() = this

            inner class EmptyClient(cmpt: Cmpt) : ICmptClient {

                override val service = cmpt
                override val style = GraphicsStyle(cmpt)

            }

        }

    }

}