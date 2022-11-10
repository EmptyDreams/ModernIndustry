/** 与IO操作有关的封装 */
package top.kmar.mi.api.utils.expands

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.network.ByteBufUtils
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
    val length = readVarInt()
    return readCharSequence(length, StandardCharsets.UTF_8).toString()
}

/**
 * 写入字符串
 * @receiver [ByteBuf]
 */
fun ByteBuf.writeString(data: String) {
    writeVarInt(data.length)
    writeCharSequence(data, StandardCharsets.UTF_8)
}

/** 读取一个变长整型 */
fun ByteBuf.readVarInt(): Int {
    var result = 0
    while (true) {
        val content = readByte().toInt()
        result = (result shl 7) or (content and 0b01111111)
        if (content shr 7 == 1) break
    }
    return result
}

/** 写入一个变长整型 */
fun ByteBuf.writeVarInt(data: Int) {
    var tmp = data
    do {
        var content = tmp and 0b01111111
        tmp = tmp ushr 7
        if (tmp == 0) content = content or 0b10000000
        writeByte(content)
    } while (tmp != 0)
}

/** 读取一个 [NBTBase] */
fun ByteBuf.readNbt(): NBTBase {
    val id = readByte().toInt()
    return when (id) {
        0 -> NBTTagEnd()
        1 -> NBTTagByte(readByte())
        2 -> NBTTagShort(readShort())
        3 -> NBTTagInt(readInt())
        4 -> NBTTagLong(readLong())
        5 -> NBTTagFloat(readFloat())
        6 -> NBTTagDouble(readDouble())
        7 -> {
            val length = readVarInt()
            val array = ByteArray(length) { readByte() }
            NBTTagByteArray(array)
        }
        8 -> NBTTagString(readString())
        9 -> {
            val length = readVarInt()
            val result = NBTTagList()
            for (i in 0 until length) {
                result.appendTag(readNbt())
            }
            result
        }
        10 -> ByteBufUtils.readTag(this)!!
        11 -> {
            val length = readVarInt()
            val array = IntArray(length) { readInt() }
            NBTTagIntArray(array)
        }
        12 -> {
            val length = readVarInt()
            val array = LongArray(length) { readLong() }
            NBTTagLongArray(array)
        }
        else -> throw AssertionError("未知 id：$id")
    }
}

/** 写入一个 [NBTBase] */
fun ByteBuf.writeNbt(nbt: NBTBase) {
    val id = nbt.id.toInt()
    writeByte(id)
    when (id) {
        0 -> {}
        1 -> writeByte((nbt as NBTTagByte).int)
        2 -> writeShort((nbt as NBTTagShort).int)
        3 -> writeInt((nbt as NBTTagInt).int)
        4 -> writeLong((nbt as NBTTagLong).long)
        5 -> writeFloat((nbt as NBTTagFloat).float)
        6 -> writeDouble((nbt as NBTTagDouble).double)
        7 -> {
            val array = (nbt as NBTTagByteArray).byteArray
            writeVarInt(array.size)
            array.forEach { writeByte(it.toInt()) }
        }
        8 -> writeString((nbt as NBTTagString).string)
        9 -> {
            val list = nbt as NBTTagList
            writeVarInt(list.tagCount())
            list.forEach { writeNbt(it) }
        }
        10 -> ByteBufUtils.writeTag(this, nbt as NBTTagCompound)
        11 -> {
            val array = (nbt as NBTTagIntArray).intArray
            writeVarInt(array.size)
            array.forEach { writeInt(it) }
        }
        12 -> {
            val field = nbt::class.java.getDeclaredField("data")
            field.isAccessible = true
            val array = field[nbt] as LongArray
            writeVarInt(array.size)
            array.forEach { writeLong(it) }
        }
        else -> throw AssertionError("未知 id：$id")
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