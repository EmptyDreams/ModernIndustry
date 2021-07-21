package xyz.emptydreams.mi.api.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.FluidTransferCapability;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.IFluidTransfer;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.block.BlockAddition;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;
import xyz.emptydreams.mi.content.blocks.base.EleTransferBlock;
import xyz.emptydreams.mi.content.blocks.properties.MIProperty;
import xyz.emptydreams.mi.content.items.debug.DebugDetails;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
@AutoTileEntity("FLUID_TRANSFER_TILE_ENTITY")
public class FTTileEntity extends BaseTileEntity implements IAutoNetwork {
	
	@Storage private final FluidCapability cap = new FluidCapability();
	/** 管道状态 */
	@Storage private final FTStateEnum stateEnum;
	
	public FTTileEntity(FTStateEnum state) {
		stateEnum = state;
	}
	
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
		netRange = new Range3D(pos.getX(), pos.getY(), pos.getZ(), 128);
	}
	
	@Override
	public void receive(@Nonnull IDataReader compound) {
		cap.linkData = compound.readByte();
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
	private Range3D netRange;
	
	/**
	 * <p>像客户端发送服务端存储的信息
	 * <p><b>这其中写有更新内部数据的代码，重写时应该调用</b>
	 */
	public void send() {
		if (world.isRemote) return;
		if (players.size() == world.playerEntities.size()) return;
		ByteDataOperator operator = new ByteDataOperator(1);
		operator.writeByte((byte) cap.linkData);
		operator.writeBoolean(cap.stack == null);
		if (cap.stack != null) {
			DataTypeRegister.write(operator, cap.stack);
		}
		IMessage message = BlockMessage.instance().create(operator, new BlockAddition(this));
		MessageSender.sendToClientIf(message, world, player -> {
			if (players.contains(player.getName()) || !netRange.isIn(new Point3D(player))) return false;
			players.add(player.getName());
			return true;
		});
	}
	
	/** 创建一个blockState */
	public IBlockState createBlockState() {
		IBlockState state = world.getBlockState(getPos());
		FluidCapability cap = getFTCapability();
		return state.withProperty(MIProperty.ALL_FACING, cap.getFacing())
					.withProperty(EleTransferBlock.UP, cap.hasPlugUp())
					.withProperty(EleTransferBlock.DOWN, cap.hasPlugDown())
					.withProperty(EleTransferBlock.WEST, cap.hasPlugWest())
					.withProperty(EleTransferBlock.EAST, cap.hasPlugEast())
					.withProperty(EleTransferBlock.NORTH, cap.hasPlugNorth())
					.withProperty(EleTransferBlock.SOUTH, cap.hasPlugSouth());
	}
	
	/** 更新state */
	public void updateState() {
		WorldUtil.setBlockState(world, pos, createBlockState());
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
	
	@DebugDetails
	public class FluidCapability implements IFluidTransfer {
		
		/** 一格管道可以容纳的最大流体量 */
		public static final int FLUID_TRANSFER_MAX_AMOUNT = 1000;
		
		/** 存储包含的流体类型 */
		@Storage protected FluidStack stack = null;
		/** 六个方向的连接数据 */
		@Storage(byte.class) protected int linkData = 0b000000;
		/** 六个方向的管塞数据 */
		@Storage protected final Map<EnumFacing, Item> plugData = new EnumMap<>(EnumFacing.class);
		/** 存储管道方向 */
		@Storage protected EnumFacing facing = EnumFacing.SOUTH;
		
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
		public void setFacing(EnumFacing facing) {
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
			markDirty();
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
			if ((hasPlugUp() && plug != null) || !stateEnum.canSetPlug(getFacing(), UP)) return false;
			setPlugData(UP, plug);
			return true;
		}
		
		@Override
		public boolean setPlugDown(Item plug) {
			if ((hasPlugDown() && plug != null) || !stateEnum.canSetPlug(getFacing(), DOWN)) return false;
			setPlugData(DOWN, plug);
			return true;
		}
		
		@Override
		public boolean setPlugNorth(Item plug) {
			if ((hasPlugNorth() && plug != null) || !stateEnum.canSetPlug(getFacing(), NORTH)) return false;
			setPlugData(NORTH, plug);
			return true;
		}
		
		@Override
		public boolean setPlugSouth(Item plug) {
			if ((hasPlugSouth() && plug != null) || !stateEnum.canSetPlug(getFacing(), SOUTH)) return false;
			setPlugData(SOUTH, plug);
			return true;
		}
		
		@Override
		public boolean setPlugWest(Item plug) {
			if ((hasPlugWest() && plug != null) || !stateEnum.canSetPlug(getFacing(), WEST)) return false;
			setPlugData(WEST, plug);
			return true;
		}
		
		@Override
		public boolean setPlugEast(Item plug) {
			if ((hasPlugEast() && plug != null) || !stateEnum.canSetPlug(getFacing(), EAST)) return false;
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