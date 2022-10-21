package top.kmar.mi.api.graphics.utils

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.utils.WorldUtil
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

/**
 * GUI注册机
 * @author EmptyDreams
 */
class GuiRegedit {

    private val registry = Object2ObjectAVLTreeMap<ResourceLocation, Node>()
    private val oppositeRegistry = Int2ObjectAVLTreeMap<ResourceLocation>()
    private val idIndex = AtomicInteger(0)
    @SideOnly(Side.CLIENT)
    private val clientIdIndex = AtomicInteger(0)

    /** 注册一个双端GUI */
    fun registryGui(key: ResourceLocation, root: BaseGraphics.DocumentCmpt) {
        if (key in registry) throw IllegalArgumentException("指定key[$key]已经被注册")
        val id = idIndex.incrementAndGet()
        registry[key] = Node(id, root, LinkedList(), LinkedList(), LinkedList())
        oppositeRegistry[id] = key
    }

    /** 注册一个客户端GUI */
    @SideOnly(Side.CLIENT)
    fun registryClientGui(key: ResourceLocation, root: BaseGraphics.DocumentCmpt) {
        if (key in registry) throw IllegalArgumentException("指定key[$key]已经被注册")
        val id = clientIdIndex.decrementAndGet()
        registry[key] = Node(id, root, LinkedList(), LinkedList(), LinkedList())
        oppositeRegistry[id] = key
    }

    /** 为指定GUI注册一个初始化任务，其会在[BaseGraphics]对象创建完成后触发 */
    fun registryInitTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
        val node = registry[key] ?: throw NullPointerException("指定key[$key]没有被注册")
        node.initList.add(task)
    }

    /** 为指定GUI注册一个循环任务，其会在[BaseGraphics]每次完成网络任务后触发 */
    fun registryLoopTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
        if (WorldUtil.isClient()) return
        val node = registry[key] ?: throw NullPointerException("指定key[$key]没有被注册")
        node.loopList.add(task)
    }

    /** 为指定GUI注册一个客户端循环任务 */
    fun registryClientLoopTask(key: ResourceLocation, task: Consumer<BaseGraphics>) {
        if (WorldUtil.isServer()) return
        val node = registry[key] ?: throw NullPointerException("指定key[$key]没有被注册")
        node.clientLoopList.add(task)
    }

    /** 构建一个GUI对象 */
    fun buildGui(key: ResourceLocation, player: EntityPlayer, pos: BlockPos): BaseGraphics {
        val node = registry[key] ?: throw NullPointerException("对应key[$key]没有被注册")
        return BaseGraphics(player, pos, key, node.root)
    }

    /** 通过key获取内部ID */
    fun getID(key: ResourceLocation): Int {
        val node = registry[key] ?: throw NullPointerException("对应key[$key]没有被注册")
        return node.id
    }

    /** 通过内部id获取key */
    fun getKey(id: Int) =
        oppositeRegistry[id] ?: throw NullPointerException("无效id：$id")

    /** 触发循环任务 */
    fun invokeLoopTask(key: ResourceLocation, gui: BaseGraphics) {
        registry[key]!!.loopList.forEach { it.accept(gui) }
    }

    @SideOnly(Side.CLIENT)
    fun invokeClientLoopTask(key: ResourceLocation, gui: BaseGraphics) {
        registry[key]!!.clientLoopList.forEach { it.accept(gui) }
    }

    /** 触发初始化任务 */
    fun invokeInitTask(key: ResourceLocation, gui: BaseGraphics) {
        registry[key]!!.initList.forEach { it.accept(gui) }
    }

    /** 根据key值顺序重新为GUI分配内部ID，并写入到新的注册机当中 */
    fun sort(): GuiRegedit {
        val result = GuiRegedit()
        registry.forEach { (key, node) ->
            val newId = if (node.id < 0) clientIdIndex.decrementAndGet() else idIndex.incrementAndGet()
            result.registry[key] = node.deepCopy(newId)
            result.oppositeRegistry[newId] = key
        }
        return result
    }

    private data class Node(
        val id: Int,
        val root: BaseGraphics.DocumentCmpt,
        val loopList: MutableList<Consumer<BaseGraphics>>,
        val initList: MutableList<Consumer<BaseGraphics>>,
        val clientLoopList: MutableList<Consumer<BaseGraphics>>
    ) {

        fun deepCopy(id: Int): Node {
            val newLoopList = ArrayList(loopList)
            val newInitList = ArrayList(initList)
            val newClientLoopTask = ArrayList(clientLoopList)
            return Node(id, root, newLoopList, newInitList, newClientLoopTask)
        }

    }

}