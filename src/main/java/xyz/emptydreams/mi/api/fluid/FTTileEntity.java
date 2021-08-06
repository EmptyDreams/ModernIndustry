package xyz.emptydreams.mi.api.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.block.BlockAddition;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.DataSerialize;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;
import xyz.emptydreams.mi.content.items.debug.DebugDetails;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;
import static xyz.emptydreams.mi.api.capabilities.fluid.IFluid.FLUID_TRANSFER_MAX_AMOUNT;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
public abstract class FTTileEntity extends BaseTileEntity implements IAutoNetwork, ITickable {
	
	protected final FluidSrcCap cap = initCap();
	
	/** 存储包含的流体类型 */
	@Storage protected FluidStack fluidStack = null;
	/** 六个方向的连接数据 */
	@Storage(byte.class) protected int linkData = 0b000000;
	/** 六个方向的管塞数据 */
	@Storage protected final Map<EnumFacing, Item> plugData = new EnumMap<>(EnumFacing.class);
	/** 存储管道内流体来源 */
	@Storage protected EnumFacing source = null;
	
	public FTTileEntity() { }
	
	/** 初始化流体Cap，子类可重写该方法以修改cap指向的对象 */
	protected FluidSrcCap initCap() {
		return new FluidSrcCap();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) return true;
		boolean aperture = facing == null || cap.hasAperture(facing);
		if (!aperture) return false;
		return capability == FluidCapability.TRANSFER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}
	
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == FluidCapability.TRANSFER
				&& (facing == null || cap.hasAperture(facing))) {
			return  FluidCapability.TRANSFER.cast(cap);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(handler);
		return super.getCapability(capability, facing);
	}
	
	public IFluid getFTCapability() {
		return cap;
	}
	
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		netRange = new Range3D(pos.getX(), pos.getY(), pos.getZ(), 128);
	}
	
	@Override
	public final void receive(@Nonnull IDataReader reader) {
		linkData = reader.readByte();
		if (reader.readBoolean()) {
			fluidStack = DataSerialize.read(reader, FluidStack.class, FluidStack.class, null);
		}
		syncClient(reader);
		updateBlockState(true);
	}
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<String> players = new ArrayList<>(1);
	/** 存储网络数据传输的更新范围，只有在范围内的玩家需要进行更新 */
	private Range3D netRange;
	
	/** 用于写入需要同步的数据 */
	abstract protected void sync(IDataWriter writer);
	
	/** 用于客户端同步数据 */
	@SideOnly(Side.CLIENT)
	abstract protected void syncClient(IDataReader reader);
	
	/**
	 * <p>像客户端发送服务端存储的信息
	 * <p><b>这其中写有更新内部数据的代码，重写时应该调用</b>
	 */
	protected final void send() {
		if (world.isRemote) return;
		if (players.size() == world.playerEntities.size()) return;
		ByteDataOperator operator = new ByteDataOperator(1);
		operator.writeByte((byte) linkData);
		operator.writeBoolean(fluidStack != null);
		if (fluidStack != null) {
			DataSerialize.write(operator, fluidStack, FluidStack.class);
		}
		sync(operator);
		IMessage message = BlockMessage.instance().create(operator, new BlockAddition(this));
		MessageSender.sendToClientIf(message, world, player -> {
			if (players.contains(player.getName()) || !netRange.isIn(new Point3D(player))) return false;
			players.add(player.getName());
			return true;
		});
		updateBlockState(false);
	}
	
	@Override
	public void markDirty() {
		players.clear();
		send();
		super.markDirty();
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		send();
		if (isRemove && players.size() != world.playerEntities.size()) {
			isRemove = false;
			WorldUtil.addTickable(this);
		}
		return super.getUpdateTag();
	}
	
	public void updateBlockState(boolean isRunOnClient) {
		if (world.isRemote && !isRunOnClient) return;
		IBlockState oldState = world.getBlockState(pos);
		IBlockState newState = oldState.getActualState(world, pos);
		WorldUtil.setBlockState(world, pos, oldState, newState);
	}
	
	protected int sleepTime = 20;
	protected int nowTime = 0;
	
	@Override
	public void update() {
		send();
		//检查运行条件
		if (updateTickableState()) {
			nowTime = 0;
			return;
		}
		if (++nowTime != sleepTime) return;
		nowTime = 0;
		List<EnumFacing> nexts = cap.next();
		int amount = Math.min(cap.fluidAmount(), FLUID_TRANSFER_MAX_AMOUNT);
		if (nexts.isEmpty()) return;
		if (nexts.remove(DOWN)) {
			FluidStack stack = FTWorker.applyFluid(world, pos.offset(DOWN), UP, amount);
			if (stack != null) {
				if (stack.amount == amount) return;
				amount -= stack.amount;
			}
		}
		boolean hasUp = nexts.remove(UP);
		for (EnumFacing facing : nexts) {
			FluidStack stack = FTWorker.applyFluid(world, pos.offset(facing), facing.getOpposite(), amount);
			if (stack == null) continue;
			if (stack.amount == amount) return;
			amount -= stack.amount;
		}
		if (!hasUp) return;
		FTWorker.applyFluid(world, pos.offset(UP), DOWN, amount);
	}
	
	private boolean isRemove = false;
	
	/**
	 * 更新管道tickable的状态
	 * @return 如果移除成功则返回true
	 */
	public boolean updateTickableState() {
		if (world.isRemote) {
			if (!isRemove) {
				isRemove = true;
				WorldUtil.removeTickable(this);
			}
			return true;
		}
		if (!isRemove && cap.fluid() == null) {
			isRemove = true;
			WorldUtil.removeTickable(this);
			return true;
		} else if (isRemove) {
			isRemove = false;
			WorldUtil.addTickable(this);
		}
		return false;
	}
	
	/** @see IFluid#next() */
	abstract public List<EnumFacing> next();
	
	/** @see IFluid#hasAperture(EnumFacing) */
	abstract public boolean hasAperture(EnumFacing facing);
	
	/** @see IFluid#canLink(EnumFacing) */
	abstract public boolean canLink(EnumFacing facing);
	
	/** @see IFluid#link(EnumFacing) */
	abstract public boolean link(EnumFacing facing);
	
	protected void setLinkedData(EnumFacing facing, boolean isLinked) {
		switch (facing) {
			case DOWN:
				if (isLinked) linkData |= 0b010000;
				else linkData &= 0b101111;
				break;
			case UP:
				if (isLinked) linkData |= 0b100000;
				else linkData &= 0b011111;
				break;
			case NORTH:
				if (isLinked) linkData |= 0b000001;
				else linkData &= 0b111110;
				break;
			case SOUTH:
				if (isLinked) linkData |= 0b000010;
				else linkData &= 0b111101;
				break;
			case WEST:
				if (isLinked) linkData |= 0b000100;
				else linkData &= 0b111011;
				break;
			case EAST:
				if (isLinked) linkData |= 0b001000;
				else linkData &= 0b110111;
				break;
		}
	}
	
	@DebugDetails
	public class FluidSrcCap implements IFluid {
		
		@Override
		public int fluidAmount() {
			if (fluidStack == null) return 0;
			return fluidStack.amount;
		}
		
		@Nullable
		@Override
		public Fluid fluid() {
			if (fluidStack == null || fluidStack.amount == 0) return null;
			return fluidStack.getFluid();
		}
		
		@Override
		public void setFluid(@Nullable FluidStack stack) {
			if (stack == null || stack.amount == 0) fluidStack = null;
			else fluidStack = stack.copy();
			updateTickableState();
		}
		
		@Override
		public int extract(FluidStack stack, boolean simulate) {
			if (world.isRemote) return 0;
			if (fluidStack == null) return 0;
			int real = Math.min(stack.amount, fluidStack.amount);
			if (simulate) return real;
			fluidStack.amount -= real;
			updateTickableState();
			markDirty();
			return real;
		}
		
		@Override
		public int insert(FluidStack stack, EnumFacing facing, boolean simulate) {
			if (world.isRemote) return 0;
			if (fluidStack == null) fluidStack = new FluidStack(stack.getFluid(), 0);
			int sum = fluidStack.amount + stack.amount;
			if (sum <= getMaxAmount()) {
				if (simulate) return stack.amount;
				fluidStack.amount = sum;
				source = facing;
				updateTickableState();
				markDirty();
				return stack.amount;
			}
			int real = getMaxAmount() - fluidStack.amount;
			if (simulate) return real;
			fluidStack.amount = getMaxAmount();
			source = facing;
			updateTickableState();
			markDirty();
			return real;
		}
		
		@Override
		public void setSource(EnumFacing facing) {
			source = facing;
		}
		
		@Override
		public int getMaxAmount() {
			return FLUID_TRANSFER_MAX_AMOUNT;
		}
		
		@Nullable
		@Override
		public IFluid getLinkedTransfer(EnumFacing facing) {
			if (facing == null) return this;
			BlockPos target = pos.offset(facing);
			TileEntity entity = world.getTileEntity(target);
			if (entity == null) return null;
			return entity.getCapability(FluidCapability.TRANSFER, facing.getOpposite());
		}
		
		@Nonnull
		@Override
		public List<EnumFacing> next() {
			return FTTileEntity.this.next();
		}
		
		@Override
		public boolean hasAperture(EnumFacing facing) {
			return FTTileEntity.this.hasAperture(facing);
		}
		
		@Override
		public boolean canLink(EnumFacing facing) {
			return FTTileEntity.this.canLink(facing);
		}
		
		@Override
		public boolean link(EnumFacing facing) {
			return FTTileEntity.this.link(facing);
		}
		
		@Override
		public void unlink(EnumFacing facing) {
			setLinkedData(facing, false);
		}
		
		@Override
		public boolean isLinkedUp() {
			return (linkData & 0b100000) == 0b100000;
		}
		@Override
		public boolean isLinkedDown() {
			return (linkData & 0b010000) == 0b010000;
		}
		@Override
		public boolean isLinkedEast() {
			return (linkData & 0b001000) == 0b001000;
		}
		@Override
		public boolean isLinkedWest() {
			return (linkData & 0b000100) == 0b000100;
		}
		@Override
		public boolean isLinkedSouth() {
			return (linkData & 0b000010) == 0b000010;
		}
		@Override
		public boolean isLinkedNorth() {
			return (linkData & 0b000001) == 0b000001;
		}
		
		@Override
		public boolean setPlugUp(Item plug) {
			if ((hasPlugUp() && plug != null) || !canSetPlug(UP)) return false;
			setPlugData(UP, plug);
			return true;
		}
		
		@Override
		public boolean setPlugDown(Item plug) {
			if ((hasPlugDown() && plug != null) || !canSetPlug(DOWN)) return false;
			setPlugData(DOWN, plug);
			return true;
		}
		
		@Override
		public boolean setPlugNorth(Item plug) {
			if ((hasPlugNorth() && plug != null) || !canSetPlug(NORTH)) return false;
			setPlugData(NORTH, plug);
			return true;
		}
		
		@Override
		public boolean setPlugSouth(Item plug) {
			if ((hasPlugSouth() && plug != null) || !canSetPlug(SOUTH)) return false;
			setPlugData(SOUTH, plug);
			return true;
		}
		
		@Override
		public boolean setPlugWest(Item plug) {
			if ((hasPlugWest() && plug != null) || !canSetPlug(WEST)) return false;
			setPlugData(WEST, plug);
			return true;
		}
		
		@Override
		public boolean setPlugEast(Item plug) {
			if ((hasPlugEast() && plug != null) || !canSetPlug(EAST)) return false;
			setPlugData(EAST, plug);
			return true;
		}
		
		@Override
		public boolean hasPlugUp() {
			return plugData.get(UP) != null;
		}
		
		@Override
		public boolean hasPlugDown() {
			return plugData.get(DOWN) != null;
		}
		
		@Override
		public boolean hasPlugNorth() {
			return plugData.get(NORTH) != null;
		}
		
		@Override
		public boolean hasPlugSouth() {
			return plugData.get(SOUTH) != null;
		}
		
		@Override
		public boolean hasPlugWest() {
			return plugData.get(WEST) != null;
		}
		
		@Override
		public boolean hasPlugEast() {
			return plugData.get(EAST) != null;
		}
		
		private void setPlugData(EnumFacing facing, Item plug) {
			plugData.put(facing, plug);
			markDirty();
		}
		
	}
	
	private final IFluidHandler handler = new IFluidHandler() {
		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (resource == null) return null;
			int result = cap.extract(resource, !doDrain);
			if (result == 0) return null;
			if (result == resource.amount) return resource;
			return new FluidStack(resource.getFluid(), result);
		}
		
		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			Fluid fluid = cap.fluid();
			if (fluid == null) return null;
			return drain(new FluidStack(fluid, maxDrain), doDrain);
		}
		
		@Override
		public int fill(FluidStack resource, boolean doFill) {
			return cap.insert(resource, null, !doFill);
		}
		
		@Override
		public IFluidTankProperties[] getTankProperties() {
			Fluid fluid = cap.fluid();
			FluidStack stack = (fluid == null || cap.fluidAmount() == 0)
					? null : new FluidStack(fluid, cap.fluidAmount());
			return new IFluidTankProperties[] {
					new FluidTankProperties(stack, FLUID_TRANSFER_MAX_AMOUNT, true, true) };
		}
	};
	
}