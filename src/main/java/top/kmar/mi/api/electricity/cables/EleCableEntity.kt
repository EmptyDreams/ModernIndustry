package top.kmar.mi.api.electricity.cables

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.values
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.electricity.EleEnergy
import top.kmar.mi.api.electricity.cables.InvalidCodeManager.Companion.invalidCodeManager
import top.kmar.mi.api.electricity.caps.ElectricityCapability.capObj
import top.kmar.mi.api.net.IAutoNetwork
import top.kmar.mi.api.net.handler.MessageSender
import top.kmar.mi.api.net.message.block.BlockAddition
import top.kmar.mi.api.net.message.block.BlockMessage
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.TickHelper
import top.kmar.mi.api.utils.container.CacheContainer
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.data.math.Range3D
import top.kmar.mi.api.utils.expands.clipAt
import top.kmar.mi.api.utils.expands.isClient
import top.kmar.mi.api.utils.expands.whatFacing
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * 导线的TE
 * @author EmptyDreams
 */
@AutoTileEntity("cable")
class EleCableEntity : BaseTileEntity(), IAutoNetwork {

    /** 存储指定方向是否连接的有方块 */
    @field:AutoSave
    private val linkData = IndexEnumMap(values())
    /** 连接的上一根导线 */
    @field:AutoSave private var linkedCable0: EnumFacing? = null
    /** 连接的下一根导线 */
    @field:AutoSave private var linkedCable1: EnumFacing? = null

    /** 是否需要发送的客户端 */
    var isSend = false
        private set
    /**
     * 线缆编号，相连线缆的编号的差值的绝对值一定为 1
     *
     * 访问该变量之前请确保已经访问过 [cacheId]，该变量通过 [cacheId]的 getter 进行更新
     */
    @field:AutoSave
    var code: Int = 0
    /** 线路缓存的 code */
    @field:AutoSave
    var cacheId: Int = 0
        private set
        get() {
            if (field == 0) return 0
            val allocator = world.cableCacheIdAllocator
            if (field !in allocator) {
                do {
                    code = world.invalidCodeManager.update(field, code)
                    field = world.invalidCacheData.update(field, code)
                } while (field != 0 && field !in allocator)
                _cache.clear()
            }
            return field
        }
    /** 线路缓存 */
    private val cache: CableCache
        get() {
            cacheId
            return _cache.get()
        }
    private val _cache = CacheContainer {
        if (cacheId == 0) cacheId = world.cableCacheIdAllocator.next()
        world.cableCacheIdAllocator[cacheId, { CableCache() }]
            .apply { update(this@EleCableEntity) }
    }
    /** 电损指数 */
    val lossIndex = 0.0

    /**
     * 向导线请求能量
     * @param maxEnergy 需要的能量
     * @return 获取到的能量 [EleEnergy.capacity] <= [maxEnergy]
     */
    fun requestEnergy(maxEnergy: Int): EleEnergy {
        if (cacheId == 0) return requestEnergyOnly(code, maxEnergy)
        var result: EleEnergy = EleEnergy.empty
        var energy = maxEnergy
        cache.eachBlock(this) {
            val output = it.requestEnergyOnly(code, energy)
            result = result.merge(output)
            energy = maxEnergy - result.capacity
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
            if (!isLink(facing) || facing === linkedCable0 || facing === linkedCable1) continue
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
            if (isLink(facing) && facing !== linkedCable0 && facing !== linkedCable1) return true
        }
        return false
    }

