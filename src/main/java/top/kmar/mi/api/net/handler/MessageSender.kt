package top.kmar.mi.api.net.handler

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.net.NetworkLoader
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.data.math.Range3D
import java.util.function.Predicate

/**
 * 发送消息的工具类
 * @author EmptyDreams
 */
class MessageSender {

    companion object {
        /**
         * 发送信息到服务端
         * @param message 消息
         */
        @SideOnly(Side.CLIENT)
        @JvmStatic
        fun sendToServer(message: IMessage) {
            NetworkLoader.instance().sendToServer(message)
        }

        /**
         * 发送消息到在指定世界指定范围内的所有玩家
         * @param world 世界
         * @param range 范围
         * @param messageSupplier 生成信息内容
         */
        @JvmStatic
        fun sendToClientAround(world: World, range: Range3D, messageSupplier: () -> IMessage) {
            val message by lazy(LazyThreadSafetyMode.PUBLICATION) { messageSupplier() }
            WorldUtil.forEachPlayers(world, range) { sendToClient(it as EntityPlayerMP, message) }
        }

        /**
         * 发送信息到指定世界中满足条件的所有玩家
         * @param world 世界对象
         * @param test 条件表达式
         * @param messageSupplier 生成信息内容
         * @throws ClassCastException 如果world中存储的用户对象不是[EntityPlayerMP]
         */
        @JvmStatic
        fun sendToClientIf(world: World, test: (EntityPlayer) -> Boolean, messageSupplier: () -> IMessage) {
            val message by lazy(LazyThreadSafetyMode.PUBLICATION) { messageSupplier() }
            WorldUtil.forEachPlayers(world) {
                if (test(it)) sendToClient(it as EntityPlayerMP, message)
            }
        }

        /**
         * 发送信息到指定世界中的所有玩家
         * @param world 世界对象
         * @param messageSupplier 生成信息内容
         * @throws ClassCastException 如果world中存储的用户对象不是[EntityPlayerMP]
         */
        @JvmStatic
        fun sendToClient(world: World, messageSupplier: () -> IMessage) {
            val message by lazy(LazyThreadSafetyMode.PUBLICATION) { messageSupplier() }
            WorldUtil.forEachPlayers(world) { sendToClient(it as EntityPlayerMP, message) }
        }

        /**
         * 遍历世界中所有玩家，如果玩家满足指定要求则发送消息给玩家
         * @param test 条件表达式
         * @param messageSupplier 生成信息内容
         * @throws ClassCastException 如果world中存储的用户对象不是[EntityPlayerMP]
         */
        @JvmStatic
        fun sendToClientIf(test: Predicate<EntityPlayer>, messageSupplier: () -> IMessage) {
            val message by lazy(LazyThreadSafetyMode.PUBLICATION) { messageSupplier() }
            WorldUtil.forEachPlayers {
                if (test.test(it)) sendToClient(it as EntityPlayerMP, message)
            }
        }

        /**
         * 将消息发送到指定玩家
         * @param message 信息
         */
        @JvmStatic
        fun sendToClientAll(message: IMessage) {
            NetworkLoader.instance().sendToAll(message)
        }

        /**
         * 将消息发送到指定玩家
         * @param player 玩家对象
         * @param message 信息
         */
        @JvmStatic
        fun sendToClient(player: EntityPlayerMP, message: IMessage) {
            NetworkLoader.instance().sendTo(message, player)
        }
    }

}