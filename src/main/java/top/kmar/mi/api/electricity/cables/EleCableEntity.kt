package top.kmar.mi.api.electricity.cables

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.values
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.electricity.caps.ElectricityCapability.capObj
import top.kmar.mi.api.electricity.info.EleEnergy
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.container.CacheContainer
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.expands.clipAt
import kotlin.math.absoluteValue

/**
 *
 * @author EmptyDreams
 */
class EleCableEntity : BaseTileEntity() {

    /** 存储指定方向是否连接的有方块 */
    @field:AutoSave
    private val linkData = IndexEnumMap(values())
    /** 连接的上一根导线 */
    @field:AutoSave private var prevCable: EnumFacing? = null
    /** 连接的下一根导线 */
    @field:AutoSave private var nextCable: EnumFacing? = null

    /**
     * 线缆编号，相连线缆的编号的差值的绝对值一定为 1
     *
     * 编号沿 [nextCable] 方向严格递增，沿 [prevCable] 方向严格递减
     */
    @field:AutoSave private var code: Int = 0
    /** 线路缓存的 code */
    @field:AutoSave
    private var cacheCode: Int = 0
        set(value) {
            if (field == value) return
            field = value
            _cache.clear()
        }
    /** 线路缓存 */
    private val cache: CableCache
        get() = _cache.get()

    val lossIndex = 0.0

    // ---------- 内部缓存值 start ---------- //

    private var _cache = CacheContainer {
        if (cacheCode == 0) cacheCode = getIdAllocator().next()
        val map = cacheMap.computeIfAbsent(world) { Int2ObjectAVLTreeMap() }
        map.computeIfAbsent(cacheCode) { CableCache() }.apply {
            insert(this@EleCableEntity)
        }
    }

    /** 获取当前世界的线缆ID分配器 */
    private fun getIdAllocator(): IdAllocator = (
            world.perWorldStorage.getOrLoadData(IdAllocator::class.java, "CableId")
                ?: IdAllocator("CableId")
            ) as IdAllocator

    // ---------- 内部缓存值 end ------------ //

    /**
     * 向导线请求能量
     * @param maxEnergy 需要的能量
     * @return 获取到的能量 [EleEnergy.capacity] <= [maxEnergy]
     */
    fun requestEnergy(maxEnergy: Int): EleEnergy {
        if (nextCable == null && prevCable == null)
            return requestEnergyOnly(code, maxEnergy)
        var result: EleEnergy = EleEnergy.empty
        var energy = maxEnergy
        cache.eachBlock(this) {
            val output = it.requestEnergyOnly(code, energy)
            energy -= output.capacity
            result = result.merge(output)
            energy != 0
        }
        return result
    }

    /** 向周围除导线外的方块请求能量 */
    private fun requestEnergyOnly(start: Int, maxEnergy: Int): EleEnergy {
        val length = (start - code + 1).absoluteValue
        var result = EleEnergy.empty
        var energy = maxEnergy
        for (facing in values()) {
            if (energy == 0) break
            if (!isLink(facing) || facing === nextCable || facing === prevCable) continue
            val cap = world.getTileEntity(pos.offset(facing))?.getCapability(capObj, facing.opposite)
            require(cap != null) { "导线[$this]在指定方向[$facing]上的连接存在异常" }
            val output = cap.extract(energy) {
                val real = lossIndex * it.current * length
                if (real < 1e-6) 0
                else real.toInt().coerceAtLeast(1)
            }
            result = result.merge(output)
            energy -= result.capacity
        }
        return result
    }

    /** 判断当前导线是否连接的有非线缆方块 */
    fun hasLinkedBlock(): Boolean {
        for (facing in values()) {
            if (isLink(facing) && facing !== nextCable && facing !== prevCable) return true
        }
        return false
    }

    /** 判断指定方向是否连接的有方块 */
    fun isLink(facing: EnumFacing) = linkData[facing]

    /**
     * 连接指定方向的导线
     *
     * 连接时会同时修改两根导线的数据
     *
     * @return 是否连接成功
     */
    fun linkCable(facing: EnumFacing): Boolean {
        val entity = world.getTileEntity(pos.offset(facing))
        if (entity !is EleCableEntity) return false
        return linkCable(facing, entity)
    }

    /**
     * 尝试连接指定导线（不循序环形连接）
     *
     * 连接时会同时修改两根导线的数据
     *
     * @param facing 要连接的导线相对于当前导线的方向
     * @param target 要连接的导线的TE对象
     * @return 是否连接成功
     */
    fun linkCable(facing: EnumFacing, target: EleCableEntity): Boolean {
        if (isLink(facing)) return true
        // 检查两个导线是否在同一个线路中，是则禁止连接
        if (cacheCode == target.cacheCode && cacheCode != 0) return false
        val opposite = facing.opposite
        if (prevCable == null) {    // 尝试将目标连接到 `prev` 方向
            if (target.nextCable == null) {
                prevCable = facing
                target.nextCable = opposite
            } else if (target.nextCable !== opposite) return false
        } else if (nextCable == null) { // 尝试将目标连接到 `next` 方向
            if (target.prevCable == null) {
                nextCable = facing
                target.prevCable = opposite
            } else if (target.prevCable !== opposite) return false
        } else return facing === prevCable || facing === nextCable
        linkData[facing] = true
        target.linkData[opposite] = true
        if (cacheCode != 0) cache.syncCache(this, target)
        else target.cache.syncCache(target, this)
        return true
    }

