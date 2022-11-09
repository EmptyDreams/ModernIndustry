package top.kmar.mi.api.electricity.cables

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData

/**
 * 废弃 ID 管理器
 * @author EmptyDreams
 */
class InvalidCacheManager(name: String) : WorldSavedData(cacheKey) {

    init {
        assert(name == cacheKey)
    }

    private val dataMap = Int2ObjectRBTreeMap<Node>()

    /**
     * 标记一个 ID 为被废弃的
     * @param invalidId 被废弃的 ID
     * @param count 线路长度
     * @param splitCode 分割点的编号，该点被划分到右侧
     * @param leftNewId 左侧新 ID
     * @param rightNewId 右侧新 ID
     */
    fun markInvalid(invalidId: Int, count: Int, splitCode: Int, leftNewId: Int, rightNewId: Int) {
        dataMap.put(invalidId, Node(count, splitCode, leftNewId, rightNewId))
        markDirty()
    }

    /** 标记一个 ID 为废弃的 */
    fun markInvalid(invalidId: Int, count: Int) {
        markInvalid(invalidId, count, 0, 0, 0)
    }

    /**
     * 标记一个 ID 为废弃的，整条线路的新 ID 为同一个值
     * @param invalidId 被废弃的 ID
     * @param count 线路长度
     * @param newId 新的 ID
     */
    fun markInvalid(invalidId: Int, count: Int, newId: Int) {
        markInvalid(invalidId, count, 0, newId, newId)
    }

    /** 通过废弃的 ID 查找新的 ID，未查找到返回 0 */
    fun update(invalidId: Int, code: Int): Int {
        val node = dataMap.get(invalidId) ?: return 0
        if (node.count == 1) dataMap.remove(invalidId)
        else --node.count
        markDirty()
        return if (code < node.splitCode) node.leftNewId else node.rightNewId
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        nbt.keySet.forEach {
            val array = nbt.getIntArray(it)
            dataMap.put(it.toInt(), Node(array[0], array[1], array[2], array[3]))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        dataMap.forEach { (key, node) ->
            val array = intArrayOf(node.count, node.splitCode, node.leftNewId, node.rightNewId)
            compound.setIntArray(key.toString(), array)
        }
        return compound
    }

    override fun toString(): String {
        return dataMap.toString()
    }

    companion object {

        const val cacheKey = "cable_invalid_cache"

        /** 获取存储在当前世界中的无效缓存的数据 */
        @JvmStatic
        val World.invalidCacheData: InvalidCacheManager
            get() {
                var saved = perWorldStorage.getOrLoadData(InvalidCacheManager::class.java, cacheKey)
                if (saved != null) saved as InvalidCacheManager
                else {
                    saved = InvalidCacheManager(cacheKey)
                    perWorldStorage.setData(cacheKey, saved)
                }
                return saved
            }

    }

    private data class Node(
        var count: Int,
        val splitCode: Int,
        val leftNewId: Int,
        val rightNewId: Int
    )

}