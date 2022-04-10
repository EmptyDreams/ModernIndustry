package top.kmar.mi.api.electricity.info

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.kmar.mi.api.electricity.EleWorker
import top.kmar.mi.api.electricity.interfaces.IEleOutputer
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.content.tileentity.EleSrcCable
import java.util.*
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap as HashMap

private typealias Edge = HashMap<BlockPos, EdgeInfo>

/**
 * 线路的缓存信息
 * @author EmptyDreams
 */
class CableCache {

    companion object {
        /**
         * 重新计算整条线路的缓存
         * @param cable 当前电线
         */
        @JvmStatic fun calculate(cable: EleSrcCable) {
            val cache = CableCache()
            cable.forEachAll { it, _, _ ->
                val pos = it.pos
                WorldUtil.forEachAroundTE(cable.world, it.pos) { te, _ ->
                    if (EleWorker.isOutputer(te))
                        cache.addOutputer(pos, te.pos)
                }
                it.cache = cache
                true
            }
        }
    }

    private var outputerAmount = 0

    /**
     * 存储发电机列表
     *
     * key -> 电线， value -> 连接的发电机
     */
    private val outputers = HashMap<BlockPos, MutableSet<BlockPos>>()

    /** 获取线路中发电机数量  */
    fun getOutputerAmount() = outputerAmount

    /**
     * 移除一个发电机
     * @param cable 发电机连接的电线
     * @param outer 发电机的坐标
     */
    fun removeOuter(cable: BlockPos, outer: BlockPos) {
        val wire = outputers.getOrDefault(cable, null) ?: return
        if (wire.size == 1) outputers.remove(cable) else wire.remove(outer)
        --outputerAmount
    }

    /**
     * 添加一个发电机
     * @param cable 发电机连接的电线
     * @param outer 发电机
     */
    fun addOutputer(cable: BlockPos, outer: BlockPos) {
        val wire = outputers.computeIfAbsent(cable) { TreeSet() }
        if (wire.contains(outer)) return
        wire.add(outer)
        ++outputerAmount
    }

    /**
     * 线路缓存信息：
     *
     * **key-BlockPos**: 用电器连接的电线的坐标
     *
     * **value-Edge**:
     *
     * &emsp;&emsp;**son-key: BlockPos**: 发电机坐标
     * &emsp;&emsp;**son-value: BlockPos**: 缓存信息
     */
    private val lineCache = HashMap<BlockPos, Edge>()

    /**
     * 读取或计算缓存信息，当没有缓存信息时自动计算
     * @param start 起点
     * @param user 用电器的TE
     * @return 计算结果，当线路中没有可用的发电机时返回null
     */
    operator fun invoke(start: EleSrcCable, user: TileEntity): PathInfo? {
        val world = start.world
        val demand = EleWorker.getInputer(user)!!.getEnergyDemand(user)
        val cache = readCache(start)

        var ans: EleEnergy? = null
        var info: EdgeInfo? = null
        var outer: TileEntity? = null
        for ((pos, value) in cache) {
            val (te, outputer) = value(world, pos)
            val energy = outputer.output(te, demand, true)
            if (energy.notEmpty() && (ans == null || energy < ans)) {
                ans = energy
                info = value
                outer = te
            }
        }
        return if (ans != null) PathInfo(ans, info!!.path, outer, user) else null
    }

    private fun readCache(start: EleSrcCable): Edge {
        val cache = lineCache.computeIfAbsent(start.pos) { Edge() }
        if (cache.size != getOutputerAmount()) {
            if (start.linkAmount == 0) {
                fillEdgeInfo(start, null, cache)
            } else {
                fillEdgeInfo(start, start.prevPos, cache)
                fillEdgeInfo(start, start.nextPos, cache)
            }
        }
        return cache
    }

    private fun fillEdgeInfo(start: EleSrcCable, prev: BlockPos?, cache: Edge) {
        val path = LinkedList<TileEntity>()
        start.forEach(prev) { it, _, _ ->
            run {
                path.addLast(it)
                val values = outputers.getOrDefault(it.pos, null) ?: return@run true
                for (outer in values) {
                    if (!cache.containsKey(outer)) {
                        @Suppress("UNCHECKED_CAST")
                        cache[outer] = EdgeInfo(path.clone() as List<TileEntity>)
                    }
                }
                true
            }
        }
    }

}

private data class EdgeInfo(
    /** 通过的线路（可以无序） */
    val path: List<TileEntity>
) {

    operator fun invoke(world: World, pos: BlockPos): Pair<TileEntity, IEleOutputer> {
        val te = world.getTileEntity(pos) ?: throw NullPointerException("没有找到TileEntity：$pos")
        val outputer = EleWorker.getOutputer(te) ?: throw NullPointerException("没有找到EleOutputer：$pos")
        return Pair(te, outputer)
    }

}