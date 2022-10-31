package top.kmar.mi.api.craft

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.shapes.IShape

/**
 * 合成表注册机管理器
 * @author EmptyDreams
 */
object CraftGuide {

    @JvmStatic
    private val regeditMap = Object2ObjectOpenHashMap<String, CraftRegedit>()

    /**
     * 注册一个合成表
     * @param group 合成表分组名称
     * @param id 合成表 ID，同一个分组内 ID 不可重复
     * @param shape 合成表输入
     * @param output 合成表输出
     * @throws IllegalArgumentException 如果 ID 重复
     */
    @JvmStatic
    fun registry(group: String, id: ResourceLocation, shape: IShape, output: CraftOutput) {
        regeditMap.computeIfAbsent(group) { CraftRegedit() }.registry(id, shape, output)
    }

    /** 获取一个注册机 */
    @JvmStatic
    fun getRegedit(group: String) = regeditMap[group]

    /** 获取指定合成表的输出 */
    @JvmStatic
    fun findOutput(group: String, id: ResourceLocation) =
        getRegedit(group)?.findOutput(id)

    /** 通过输入获取合成表输出 */
    @JvmStatic
    fun findOutput(group: String, input: ElementList) =
        getRegedit(group)?.findOutput(input)

    /** 通过输出查询无序合成表的输出 */
    @JvmStatic
    fun findDisorderlyOutput(group: String, input: ElementList) =
        getRegedit(group)?.findDisorderlyOutput(input)

    /** 判断是否存在与指定输入相匹配的合成表 */
    @JvmStatic
    fun has(group: String, input: ElementList) = regeditMap[group]?.findOutput(input) != null

}