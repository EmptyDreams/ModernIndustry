package top.kmar.mi.content.tileentity.pipes;

import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.fluid.FTTileEntity;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.api.register.others.AutoTileEntity;

import javax.annotation.Nonnull;

/**
 * 直线型管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("StraightPipe")
public class StraightPipeTileEntity extends FTTileEntity {
	
	/** 管道朝向 */
	@Storage
    protected EnumFacing facing;
	
	public StraightPipeTileEntity() {
		this(EnumFacing.NORTH);
	}
	
	public StraightPipeTileEntity(EnumFacing facing) {
		this.facing = facing;
	}
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) facing.ordinal());
	}
	
	@Override
	public void syncClient(@Nonnull IDataReader reader) {
		facing = EnumFacing.values()[reader.readByte()];
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing.getAxis() == this.facing.getAxis();
	}
	
	@Override
	public boolean canLinkFluid(EnumFacing facing) {
		return isLinked(facing) ||
				(super.canLinkFluid(facing) && (hasAperture(facing) || getLinkData().isInit()));
	}
	
	@Override
	public boolean linkFluid(EnumFacing facing) {
		if (!super.linkFluid(facing)) return false;
		setFacing(facing);
		updateBlockState(false);
		return true;
	}
	
	@Override
	public int getLinkedAmount() {
		if (isLinked(getFacing().getOpposite())) return isLinked(getFacing()) ? 2 : 1;
		else return isLinked(getFacing()) ? 1 : 0;
	}
	
	/** 设置管道正方向 */
	public void setFacing(EnumFacing facing) {
		//加一个判断是为了防止管道连接时方向倒转导致内容反转
		if (facing.getAxis() != this.facing.getAxis()) this.facing = facing;
	}
	
	/** 获取管道正方向 */
	public EnumFacing getFacing() {
		return facing;
	}
	
}