package xyz.emptydreams.mi.api.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.fluid.capabilities.FluidTransferCapability;
import xyz.emptydreams.mi.api.fluid.capabilities.IFluidTransfer;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.block.BlockAddition;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;
import xyz.emptydreams.mi.content.items.debug.DebugDetails;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
@AutoTileEntity("FLUID_TRANSFER_TILE_ENTITY")
public class FTTileEntity extends BaseTileEntity implements IAutoNetwork {
	
	@Storage
	private final FluidCapability cap = new FluidCapability();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) return true;
		return capability == FluidTransferCapability.TRANSFER;
	}
	
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == FluidTransferCapability.TRANSFER
				&& (facing == null || cap.isLinked(facing))) {
			//noinspection unchecked
			return (T) cap;
		}
		return super.getCapability(capability, facing);
	}
	
	public FluidCapability getFTCapability() {
		return cap;
	}
	
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		net_range = new Range3D(pos.getX(), pos.getY(), pos.getZ(), 128);
	}
	
	@Override
	public void receive(@Nonnull IDataReader compound) {
		cap.data = compound.readByte();
		if (!compound.readBoolean()) {
			cap.stack = DataTypeRegister.read(compound, FluidStack.class, null);
		}
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<String> players = new ArrayList<>(1);
	/** 存储网络数据传输的更新范围，只有在范围内的玩家需要进行更新 */
	private Range3D net_range;
	
	/**
	 * <p>像客户端发送服务端存储的信息
	 * <p><b>这其中写有更新内部数据的代码，重写时应该调用</b>
	 */
	public void send() {
		if (world.isRemote) return;
		if (players.size() == world.playerEntities.size()) return;
		ByteDataOperator operator = new ByteDataOperator(1);
		operator.writeByte((byte) cap.data);
		operator.writeBoolean(cap.stack == null);
		if (cap.stack != null) {
			DataTypeRegister.write(operator, cap.stack);
		}
		IMessage message = BlockMessage.instance().create(operator, new BlockAddition(this));
		MessageSender.sendToClientIf(message, world, player -> {
			if (players.contains(player.getName()) || !net_range.isIn(new Point3D(player))) return false;
			players.add(player.getName());
			return true;
		});
	}
	
	@Override
	public void markDirty() {
		players.clear();
		send();
		super.markDirty();
	}
	
	@DebugDetails
	public class FluidCapability implements IFluidTransfer {
		
		/** 一格管道可以容纳的最大流体量 */
		public static final int FLUID_TRANSFER_MAX_AMOUNT = 1000;
		
		/** 存储包含的流体类型 */
		@Storage protected FluidStack stack = null;
		/** 六个方向的渲染数据 */
		@Storage(byte.class) protected int data = 0b000000;
		
		@Override
		public int fluidAmount() {
			if (stack == null) return 0;
			return stack.amount;
		}
		
		@Nullable
		@Override
		public Fluid fluid() {
			if (stack == null || stack.amount == 0) return null;
			return stack.getFluid();
		}
		
		@Override
		public void setFluid(@Nullable FluidStack stack) {
			if (stack == null || stack.amount == 0) this.stack = null;
			else this.stack = stack.copy();
			markDirty();
		}
		
		@Override
		public int extract(int amount, boolean simulate) {
			int real = Math.min(amount, stack.amount);
			if (simulate) return real;
			stack.amount -= real;
			markDirty();
			return real;
		}
		
		@Override
		public int insert(int amount, boolean simulate) {
			int sum = amount + stack.amount;
			if (sum <= getMaxAmount()) {
				if (!simulate) stack.amount = sum;
				return amount;
			}
			int real = getMaxAmount() - stack.amount;
			if (!simulate) stack.amount = getMaxAmount();
			markDirty();
			return real;
		}
		
		@Override
		public int getMaxAmount() {
			return FLUID_TRANSFER_MAX_AMOUNT;
		}
		
		@Nullable
		@Override
		public IFluidTransfer getLinkedTransfer(EnumFacing facing) {
			if (facing == null) return this;
			BlockPos target = pos.offset(facing);
			TileEntity entity = world.getTileEntity(target);
			if (entity == null) return null;
			return entity.getCapability(FluidTransferCapability.TRANSFER, facing);
		}
		
		@Override
		public boolean link(EnumFacing facing) {
			if (isLinked(facing)) return false;
			setData(facing, true);
			return true;
		}
		
		@Override
		public void unlink(EnumFacing facing) {
			setData(facing, false);
		}
		
		private void setData(EnumFacing facing, boolean isLinked) {
			switch (facing) {
				case DOWN:
					if (isLinked) data |= 0b010000;
					else data &= 0b101111;
					break;
				case UP:
					if (isLinked) data |= 0b100000;
					else data &= 0b0111111;
					break;
				case NORTH:
					if (isLinked) data |= 0b000001;
					else data &= 0b111110;
					break;
				case SOUTH:
					if (isLinked) data |= 0b000010;
					else data &= 0b111101;
					break;
				case WEST:
					if (isLinked) data |= 0b000100;
					else data &= 0b111011;
					break;
				case EAST:
					if (isLinked) data |= 0b001000;
					else data &= 0b110111;
					break;
			}
			markDirty();
		}
		
		@Override
		public boolean isLinkedUp() {
			return (data & 0b100000) == 0b100000;
		}
		@Override
		public boolean isLinkedDown() {
			return (data & 0b010000) == 0b010000;
		}
		@Override
		public boolean isLinkedEast() {
			return (data & 0b001000) == 0b001000;
		}
		@Override
		public boolean isLinkedWest() {
			return (data & 0b000100) == 0b000100;
		}
		@Override
		public boolean isLinkedSouth() {
			return (data & 0b000010) == 0b000010;
		}
		@Override
		public boolean isLinkedNorth() {
			return (data & 0b000001) == 0b000001;
		}
		
	}
	
}