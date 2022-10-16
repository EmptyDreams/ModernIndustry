package top.kmar.mi.api.graphics.utils

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphics
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

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
        val id = idIndex.incrementAndGet()
        registry[key] = Node(id, root, LinkedList(), LinkedList())
        oppositeRegistry[id] = key
    }

    /** 注册一个客户端GUI */
    @SideOnly(Side.CLIENT)
    fun registryClientGui(key: ResourceLocation, root: BaseGraphics.DocumentCmpt) {
        val id = clientIdIndex.decrementAndGet()
        registry[key] = Node(id, root, LinkedList(), LinkedList())
        oppositeRegistry[id] = key
    }

    /** 为指定GUI注册一个初始化任务，其会在[BaseGraphics]对象创建完成后触发 */
    fun registryInitTask(key: ResourceLocation, task: InitTask) {
        registry[key]!!.initList.add(task)
    }

    /** 为指定GUI注册一个循环任务，其会在[BaseGraphics]每次完成网络任务后触发 */
    fun registryLoopTask(key: ResourceLocation, task: (BaseGraphics) -> Unit) {
        registry[key]!!.loopList.add(task)
    }

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
        registry[key]!!.loopList.forEach { it(gui) }
    }

    /** 触发初始化任务 */
    fun invokeInitTask(key: ResourceLocation, gui: BaseGraphics, player: EntityPlayer, pos: BlockPos) {
        registry[key]!!.initList.forEach { it.invoke(gui, player, pos) }
    }

    /** 根据key值顺序重新为GUI分配内部ID，并写入到新的注册机当中 */
    fun sort(): GuiRegedit {
        val result = GuiRegedit()
        registry.forEach { (key, node) ->
            if (node.id < 0) result.registryClientGui(key, node.root)
            else result.registryGui(key, node.root)
            node.initList.forEach { result.registryInitTask(key, it) }
            node.loopList.forEach { result.registryLoopTask(key, it) }
        }
        return result
    }

    private data class Node(
        val id: Int,
        val root: BaseGraphics.DocumentCmpt,
        val loopList: MutableList<(BaseGraphics) -> Unit>,
        val initList: MutableList<InitTask>
    )

    fun interface InitTask {

        fun invoke(gui: BaseGraphics, player: EntityPlayer, pos: BlockPos)

    }

}