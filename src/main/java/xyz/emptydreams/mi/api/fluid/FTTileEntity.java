package xyz.emptydreams.mi.api.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.api.fluid.capabilities.FluidTransferCapability;
import xyz.emptydreams.mi.api.fluid.capabilities.IFluidTransfer;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
@AutoTileEntity("FLUID_TRANSFER_TILE_ENTITY")
public class FTTileEntity extends BaseTileEntity {
	
	private final FluidCapability cap = new FluidCapability();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) return true;
		return capability == FluidTransferCapability.TRANSFER;
	}
	
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == FluidTransferCapability.TRANSFER && getLinked().contains(facing)) {
			//noinspection unchecked
			return (T) cap;
		}
		return super.getCapability(capability, facing);
	}
	
	private List<EnumFacing> getLinked() {
		return cap.linked;
	}
	
	public class FluidCapability implements IFluidTransfer {
		
		public static final int FLUID_TRANSFER_MAX_AMOUNT = 1000;
		
		/** 存储连接的设备的方向 */
		@Storage protected List<EnumFacing> linked = new ArrayList<>(6);
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
		}
		
		@Override
		public int extract(int amount, boolean simulate) {
			int real = Math.min(amount, stack.amount);
			if (simulate) return real;
			stack.amount -= real;
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
			if (linked.contains(facing)) return false;
			return linked.add(facing);
		}
		
		@Override
		public void unlink(EnumFacing facing) {
			linked.remove(facing);
		}
		
		/** 是否连接指定方向 */
		public boolean isLinked(EnumFacing facing) {
			switch (facing) {
				case DOWN: return isLinkedDown();
				case UP: return isLinkedUp();
				case NORTH: return isLinkedNorth();
				case SOUTH: return isLinkedSouth();
				case WEST: return isLinkedWest();
				case EAST: return isLinkedEast();
				default: throw new IllegalArgumentException("输入了未知的方向：" + facing.getName());
			}
		}
		/** 是否连接上方 */
		public boolean isLinkedUp() {
			return (data & 0b100000) == 0b100000;
		}
		/** 是否连接下方 */
		public boolean isLinkedDown() {
			return (data & 0b010000) == 0b010000;
		}
		/** 是否连接东方 */
		public boolean isLinkedEast() {
			return (data & 0b001000) == 0b001000;
		}
		/** 是否连接西方 */
		public boolean isLinkedWest() {
			return (data & 0b000100) == 0b000100;
		}
		/** 是否连接南方 */
		public boolean isLinkedSouth() {
			return (data & 0b000010) == 0b000010;
		}
		/** 是否连接北方 */
		public boolean isLinkedNorth() {
			return (data & 0b000001) == 0b000001;
		}
		
	}
	
}