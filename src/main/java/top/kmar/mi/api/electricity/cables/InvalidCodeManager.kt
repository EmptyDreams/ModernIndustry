package top.kmar.mi.api.electricity.cables

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import top.kmar.mi.api.electricity.cables.CableCodeTransformEnum.FLIP
import top.kmar.mi.api.electricity.cables.CableCodeTransformEnum.LINEAR
import top.kmar.mi.api.utils.expands.floorDiv2

/**
 * 旧导线 code 管理器
 * @author EmptyDreams
 */
class InvalidCodeManager(name: String) : WorldSavedData(cacheKey) {

    private val codeMap = Int2ObjectRBTreeMap<Node>()

    init {
        assert(name == cacheKey)
    }

    /** 更新某个 code，为查询到则返回 [oldCode] */
    fun update(id: Int, oldCode: Int): Int {
        val node = codeMap.get(id) ?: return oldCode
        if (node.count == 1) codeMap.remove(id)
        else --node.count
        return node.update(oldCode)
    }

    /** 标记某个缓存中的所有导线的 code 为等待线性更新更新状态 */
    fun markInvalidLinear(id: Int, count: Int, value: Int) {
        assert(id !in codeMap)
        codeMap.put(id, Node(LINEAR, count, 0, 0, value))
    }

    /** 标记某个缓存中的所有导线的 code 为等待翻转更新的状态 */
    fun markInvalidFlip(id: Int, min: Int, max: Int) {
        assert(id !in codeMap)
        codeMap.put(id, Node(FLIP, max - min + 1, min, max, 0))
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        nbt.keySet.forEach { id ->
            val array = nbt.getIntArray(id)
            codeMap.put(id.toInt(), Node(
                CableCodeTransformEnum.values()[array[0]],
                array[1], array[2], array[3], array[4]
            ))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        codeMap.forEach { (id, node) ->
            val (map, count, min, max, value) = node
            compound.setIntArray(id.toString(), intArrayOf(map.ordinal, count, min, max, value))
        }
        return compound
    }

    private data class Node(
        /** 变换方式 */
        val map: CableCodeTransformEnum,
        /** 需要更新的数量 */
        var count: Int,
        /** 最小 code */
        val min: Int,
        /** 最大 code */
        val max: Int,
        /** 更新辅助值 */
        val value: Int
    ) {

        fun update(oldCode: Int): Int = map.update(oldCode, min, max, value)

    }

    companion object {

        private const val cacheKey = "cable_invalid_code"

        @JvmStatic
        val World.invalidCodeManager: InvalidCodeManager
            get() {
                var allocator = perWorldStorage.getOrLoadData(InvalidCacheManager::class.java, cacheKey)
                if (allocator == null) {
                    allocator = InvalidCodeManager(cacheKey)
                    perWorldStorage.setData(cacheKey, allocator)
                } else allocator as InvalidCodeManager
                return allocator
            }

    }

}

/**
 * 导线 code 更新方式
 * @author EmptyDreams
 */
enum class CableCodeTransformEnum {

    /** 线性变换（整体增加或减少） */
    LINEAR {
        override fun update(oldCode: Int, min: Int, max: Int, value: Int): Int {
            return oldCode + value
        }
    },
    /** 翻转 */
    FLIP {
        override fun update(oldCode: Int, min: Int, max: Int, value: Int): Int {
            val length = max - min
            val mid = min + length.floorDiv2()
            return (mid shl 1) - oldCode - (length.and(1) xor 1)
        }
    };

    /** 将旧 code 更新为新的 code */
    abstract fun update(oldCode: Int, min: Int, max: Int, value: Int): Int

}