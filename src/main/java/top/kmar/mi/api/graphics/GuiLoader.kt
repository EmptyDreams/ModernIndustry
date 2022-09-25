package top.kmar.mi.api.graphics

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.register.others.AutoLoader
import java.lang.reflect.Modifier
import java.util.concurrent.atomic.AtomicInteger

/**
 * GUI加载器
 * @author EmptyDreams
 */
@AutoLoader
object GuiLoader : IGuiHandler {

    /** 存储注册列表 */
    private val registers = Int2ObjectRBTreeMap<Class<out BaseGraphics>>()
    /** 注册下标 */
    private val registryIndex = AtomicInteger(0)
    /** 客户端注册下标 */
    @SideOnly(Side.CLIENT)
    private val registryIndexClient = AtomicInteger(0)
    var registryFinish = false
        private set

    init {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModernIndustry.instance, this)
        MinecraftForge.EVENT_BUS.post(MIGuiRegistryEvent())
        registryFinish = true
    }

    /** 构建一个服务端的GUI对象 */
    override fun getServerGuiElement(ID: Int,
                                     player: EntityPlayer, world: World,
                                     x: Int, y: Int, z: Int
    ): BaseGraphics {
        val id = registers[ID] ?: throw IllegalArgumentException("指定ID[$ID]的GUI不存在")
        val constructor = try {
            id.getDeclaredConstructor()
        } catch (e: NoSuchElementException) {
            throw AssertionError("${id.name} 不包含无参构造函数")
        }
        if (!Modifier.isPublic(constructor.modifiers)) constructor.isAccessible = true
        return constructor.newInstance().apply { init(player, BlockPos(x, y, z)) }
    }

    /** 构建一个客户端的GUI对象 */
    @SideOnly(Side.CLIENT)
    override fun getClientGuiElement(ID: Int,
                                     player: EntityPlayer, world: World,
                                     x: Int, y: Int, z: Int) =
        getServerGuiElement(ID, player, world, x, y, z).client

    class MIGuiRegistryEvent : Event() {

        /** 检查此时是否可以注册 */
        fun check() = !registryFinish

        /** 注册一个客户端服务端通用的GUI，注册阶段过后不能调用该函数 */
        fun registry(clazz: Class<out BaseGraphics>): Int {
            if (registryFinish) throw AssertionError("GUI必须通过事件注册")
            val id = registryIndex.incrementAndGet()
            registers[id] = clazz
            return id
        }

        /** 注册一个客户端的GUI，该函数可在注册事件完毕后继续注册 */
        @SideOnly(Side.CLIENT)
        fun registryClient(clazz: Class<out BaseGraphics>): Int {
            val id = registryIndexClient.decrementAndGet()
            registers[id] = clazz
            return id
        }

    }

}