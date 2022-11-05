package top.kmar.mi.api.electricity

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import top.kmar.mi.api.araw.interfaces.AutoSave
import top.kmar.mi.api.electricity.cables.EleCableEntity
import top.kmar.mi.api.electricity.caps.ElectricityCapability.capObj
import top.kmar.mi.api.electricity.caps.EmptyElectricityCap
import top.kmar.mi.api.electricity.caps.IElectricityCap
import top.kmar.mi.api.tools.BaseTileEntity
import top.kmar.mi.api.utils.container.IndexEnumMap
import top.kmar.mi.api.utils.expands.computeIfAbsent
import java.util.function.Consumer

/**
 * 电力方块的父级TE
 * @author EmptyDreams
 */
abstract class EleTileEntity : BaseTileEntity() {

    /** 存储方块六个方向上的连接数据 */
    @field:AutoSave
    protected val linkData = IndexEnumMap(EnumFacing.values())
    /** 缓存各个方向上的`cap` */
    private val capArray = Array<IElectricityCap?>(EnumFacing.values().size) { null }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == capObj) {
            if (facing != null) return linkData[facing]
            for (value in EnumFacing.values()) {
                if (linkData[value]) return true
            }
            return false
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == capObj) return capObj.cast(getEleCap(facing))
        return super.getCapability(capability, facing)
    }

    /**
     * 检查输入的能量
     * @param input 输入的能量
     * @param minVoltage 最低电压
     * @param maxVoltage 最高电压
     * @param under 欠压时触发
     * @param over 过压时触发
     * @return 是否可以正常工作
     */
    protected inline fun checkEnergy(
        input: EleEnergy, minVoltage: Int, maxVoltage: Int,
        empty: () -> Unit, under: () -> Unit, over: () -> Unit
    ): Boolean {
        if (input.isEmpty) empty()
        else if (input.voltage > maxVoltage) over()
        else if (input.voltage < minVoltage) under()
        else return true
        return false
    }

    /**
     * 构建指定方向上的`cap`，
     * @param facing 指定的方向，该方向一定已经连接方块
     * @return 构建的`cap`对象，值会被缓存
     */
    protected open fun buildCap(facing: EnumFacing): IElectricityCap {
        return EmptyElectricityCap
    }

    /** 使指定方向上的`cap`缓存失效 */
    protected fun invalidCap(facing: EnumFacing) {
        capArray[facing.index] = null
    }

    /**
     * 获取指定方向上的[IElectricityCap]
     * @param facing 指定方向，如果为`null`则按照[EnumFacing]中的下标顺序查找第一个`cap`
     * @return 查找到的`cap`，如果没有找到则返回`null`
     */
    fun getEleCap(facing: EnumFacing?): IElectricityCap? {
        if (facing == null) {
            for (value in EnumFacing.values()) {
                return getEleCap(value) ?: continue
            }
            return null
        }
        if (!linkData[facing]) return null
        return capArray.computeIfAbsent(facing.index) { buildCap(facing) }
    }

    /** 向周围网络请求能量 */
    open fun requestEnergy(maxEnergy: Int): EleEnergy {
        var energy = maxEnergy
        var result = EleEnergy.empty
        eachLinked {
            val cable = world.getTileEntity(pos.offset(it)) as? EleCableEntity ?: return@eachLinked true
            val request = cable.requestEnergy(energy)
            energy -= request.capacity
            result = result.merge(request)
            energy != 0
        }
        return result
    }

    /** 统计连接了几个方向 */
    open fun countLinks(): Int {
        var result = 0
        for (i in capArray.indices) {
            if (linkData[EnumFacing.values()[i]]) ++result
        }
        return result
    }

    /** 判断指定方向是否连接 */
    open fun isLink(facing: EnumFacing): Boolean = linkData[facing]

    /**
     * 连接指定方向
     * @return 是否连接成功，重复连接返回`true`
     */
    open fun link(facing: EnumFacing): Boolean {
        linkData[facing] = true
        return true
    }

    /** 移除指定方向的连接 */
    open fun unlink(facing: EnumFacing) {
        linkData[facing] = false
    }

    /**
     * 遍历连接的方向
     * @param breakConsumer 遍历任务，返回`false`表示终止遍历
     */
    fun eachLinked(breakConsumer: (EnumFacing) -> Boolean) {
        for (value in EnumFacing.values()) {
            if (isLink(value)) {
                if (!breakConsumer(value)) break
            }
        }
    }

    /** 遍历所有连接的方向 */
    fun eachAllLinked(consumer: Consumer<EnumFacing>) {
        for (value in EnumFacing.values()) {
            if (isLink(value)) {
                consumer.accept(value)
            }
        }
    }

    /** 产生一个爆炸 */
    protected fun explode(strength: Float, isFlaming: Boolean) {
        world.setBlockToAir(pos)
        world.newExplosion(
            null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, strength, isFlaming, true
        )
    }

}