package top.kmar.mi.api.craft

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.shapes.IShape

/**
 * 合成表注册机管理器
 * @author EmptyDreams
 */
object CraftGuide {

    private val regeditMap = Object2ObjectOpenHashMap<String, CraftRegedit>()

    /**
     * 注册一个合成表
     * @param group 合成表分组名称
     * @param id 合成表 ID，同一个分组内 ID 不可重复
     * @param shape 合成表输入
     * @param output 合成表输出
     * @throws IllegalArgumentException 如果 ID 重复
     */
    fun registry(group: String, id: ResourceLocation, shape: IShape, output: CraftOutput) {
        regeditMap.computeIfAbsent(group) { CraftRegedit() }.registry(id, shape, output)
    }

}