package minedreams.mi.api.net.info;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * 方块坐标(BlockPos)管理
 * @author EmptyDremas
 * @version V1.0
 */
public final class InfoLocation implements SimpleImplInfo<BlockPos> {

	private BlockPos pos;
	
	@Override
	public BlockPos getInfo() {
		return pos;
	}

	@Override
	public void add(BlockPos info) {
		pos = info;
	}

	public void add(TileEntity te) {
		if (te == null) pos = null;
		else pos = te.getPos();
	}
	
	@Override
	public void delete(BlockPos info) {
		pos = null;
	}

	@Override
	public void writeTo(ByteBuf buf) {
		if (pos == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		}
	}

	@Override
	public void readFrom(ByteBuf buf) {
		if (buf.readBoolean())
			pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		else pos = null;
	}

}
