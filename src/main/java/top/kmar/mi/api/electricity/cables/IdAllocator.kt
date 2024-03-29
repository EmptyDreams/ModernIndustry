package top.kmar.mi.api.electricity.cables

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import top.kmar.mi.api.utils.expands.isServer
import top.kmar.mi.api.utils.expands.random
import java.util.concurrent.atomic.AtomicInteger

/**
 * 导线的ID分配器，特别规定：`0`为无效ID
 * @author EmptyDreams
 */
class IdAllocator(name: String) : WorldSavedData(name) {

    /** 已加载的ID */
    private val loadedId = IntAVLTreeSet()
    /** 分配ID的位置 */
    private val flag = AtomicInteger(1)
    /** 存储附属信息 */
    private val infoMap = Int2ObjectRBTreeMap<Any>()

    /** 获取下一个ID，当ID碰撞时会随机增加`[1, 8]`的数值 */
    fun next(): Int {
        var index = flag.getAndIncrement()
        while (index == 0 || index in loadedId) {
            index = flag.addAndGet(random.nextInt(8) + 1)
        }
        loadedId += index
        markDirty()
        return index
    }

    /** 移除指定ID */
    fun delete(id: Int) {
        loadedId.remove(id)
        infoMap.remove(id)
        markDirty()
    }

    /**
     * 获取指定 id 对应的 info，如果不存在则调用 [supplier] 进行创建
     * @throws ClassCastException 如果 info 类型不为 [T]
     */
    operator fun <T> get(id: Int, supplier: () -> T): T {
        assert(id in this)
        val result = infoMap.get(id)
        @Suppress("UNCHECKED_CAST")
        if (result != null) return result as T
        val value = supplier()
        infoMap.put(id, value)
        return value
    }

    operator fun contains(id: Int): Boolean = id in loadedId

    override fun readFromNBT(nbt: NBTTagCompound) {
        val array = nbt.getIntArray("data")
        array.forEach { loadedId.add(it) }
        flag.set(nbt.getInteger("flag"))
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val array = loadedId.toIntArray()
        compound.setIntArray("data", array)
        compound.setInteger("flag", flag.get())
        return compound
    }

    fun idList() = loadedId.clone() as IntAVLTreeSet

    fun infoList() = infoMap.clone()

    companion object {

        /** 当前世界的线缆 ID 分配器 */
        @JvmStatic
        val World.cableCacheIdAllocator: IdAllocator
            get() {
                assert(isServer())
                var allocator = perWorldStorage.getOrLoadData(IdAllocator::class.java, EleCableEntity.storageKey)
                if (allocator == null) {
                    allocator = IdAllocator(EleCableEntity.storageKey)
                    perWorldStorage.setData(EleCableEntity.storageKey, allocator)
                } else allocator as IdAllocator
                return allocator
            }

    }

}