    /** 移除当前导线与指定方向上的任意方块的连接 */
    fun unlink(facing: EnumFacing) {
        if (!isLink(facing)) return
        linkData[facing] = false
        val targetPos = pos.offset(facing)
        if (facing == prevCable) {
            val that = world.getTileEntity(targetPos) as EleCableEntity
            prevCable = null
            that.nextCable = null
            cache.clipAt(this)
        } else if (facing == nextCable) {
            val that = world.getTileEntity(targetPos) as EleCableEntity
            nextCable = null
            that.prevCable = null
            cache.clipAt(that)
        }
    }

    /** 连接指定方向上的方块 */
    fun linkBlock(facing: EnumFacing) {
        linkData[facing] = true
        cache.update(this)
    }

    /** 计算两个导线之间的路径长度 */
    fun distance(that: EleCableEntity): Int {
        assert(cache == that.cache) { "两个导线($pos and ${that.pos})不再同一条线路中" }
        return (code - that.code).absoluteValue
    }

    /** 判断当前节点是否为端点 */
    fun isEndpoint(): Boolean =
        (nextCable == null && prevCable == null) ||
                (nextCable != null && prevCable == null) || nextCable == null

    override fun onLoad() {
        if (cacheCode != 0 && cacheCode !in getIdAllocator()) {
            cacheCode = 0
        }
    }

    /**
     * 从当前位置开始遍历线路，必须保证当前导线为线路端点
     * @param task 要执行的任务
     */
    fun startEach(task: (EleCableEntity) -> Unit) {
        assert(isEndpoint()) { "当前导线[$pos]不是端点" }
        startEach(nextCable != null, task)
    }

    /**
     * 从当前位置开始遍历线路
     * @param prev 上一根导线
     * @param task 要执行的任务
     */
    fun startEach(prev: EleCableEntity, task: (EleCableEntity) -> Unit) {
        val isNext = nextCable != null && pos.offset(nextCable!!) == prev.pos
        startEach(isNext, task)
    }

    /**
     * 从当前位置按照指定方向遍历线路
     * @param direction 方向，`true` 表示向 [nextCable] 方向
     * @param task 要执行的任务
     */
    fun startEach(direction: Boolean, task: (EleCableEntity) -> Unit) {
        val next: (EleCableEntity) -> EnumFacing? =
            if (direction) {
                { it.nextCable }
            } else {
                { it.prevCable }
            }
        startEach(next, task)
    }

    /**
     * 从当前位置开始遍历线路
     *
     * 遍历会在以下情况下正常结束：
     * + [next] 返回 `null`
     * + 遇到了未加载的区块
     *
     * @param next 或许下一个导线的方向，返回 `null` 表示终止遍历
     * @param task 要执行的任务
     */
    fun startEach(next: (EleCableEntity) -> EnumFacing?, task: (EleCableEntity) -> Unit) {
        var point = this
        while (true) {
            task(point)
            val facing = next(point) ?: break
            val nextPos = point.pos.offset(facing)
            if (!world.isBlockLoaded(nextPos)) break
            point = world.getTileEntity(nextPos) as EleCableEntity
        }
    }

    companion object {

        private val cacheMap = Object2ObjectArrayMap<World, Int2ObjectMap<CableCache>>()

    }

    private class CableCache(initialCapacity: Int = 32) {

        /** 存储线路中线缆的数量 */
        val count: Int
            get() = blockDeque.last().code - blockDeque.first().code + 1
        /**
         * 存储线路中连接有方块的电线的坐标
         *
         * 队列的 `head` 方向为 [prevCable] 方向，`tail` 方向为 [nextCable] 方向
         */
        private val blockDeque = ArrayDeque<CacheNode>(initialCapacity)

        /** 插入一个导线，该函数不会同步导线的缓存 */
        fun insert(entity: EleCableEntity) {
            val index = -blockDeque.binarySearch { it.code.compareTo(entity.code) } - 1
            assert(index >= 0) { "重复插入元素[${entity.pos}]" }
            blockDeque.add(index, CacheNode(entity))
        }

        /** 更新一个导线的状态 */
        fun update(entity: EleCableEntity) {
            val index = blockDeque.binarySearch { it.code.compareTo(entity.code) }
            if (index < 0) {
                val insert = -index - 1
                blockDeque.add(insert, CacheNode(entity))
            } else blockDeque[index].block = entity.hasLinkedBlock()
        }

