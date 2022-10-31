/** 与IO操作有关的封装 */
package top.kmar.mi.api.utils.expands

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import top.kmar.mi.api.araw.AutoDataRW
import top.kmar.mi.api.net.handler.MessageSender.send2ClientIf
import top.kmar.mi.api.net.message.block.BlockAddition
import top.kmar.mi.api.net.message.block.BlockMessage
import top.kmar.mi.api.utils.data.math.Point3D
import top.kmar.mi.api.utils.data.math.Range3D
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Supplier

/**
 * 从[NBTTagCompound]中读取数据到类中
 *
 * @param obj 要处理的类的对象
 * @param key 数据总体在[NBTTagCompound]中的`key`
 *
 * @receiver [NBTTagCompound]
 */
fun NBTTagCompound.readObject(obj: Any, key: String = ".") {
    AutoDataRW.read2ObjAll(getCompoundTag(key), obj)
}

/**
 * 将类中的所有被`AutoSave`注释的属性写入到[NBTTagCompound]中
 *
 * @param obj 要处理的类的对象
 * @param key 数据总体在[NBTTagCompound]中的`key`
 *
 * @receiver [NBTTagCompound]
 */
fun NBTTagCompound.writeObject(obj: Any, key: String = ".") {
    val nbt = AutoDataRW.writeAll(obj)
    setTag(key, nbt)
}

/**
 * 读取字符串
 * @receiver [ByteBuf]
 */
fun ByteBuf.readString(): String {
    val size: Int = readInt()
    val result = ByteArray(size)
    for (i in 0 until size) {
        result[i] = readByte()
    }
    return String(result)
}

/**
 * 写入字符串
 * @receiver [ByteBuf]
 */
fun ByteBuf.writeString(data: String) {
    val bytes = data.toByteArray(StandardCharsets.UTF_8)
    writeInt(bytes.size)
    for (b in bytes) {
        writeByte(b.toInt())
    }
}

/**
 * 如果一个玩家没有接收该方块的信息则向其发送信息
 * @param players 存储已经发送过的玩家UUID的列表
 * @param radius 扫描半径，超过该范围的玩家将不会收到信息
 * @param messageSupplier 提供要发送的信息
 */
fun TileEntity.sendBlockMessageIfNotUpdate(
    players: MutableCollection<UUID>, radius: Int, messageSupplier: Supplier<NBTBase>
) {
    if (world.isClient()) return
    val netRange = Range3D(pos, radius)
    send2ClientIf(world, { player: EntityPlayer ->
        if (players.contains(player.uniqueID) || !netRange.isIn(Point3D(player))) return@send2ClientIf false
        players.add(player.uniqueID)
        true
    }) { BlockMessage.instance().create(messageSupplier.get(), BlockAddition(this)) }
}