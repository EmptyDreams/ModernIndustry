package xyz.emptydreams.mi.api.net.message.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.net.message.IMessageAddition;
import xyz.emptydreams.mi.api.utils.BlockUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;


/**
 * 方块的附加信息
 * @author EmptyDreams
 */
public class BlockAddition implements IMessageAddition {
	
	private World world;
	private BlockPos pos;
	
	public BlockAddition(TileEntity te) {
		this(te.getWorld(), te.getPos());
	}
	
	public BlockAddition(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}
	
	BlockAddition() { }
	
	public World getWorld() { return world; }
	public BlockPos getPos() { return pos; }
	
	@Override
	public void writeTo(NBTTagCompound tag) {
		tag.setInteger("world", world.provider.getDimension());
		BlockUtil.writeBlockPos(tag, pos, "pos");
	}
	
	@Override
	public void readFrom(NBTTagCompound tag) {
		world = WorldUtil.getWorld(tag.getInteger("world"));
		pos = BlockUtil.readBlockPos(tag, "pos");
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		BlockAddition that = (BlockAddition) o;
		
		if (!world.equals(that.world)) return false;
		return pos.equals(that.pos);
	}
	
	@Override
	public int hashCode() {
		int result = world.hashCode();
		result = 31 * result + pos.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return "world=" + world.getProviderName() +
				", pos=" + pos;
	}
	
}