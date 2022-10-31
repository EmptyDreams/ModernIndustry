package top.kmar.mi.api.net.message.block;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.net.message.IMessageAddition;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;


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
	public NBTBase writeTo() {
		NBTTagCompound result = new NBTTagCompound();
		result.setInteger("dim", world.provider.getDimension());
		result.setIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
		return result;
	}
	
	@Override
	public void readFrom(NBTBase reader) {
		NBTTagCompound nbt = (NBTTagCompound) reader;
		world = WorldExpandsKt.getWorld(nbt.getInteger("dim"));
		int[] array = nbt.getIntArray("pos");
		pos = new BlockPos(array[0], array[1], array[2]);
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