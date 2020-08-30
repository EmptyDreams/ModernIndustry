package xyz.emptydreams.mi.api.net.message.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.net.message.IMessageAddition;


/**
 * 方块的附加信息
 * @author EmptyDreams
 */
public class BlockAddition implements IMessageAddition {
	
	private final World world;
	private final BlockPos pos;
	
	public BlockAddition(TileEntity te) {
		this(te.getWorld(), te.getPos());
	}
	
	public BlockAddition(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}
	
	public World getWorld() { return world; }
	public BlockPos getPos() { return pos; }
	
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
