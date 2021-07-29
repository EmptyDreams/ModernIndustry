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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.block.BlockAddition;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.DataSerialize;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;
import xyz.emptydreams.mi.api.utils.properties.MIProperty;
import xyz.emptydreams.mi.content.items.debug.DebugDetails;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;
import static xyz.emptydreams.mi.content.blocks.base.PipeBlocks.StraightPipe;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
@AutoTileEntity("FLUID_TRANSFER_TILE_ENTITY")
public class FTTileEntity extends BaseTileEntity implements IAutoNetwork, ITickable {
	
	@Storage protected final FluidSrcCap cap = new FluidSrcCap();
	
	/** 存储当前管道的blockState，存储的原因是在管道放置之后就不会再替换state */
	@Storage protected FTStateEnum stateEnum;
	
	public FTTileEntity(FTStateEnum stateEnum) {
		this.stateEnum = stateEnum;
	}
	
	/** @deprecated 仅供MC反射调用 */
	@Deprecated
	public FTTileEntity() { }
	
	/** 获取当前方块的blockState */
	public FTStateEnum getStateEnum() {
		return stateEnum;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) return true;
		return capability == FluidCapability.TRANSFER;
	}
	
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == FluidCapability.TRANSFER
				&& (facing == null || cap.hasAperture(facing))) {
			//noinspection unchecked
			return (T) cap;
		}
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
	public void receive(@Nonnull IDataReader compound) {
		cap.linkData = compound.readByte();
		cap.setFacing(EnumFacing.values()[compound.readByte()]);
		if (!compound.readBoolean()) {
			cap.stack = DataSerialize.read(compound, FluidStack.class, FluidStack.class, null);
		}
	}
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<String> players = new ArrayList<>(1);
	/** 存储网络数据传输的更新范围，只有在范围内的玩家需要进行更新 */
	private Range3D netRange;
	
	/**
	 * <p>像客户端发送服务端存储的信息
	 * <p><b>这其中写有更新内部数据的代码，重写时应该调用</b>
	 */
	protected void send() {
		if (world.isRemote) return;
		if (players.size() == world.playerEntities.size()) return;
		updateBlockState();
		ByteDataOperator operator = new ByteDataOperator(1);
		operator.writeByte((byte) cap.linkData);
		operator.writeByte((byte) cap.getFacing().getIndex());
		operator.writeBoolean(cap.stack == null);
		if (cap.stack != null) {
			DataSerialize.write(operator, cap.stack, FluidStack.class);
		}
		IMessage message = BlockMessage.instance().create(operator, new BlockAddition(this));
		MessageSender.sendToClientIf(message, world, player -> {
			if (players.contains(player.getName()) || !netRange.isIn(new Point3D(player))) return false;
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
	
	@Override
	public NBTTagCompound getUpdateTag() {
		send();
		return super.getUpdateTag();
	}
	
	public void updateBlockState() {
		IBlockState oldState = world.getBlockState(pos);
		IBlockState newState;
		switch (stateEnum) {
			case STRAIGHT:
				newState = oldState.withProperty(MIProperty.ALL_FACING, cap.getFacing())
						           .withProperty(StraightPipe.BEFORE, cap.hasPlug(cap.getFacing()))
						           .withProperty(StraightPipe.AFTER, cap.hasPlug(cap.getFacing().getOpposite()));
				break;
			case ANGLE:
			case SHUNT:
			default: throw new IllegalArgumentException("输入了未知的状态：" + stateEnum);
		}
		WorldUtil.setBlockState(world, pos, oldState, newState);
	}
	
	protected int sleepTime = 20;
	protected int nowTime = 0;
	
	@Override
	public void update() {
		//检查运行条件
		if (updateTickableState()) {
			nowTime = 0;
			return;
		}
		if (++nowTime != sleepTime) return;
		
		
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
	
	@DebugDetails
	public class FluidSrcCap implements IFluid {
		
		/** 一格管道可以容纳的最大流体量 */
		public static final int FLUID_TRANSFER_MAX_AMOUNT = 1000;
		
		/** 存储包含的流体类型 */
		@Storage protected FluidStack stack = null;
		/** 六个方向的连接数据 */
		@Storage(byte.class) protected int linkData = 0b000000;
		/** 六个方向的管塞数据 */
		@Storage protected final Map<EnumFacing, Item> plugData = new EnumMap<>(EnumFacing.class);
		/** 存储管道方向 */
		@Storage protected EnumFacing facing = NORTH;
		/** 存储管道内流体来源 */
		@Storage protected EnumFacing source = null;
		
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
		public int extract(FluidStack stack, boolean simulate) {
			int real = Math.min(stack.amount, this.stack.amount);
			if (simulate) return real;
			this.stack.amount -= real;
			updateTickableState();
			return real;
		}
		
		@Override
		public int insert(FluidStack stack, EnumFacing facing, boolean simulate) {
			int sum = this.stack.amount + stack.amount;
			if (sum <= getMaxAmount()) {
				if (simulate) return stack.amount;
				this.stack.amount = sum;
				source = StringUtil.checkNull(facing, "facing");
				updateTickableState();
				return stack.amount;
			}
			int real = getMaxAmount() - this.stack.amount;
			if (simulate) return real;
			this.stack.amount = getMaxAmount();
			source = StringUtil.checkNull(facing, "facing");
			updateTickableState();
			return real;
		}
		
		@Override
		public void setFacing(EnumFacing facing) {
			if (facing == getFacing()) return;
			this.facing = facing;
		}
		
		@Override
		public int getMaxAmount() {
			return FLUID_TRANSFER_MAX_AMOUNT;
		}
		
		@Override
		public EnumFacing getFacing() {
			return facing;
		}
		
		@Nullable
		@Override
		public IFluid getLinkedTransfer(EnumFacing facing) {
			if (facing == null) return this;
			BlockPos target = pos.offset(facing);
			TileEntity entity = world.getTileEntity(target);
			if (entity == null) return null;
			return entity.getCapability(FluidCapability.TRANSFER, facing);
		}
		
		@Nonnull
		@Override
		public List<EnumFacing> next(EnumFacing pre) {
			if (!isLinked(pre)) throw new IllegalArgumentException("输入的方向[" + pre + "]没有连接方块");
			List<EnumFacing> result;
			switch (stateEnum) {
				case STRAIGHT:
					for (EnumFacing value : values()) {
						if (isLinked(value)) {
							if (value == pre) continue;
							result = new ArrayList<>(1);
							result.add(value);
							return result;
						}
					}
					result = Collections.emptyList();
					return result;
				case ANGLE:
					break;
				case SHUNT:
					break;
				default: throw new IllegalArgumentException("未知的状态：" + stateEnum);
			}
			return Collections.emptyList();
		}
		
		@Override
		public boolean hasAperture(EnumFacing facing) {
			switch (stateEnum) {
				case STRAIGHT:
					return !hasPlug(facing) && (facing == getFacing() || facing == getFacing().getOpposite());
				case ANGLE: return true;
				case SHUNT: return true;
				default: throw new IllegalArgumentException("该状态不属于任何一种状态：" + stateEnum);
			}
		}
		
		@Override
		public boolean canLink(EnumFacing facing) {
			TileEntity te = world.getTileEntity(pos.offset(facing));
			if (te == null) return false;
			IFluid cap = te.getCapability(FluidCapability.TRANSFER, null);
			if (cap == null) return false;
			if (cap.hasAperture(facing.getOpposite())) {
				return hasAperture(facing) || getLinkAmount() == 0;
			} else if (cap.getLinkAmount() == 0) {
				return hasAperture(facing) || getLinkAmount() == 0;
			} else {
				return false;
			}
		}
		
		@Override
		public boolean link(EnumFacing facing) {
			if (isLinked(facing)) return true;
			if (!canLink(facing)) return false;
			if (linkData == 0) {
				switch (stateEnum) {
					case STRAIGHT:
						setFacing(facing);
						break;
					case ANGLE:
						break;
					case SHUNT:
						break;
				}
			}
			setLinkedData(facing, true);
			return true;
		}
		
		@Override
		public void unlink(EnumFacing facing) {
			setLinkedData(facing, false);
		}
		
		private void setLinkedData(EnumFacing facing, boolean isLinked) {
			switch (facing) {
				case DOWN:
					if (isLinked) linkData |= 0b010000;
					else linkData &= 0b101111;
					break;
				case UP:
					if (isLinked) linkData |= 0b100000;
					else linkData &= 0b0111111;
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
	
}