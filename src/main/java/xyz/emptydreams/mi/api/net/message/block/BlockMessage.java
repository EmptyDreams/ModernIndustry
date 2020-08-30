package xyz.emptydreams.mi.api.net.message.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.utils.BlockUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 方块信息处理
 * @author EmptyDreams
 */
public final class BlockMessage implements IMessageHandle<BlockAddition> {
	
	private static final BlockMessage instance = new BlockMessage();
	private static final AtomicInteger id = new AtomicInteger();
	
	public static BlockMessage instance() {
		return instance;
	}
	
	private BlockMessage() { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean parseOnClient(@Nonnull NBTTagCompound message) {
		World world = WorldUtil.getClientWorld();
		BlockPos pos = BlockUtil.readBlockPos(message, "pos");
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof IAutoNetwork)) {
			message.setByte("cast", (byte) 0);
			return false;
		}
		((IAutoNetwork) te).receive(message.getCompoundTag("data"));
		return true;
	}
	
	@Override
	public boolean parseOnServer(@Nonnull NBTTagCompound message) {
		World world = WorldUtil.getWorld(message.getInteger("world"));
		BlockPos pos = BlockUtil.readBlockPos(message, "pos");
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof IAutoNetwork)) {
			message.setByte("cast", (byte) 0);
			return false;
		}
		((IAutoNetwork) te).receive(message.getCompoundTag("data"));
		return true;
	}
	
	@Override
	public boolean match(@Nonnull NBTTagCompound message) {
		return message.hasKey("type_block");
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
	@Nonnull
	@Override
	public NBTTagCompound packaging(@Nonnull NBTTagCompound data, @Nullable BlockAddition addition) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type_block", id.getAndIncrement());
		tag.setInteger("world", addition.getWorld().provider.getDimension());
		BlockUtil.writeBlockPos(tag, addition.getPos(), "pos");
		tag.setTag("data", data);
		return tag;
	}
	
	@Nonnull
	@Override
	public String getInfo(NBTTagCompound message) {
		String id = "id=" + message.getInteger("type_block");
		if (message.hasKey("cast")) return id + ",info=目标TileEntity没有实现IAutoNetwork接口";
		return id;
	}
	
}