        /** 同步两个导线的 code，该方法需要在导线已经完成连接后调用 */
        fun syncCache(thisEntity: EleCableEntity, thatEntity: EleCableEntity) {
            val plus =
                if (thisEntity.prevCable?.let { thisEntity.pos.offset(it) != thatEntity.pos } == false) 1 else -1
            if (thatEntity.cacheCode == 0) {    // 如果另一个方块还未分配缓存则直接将其归并到当前缓存
                thatEntity.cacheCode = thisEntity.cacheCode
                thatEntity.code = thisEntity.code + plus
                return insert(thatEntity)
            }
            val that = thatEntity.cache
            var code = thisEntity.code
            val deleteCode: Int
            if (count > that.count) {   // 如果当前缓存大小大于另一个，则将其归并到当前缓存中
                deleteCode = thatEntity.cacheCode
                thisEntity.startEach(thatEntity) {
                    code += plus
                    it.cacheCode = thisEntity.cacheCode
                    it.code = code
                }
                if (plus == 1) blockDeque.addAll(that.blockDeque)
                else blockDeque.addAll(0, that.blockDeque)
            } else {    // 如果另一个缓存的大小大于等于当前缓存的大小，则将当前缓存归并到另一个缓存中
                deleteCode = thisEntity.cacheCode
                thatEntity.startEach(thisEntity) {
                    code -= plus
                    it.cacheCode = thatEntity.cacheCode
                    it.code = code
                }
                if (plus == 1) that.blockDeque.addAll(0, blockDeque)
                else that.blockDeque.addAll(blockDeque)
            }
            cacheMap[thisEntity.world]!!.remove(deleteCode)
            thisEntity.getIdAllocator().delete(deleteCode)
        }

        /**
         * 将缓存从指定位置切开分为两份，并为线路中的导线设置新的 [cacheCode]
         *
         * 该操作会将 [entity] 分配到 [nextCable] 方向
         */
        fun clipAt(entity: EleCableEntity) {
            val world = entity.world
            val index = blockDeque.binarySearch { it.code.compareTo(entity.code) }
            assert(index >= 0) { "剪切部位[$entity]不在线路中" }
            // 判断被移除的导线是否在端点，是端点的话就无需创建新的缓存
            if (index == 0 || index == blockDeque.lastIndex) {
                blockDeque.removeAt(index)
            } else {
                val newCache = CableCache(count - index + 10)
                blockDeque.clipAt(newCache.blockDeque, index, true)
                val allocator = entity.getIdAllocator()
                allocator.delete(entity.cacheCode)
                val leftCode = allocator.next()
                val rightCode = allocator.next()
                with(cacheMap[world]!!) {
                    remove(entity.cacheCode)
                    put(leftCode, this@CableCache)
                    put(rightCode, newCache)
                }
                // 为方块更新 ID
                blockDeque.forEach {
                    it.code = leftCode
                    if (world.isBlockLoaded(it.pos)) {
                        val te = world.getTileEntity(it.pos) as EleCableEntity
                        te.cacheCode = leftCode
                    }
                }
                newCache.blockDeque.forEach {
                    it.code = rightCode
                    if (world.isBlockLoaded(it.pos)) {
                        val te = world.getTileEntity(it.pos) as EleCableEntity
                        te.cacheCode = rightCode
                        te._cache.set(newCache)
                    }
                }
            }
            entity.cacheCode = 0
        }

        /**
         * 从指定位置开始按照距离远近遍历已连接方块的导线
         * @param entity 开始遍历的位点
         * @param breakConsumer 要执行的任务，返回 `false` 表示终止遍历
         */
        fun eachBlock(entity: EleCableEntity, breakConsumer: (EleCableEntity) -> Boolean) {
            val world = entity.world
            blockDeque[entity.code].let {
                if (it.block) {
                    if (!breakConsumer(entity)) return
                }
            }
            fun invoke(pos: BlockPos): Boolean {
                if (!world.isBlockLoaded(pos)) return false
                val te = world.getTileEntity(pos) as EleCableEntity
                return !breakConsumer(te)
            }
            var left = entity.code - 1
            var right = entity.code + 1
            var isLeft = true
            while (left != -1 || right != blockDeque.size) {
                if (isLeft && left != -1) {
                    isLeft = false
                    if (invoke(blockDeque[left].pos)) break
                    --left
                } else if (right != blockDeque.size) {
                    isLeft = true
                    if (invoke(blockDeque[right].pos)) break
                    ++right
                }
            }
        }

    }

    private data class CacheNode(
        var code: Int,
        val pos: BlockPos,
        var block: Boolean
    ) {

        constructor(entity: EleCableEntity) : this(entity.code, entity.pos, entity.hasLinkedBlock())

    }

}