    /** 判断指定方向是否连接的有方块 */
    fun isLink(facing: EnumFacing) = linkData[facing]

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
        if (cacheId == target.cacheId && cacheId != 0) return false
        val opposite = facing.opposite
        if (linkedCable0 == null) {    // 尝试将目标连接到 `0`
            if (target.linkedCable0 == null) target.linkedCable0 = opposite
            else if (target.linkedCable1 == null) target.linkedCable1 = opposite
            else return false
            linkedCable0 = facing
        } else if (linkedCable1 == null) { // 尝试将目标连接到 `1`
            if (target.linkedCable0 == null) target.linkedCable0 = opposite
            else if (target.linkedCable1 == null) target.linkedCable1 = opposite
            else return false
            linkedCable1 = facing
        } else return facing === linkedCable0 || facing === linkedCable1
        linkData[facing] = true
        target.linkData[opposite] = true
        if (cacheId != 0) cache.syncCache(this, target)
        else target.cache.syncCache(target, this)
        sendToPlayers()
        target.sendToPlayers()
        return true
    }

    /** 将当前导线从缓存中移除，并更新与其连接的导线的数据 */
    fun removeFromCache() {
        if (cacheId == 0) return
        cache.clip(world, code, cacheId)
        fun task(facing: EnumFacing) {
            val cable = world.getTileEntity(pos.offset(facing)) ?: return
            cable as EleCableEntity
            if (cable.linkedCable0 === facing.opposite) cable.linkedCable0 = null
            else if (cable.linkedCable1 === facing.opposite) cable.linkedCable1 = null
            else return
            cable.sendToPlayers()
        }
        linkedCable0?.let { task(it) }
        linkedCable1?.let { task(it) }
    }

    /** 移除当前导线与指定方向上的非线缆方块的连接 */
    fun unlinkBlock(facing: EnumFacing) {
        if (!isLink(facing)) return
        assert(facing !== linkedCable0 && facing !== linkedCable1)
        linkData[facing] = false
        if (cacheId != 0) cache.update(this)
        sendToPlayers()
    }

    /** 连接指定方向上的方块 */
    fun linkBlock(facing: EnumFacing) {
        if (isLink(facing)) return
        linkData[facing] = true
        cache.update(this)
        sendToPlayers()
    }

    /**
     * 连接指定方块或删除与非导线方块的连接
     *
     * 该方法不会移除导线与导线直接的连接
     */
    fun linkAllOrUnlinkBlock(facing: EnumFacing, that: TileEntity?) {
        if (that == null) {
            if (facing !== linkedCable0 && facing !== linkedCable1) unlinkBlock(facing)
            return
        }
        if (that is EleCableEntity) linkCable(facing, that)
        else if (that.getCapability(capObj, facing.opposite) == null) unlinkBlock(facing)
        else linkBlock(facing)
    }

    /**
     * 将连接信息发送到客户端
     *
     * 发送任务会在每 tick 的结尾执行，同一 tick 内不会重复发送信息
     */
    fun sendToPlayers() {
        if (isSend || world.isClient()) return
        isSend = true
        TickHelper.addServerTask {
            isSend = false
            MessageSender.send2ClientAround(world, Range3D(pos, 128)) {
                BlockMessage.instance().create(
                    NBTTagByte(linkData.getValue().toByte()), BlockAddition(this)
                )
            }
            true
        }
    }

    /** 接收服务端发送的信息 */
    override fun receive(reader: NBTBase) {
        linkData.setValue((reader as NBTTagByte).int)
        world.markBlockRangeForRenderUpdate(pos, pos)
    }

    override fun getUpdateTag(): NBTTagCompound {
        val nbt = super.getUpdateTag()
        nbt.setByte("link", linkData.getValue().toByte())
        return nbt
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        super.handleUpdateTag(tag)
        linkData.setValue(tag.getByte("link").toInt())
    }

    /** 获取下一个导线 */
    private fun getOtherCable(that: TileEntity): EleCableEntity? {
        val facing = pos.whatFacing(that.pos)
        val next = (if (facing === linkedCable0) linkedCable1 else linkedCable0) ?: return null
        return world.getTileEntity(pos.offset(next)) as EleCableEntity
    }

    companion object {

        const val storageKey = "CableCacheId"

    }

    private class CableCache(initialCapacity: Int = 32) {

        /** 存储线路中线缆的数量 */
        val count: Int
            get() = maxCode - minCode + 1
        /**
         * 存储线路中连接有方块的电线的坐标
         *
         * 队列的 `head` 方向为负方向，`tail` 方向为正方向
         */
        private val blockDeque = ArrayDeque<CacheNode>(initialCapacity)

        private var minCode = Int.MAX_VALUE
        private var maxCode = Int.MIN_VALUE

        /** 更新数据，该函数不会同步导线的缓存 */
        fun update(entity: EleCableEntity) {
            val index = blockDeque.binarySearch { it.code.compareTo(entity.code) }
            if (index < 0) {
                if (entity.hasLinkedBlock())
                    blockDeque.add(-index - 1, CacheNode(entity))
            } else {
                if (!entity.hasLinkedBlock())
                    blockDeque.removeAt(index)
            }
        }

        /** 同步两个导线的 code，该方法需要在导线已经完成连接后调用 */
        fun syncCache(thisEntity: EleCableEntity, thatEntity: EleCableEntity) {
            val isNext = thisEntity.isPositive(thatEntity)
            if (thatEntity.cacheId == 0) {    // 如果另一个方块还未分配缓存则直接将其归并到当前缓存
                thatEntity.code = thisEntity.code + if (isNext) 1 else -1
                thatEntity.cacheId = thisEntity.cacheId
                update(thatEntity)
                minCode = minCode.coerceAtMost(min(thisEntity.code, thatEntity.code))
                maxCode = maxCode.coerceAtLeast(max(thisEntity.code, thatEntity.code))
                return
            }
            val that = thatEntity.cache
            val world = thisEntity.world
            val deleteCache: EleCableEntity
            val newCache: EleCableEntity
            val thatIsPositive = thatEntity.isPositive(thisEntity)
            val codeManager = world.invalidCodeManager
            if (count > that.count) {   // 如果当前缓存大小大于另一个，则将其归并到当前缓存中
                deleteCache = thatEntity
                newCache = thisEntity
                if (isNext) {
                    if (thatIsPositive) {
                        codeManager.markInvalidFlip(
                            thatEntity.cacheId, that.minCode, that.maxCode, maxCode - that.minCode + 1
                        )
                    } else {
                        codeManager.markInvalidLinear(
                            thatEntity.cacheId, that.count, maxCode - that.minCode + 1
                        )
                    }
                    blockDeque.addAll(that.blockDeque)
                    maxCode += that.count
                } else {
                    if (thatIsPositive) {
                        codeManager.markInvalidLinear(
                            thatEntity.cacheId, that.count, minCode - that.maxCode - 1
                        )
                    } else {
                        codeManager.markInvalidFlip(
                            thatEntity.cacheId, that.minCode, that.maxCode, minCode - that.maxCode - 1
                        )
                    }
                    blockDeque.addAll(0, that.blockDeque)
                    minCode -= that.count
                }
            } else {    // 如果另一个缓存的大小大于等于当前缓存的大小，则将当前缓存归并到另一个缓存中
                deleteCache = thisEntity
                newCache = thatEntity
                if (isNext) {
                    if (thatIsPositive) {
                        codeManager.markInvalidFlip(
                            thisEntity.cacheId, minCode, maxCode, that.maxCode - minCode + 1
                        )
                    } else {
                        codeManager.markInvalidLinear(
                            thisEntity.cacheId, count, that.minCode - maxCode - 1
                        )
                    }
                    that.blockDeque.addAll(0, blockDeque)
                } else {
                    if (thatIsPositive) {
                        codeManager.markInvalidLinear(
                            thisEntity.cacheId, count, that.maxCode - minCode + 1
                        )
                    } else {
                        codeManager.markInvalidFlip(
                            thisEntity.cacheId, minCode, maxCode, that.minCode - maxCode - 1
                        )
                    }
                    that.blockDeque.addAll(blockDeque)
                }
                if (thatIsPositive) that.maxCode += count
                else that.minCode -= count
            }
            deleteCache.cache.blockDeque.forEach {
                it.code = codeManager.getNewCode(deleteCache.cacheId, it.code)
            }
            world.invalidCacheData.markInvalid(deleteCache.cacheId, deleteCache.cache.count, newCache.cacheId)
            world.cableCacheIdAllocator.delete(deleteCache.cacheId)
        }

        /**
         * 从指定位置切开缓存
         * @param world 当前世界
         * @param code 分界点（该点会从缓存中移除）
         * @param cacheId 缓存 ID
         */
        fun clip(world: World, code: Int, cacheId: Int) {
            if (code < minCode || code > maxCode) return
            val index = blockDeque.binarySearch { it.code.compareTo(code) }
            if (count == 2) {   // 如果线长为 2 则删除缓存
                world.cableCacheIdAllocator.delete(cacheId)
                world.invalidCacheData.markInvalid(cacheId, 1)
            } else if (code == minCode || code == maxCode) { // 判断被移除的导线是否在端点，是端点的话就无需创建新的缓存
                if (index >= 0) blockDeque.removeAt(index)
                if (code == minCode) ++minCode
                else --maxCode
            } else {    // 从中间断开
                val oldCount = count
                val middle = if (index < 0) -index - 1 else index
                // 将一半的数据转移到新缓存中
                val newCache = CableCache(count - middle + 10)
                val leftIsOld = code - minCode > maxCode - code
                blockDeque.clipAt(newCache.blockDeque, middle, false, leftIsOld)
                // 更新缓存中的 code
                if (leftIsOld) {
                    newCache.minCode = code + 1
                    newCache.maxCode = maxCode
                    maxCode = code - 1
                } else {
                    newCache.minCode = minCode
                    newCache.maxCode = code - 1
                    minCode = code + 1
                }
                // 为缓存分配新的 ID，并移除老的缓存
                val allocator = world.cableCacheIdAllocator
                val hasNewId = count != 1 && newCache.count != 1    // 是否分配新的 ID
                if (hasNewId || count == newCache.count) allocator.delete(cacheId)
                val leftCode =
                    if (hasNewId) allocator.next() else if (leftIsOld && count != 1) cacheId else 0
                val rightCode =
                    if (hasNewId) allocator.next() else if (!leftIsOld && newCache.count != 1) cacheId else 0
                if (hasNewId) allocator[leftCode, { this@CableCache }]
                if (hasNewId) allocator[rightCode, { newCache }]
                // 为方块更新 ID
                if (leftIsOld)
                    world.invalidCacheData.markInvalid(cacheId, oldCount - 1, code, leftCode, rightCode)
                else
                    world.invalidCacheData.markInvalid(cacheId, oldCount - 1, code, rightCode, leftCode)
            }
        }

        /**
         * 从指定位置开始按照距离远近遍历已连接方块的导线
         * @param entity 开始遍历的位点
         * @param breakConsumer 要执行的任务，返回 `false` 表示终止遍历
         */
        fun eachBlock(entity: EleCableEntity, breakConsumer: (EleCableEntity) -> Boolean) {
            val world = entity.world
            fun invoke(pos: BlockPos): Boolean {
                if (!world.isBlockLoaded(pos)) return false
                val te = world.getTileEntity(pos) as EleCableEntity
                return !breakConsumer(te)
            }
            val startIndex = blockDeque.binarySearch { it.code.compareTo(entity.code) }
            var left: Int
            var right: Int
            if (startIndex < 0) {
                left = -startIndex - 2
                right = -startIndex - 1
            } else {
                if (!breakConsumer(entity)) return
                left = startIndex - 1
                right = startIndex + 1
            }
            val middle = entity.code
            while (left != -1 || right != blockDeque.size) {
                if (left == -1) {
                    if (invoke(blockDeque[right].pos)) break
                    ++right
                } else if (right == blockDeque.size) {
                    if (invoke(blockDeque[left].pos)) break
                    --left
                } else {
                    val leftBlock = blockDeque[left]
                    val rightBlock = blockDeque[right]
                    val leftLength = middle - leftBlock.code
                    val rightLength = rightBlock.code - middle
                    val block: CacheNode
                    if (leftLength < rightLength) {
                        block = leftBlock
                        --left
                    } else {
                        block = rightBlock
                        ++right
                    }
                    if (invoke(block.pos)) break
                }
            }
        }

        /** 判断指定导线是否为正方向 */
        private fun EleCableEntity.isPositive(that: EleCableEntity): Boolean {
            val next = getOtherCable(that) ?: return true
            next.cacheId
            return next.code < code
        }

    }

    private data class CacheNode(
        var code: Int,
        val pos: BlockPos
    ) {

        constructor(entity: EleCableEntity) : this(entity.code, entity.pos)

    }

}