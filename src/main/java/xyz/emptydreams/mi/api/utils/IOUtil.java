package xyz.emptydreams.mi.api.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.block.BlockAddition;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

/**
 * @author EmptyDreams
 */
public final class IOUtil {
	
	public static void sendBlockMessageIfNotUpdate(
			TileEntity te, ByteDataOperator operator, Collection<UUID> players, Range3D netRange) {
		IMessage message = BlockMessage.instance().create(operator, new BlockAddition(te));
		MessageSender.sendToClientIf(message, te.getWorld(), player -> {
			if (players.contains(player.getUniqueID()) || !netRange.isIn(new Point3D(player))) return false;
			players.add(player.getUniqueID());
			return true;
		});
	}
	
	/**
	 * 将字符串写入buf
	 * @param buf 指定的buf
	 * @param data 数据内容
	 */
	public static void writeStringToBuf(ByteBuf buf, String data) {
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
		buf.writeInt(bytes.length);
		for (byte b : bytes) {
			buf.writeByte(b);
		}
	}
	
	/**
	 * 从buf读取字符串
	 * @param buf 指定的buf
	 */
	@Nonnull
	public static String readStringFromBuf(ByteBuf buf) {
		int size = buf.readInt();
		byte[] result = new byte[size];
		for (int i = 0; i < size; ++i) {
			result[i] = buf.readByte();
		}
		return new String(result);
	}
	
	/**
	 * 读取坐标
	 * @param compound 要读取的标签
	 * @param name 名称
	 * @return 坐标
	 * @throws IllegalArgumentException 如果输入的名称在NBT中不存在
	 */
	@Nonnull
	public static BlockPos readBlockPos(NBTTagCompound compound, String name) {
		if (!compound.hasKey(name + "_x")) throw new IllegalArgumentException("Key值不存在：" + name);
		return new BlockPos(compound.getInteger(name + "_x"),
				compound.getInteger(name + "_y"), compound.getInteger(name + "_z"));
	}
	
	/**
	 * 写入一个坐标到标签中
	 * @param compound 要写入的标签
	 * @param pos 坐标
	 * @param name 名称
	 */
	public static void writeBlockPos(NBTTagCompound compound, BlockPos pos, String name) {
		compound.setInteger(name + "_x", pos.getX());
		compound.setInteger(name + "_y", pos.getY());
		compound.setInteger(name + "_z", pos.getZ());
	}
}