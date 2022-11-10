package top.kmar.mi.api.net.messages.block

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.net.CommonMessage
import top.kmar.mi.api.net.NetworkLoader
import top.kmar.mi.api.net.handlers.IAutoNetworkHandler
import top.kmar.mi.api.net.handlers.MessageHandlerRegedit
import top.kmar.mi.api.net.handlers.RetryMessage
import top.kmar.mi.api.net.messages.block.cap.BlockNetworkCapability
import top.kmar.mi.api.regedits.others.AutoLoader
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.TickHelper
import top.kmar.mi.api.utils.container.SideConcurrentQueue
import top.kmar.mi.api.utils.expands.getWorld
import top.kmar.mi.api.utils.expands.handleAll
import java.util.*

/**
 * 基于方块的网络通信
 * @author EmptyDreams
 */
@EventBusSubscriber
@AutoLoader
object BlockMessage : IAutoNetworkHandler {

    const val key = "mi:block"
    /** 一个信息最大尝试次数 */
    const val maxTryCount = 50

    init {
        MessageHandlerRegedit.registry(key, this)
    }

    /**
     * 发送一个信息到服务端
     * @param entity 发送信息的方块的 TE
     * @param data 要发送的信息
     */
    @SideOnly(Side.CLIENT)
    @JvmStatic
    fun sendToServer(entity: TileEntity, data: NBTBase) {
        NetworkLoader.instance.sendToServer(packing(entity, data))
    }

    /**
     * 发送一个信息到所有需要渲染当前方块的玩家
     * @param entity 发送信息的方块的 TE
     * @param data 要发送的信息
     */
    @JvmStatic
    fun sendToClient(entity: TileEntity, data: NBTBase) {
        val message = packing(entity, data)
        val x = entity.pos.x.toDouble()
        val y = entity.pos.y.toDouble()
        val z = entity.pos.z.toDouble()
        entity.world.playerEntities
            .asSequence()
            .filter { it.isInRangeToRender3d(x, y, z) }
            .map { it as EntityPlayerMP }
            .forEach { NetworkLoader.instance.sendTo(message, it) }
    }

    /** 将一个信息封装为 [IMessage] */
    private fun packing(entity: TileEntity, data: NBTBase): IMessage {
        val message = NBTTagCompound().apply {
            setInteger("world", entity.world.provider.dimension)
            setIntArray("pos", intArrayOf(entity.pos.x, entity.pos.y, entity.pos.z))
            setTag("data", data)
        }
        return CommonMessage(key, message)
    }

    override fun parse(message: NBTBase, ctx: MessageContext): RetryMessage? {
        message as NBTTagCompound
        val world = message.getInteger("world")
        val posArray = message.getIntArray("pos")
        val pos = BlockPos(posArray[0], posArray[1], posArray[1])
        val data = message.getTag("data")
        queue.add(Node(world, pos, ctx, data))
        return null
    }

    private val queue = SideConcurrentQueue<Node>()

    private data class Node(
        val world: Int,
        val pos: BlockPos,
        val ctx: MessageContext,
        val data: NBTBase
    ) {

        var parseCount = 0

    }

    init {
        TickHelper.addServerTask {
            queue.handleAll {
                val world = getWorld(it.world)
                !parse(world, it)
            }
            false
        }
        if (FMLCommonHandler.instance().side.isClient) {
            TickHelper.addClientTask {
                val world = Minecraft.getMinecraft().world ?: return@addClientTask false
                queue.handleAll {
                    if (it.world != world.provider.dimension) {
                        MISysInfo.err("[BlockMessage] 一个信息由于世界信息不符被抛弃：\n\t\t$it")
                        return@handleAll true
                    }
                    !parse(world, it)
                }
                false
            }
        }
    }

    /**
     * 处理一个信息
     * @return 是否需要将信息重新放回队列
     */
    @JvmStatic
    private fun parse(world: World, node: Node): Boolean {
        val entity = if (world.isBlockLoaded(node.pos)) world.getTileEntity(node.pos) else null
        val cap = entity?.getCapability(BlockNetworkCapability.capObj, null)
        if (cap == null) {
            if (node.parseCount == maxTryCount) {
                MISysInfo.err("""
                            [BlockMessage] 一个信息由于失败次数过多被抛弃：
                                    这有可能是接收信息的方块没有包含 BlockNetworkCapability 导致的
                                    详细信息：$node
                        """.trimIndent())
                return false
            }
            ++node.parseCount
            return true
        }
        try {
            cap.receive(node.data, node.ctx)
        } catch (e: Throwable) {
            MISysInfo.err("[BlockMessage] 一个信息由于处理过程中发生异常被抛弃", e)
        }
        return false
    }

}