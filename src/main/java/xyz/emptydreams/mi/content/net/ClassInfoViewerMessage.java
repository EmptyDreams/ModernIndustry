package xyz.emptydreams.mi.content.net;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.message.player.IPlayerHandle;
import xyz.emptydreams.mi.api.register.others.AutoPlayerHandle;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author EmptyDreams
 */
@AutoPlayerHandle("ClassInfoViewerMessage")
public class ClassInfoViewerMessage implements IPlayerHandle {
	
	private static final AtomicReference<TileEntity> TE = new AtomicReference<>();
	
	@Override
	public void apply(EntityPlayer player, IDataReader data) {
		BlockPos pos = data.readBlockPos();
		Block block = player.world.getBlockState(pos).getBlock();
		TileEntity te = block.createTileEntity(player.world, block.getDefaultState());
		NBTTagCompound nbt = data.readTagCompound();
		assert te != null;
		te.deserializeNBT(nbt);
		update(te);
	}
	
	private static void update(TileEntity te) {
		TE.set(te);
	}
	
	public static TileEntity getTileEntity() {
		return TE.get();
	}
	
	/**
	 * 检查指定GUI是否已经完成数据更新
	 */
	public static boolean isUpdate() {
		return TE.get() != null;
	}
	
	public static void unUpdate() {
		TE.set(null);
	}
	
}