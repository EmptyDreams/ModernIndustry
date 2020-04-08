package minedreams.mi.api.electricity.src.tileentity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import minedreams.mi.api.electricity.clock.OrdinaryCounter;
import minedreams.mi.api.electricity.clock.OverloadCounter;
import minedreams.mi.api.electricity.src.block.MachineBlock;
import minedreams.mi.api.electricity.src.info.BiggerVoltage;
import minedreams.mi.api.electricity.src.info.EnumBiggerVoltage;
import minedreams.mi.api.electricity.src.info.EnumVoltage;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.register.te.AutoTileEntity;
import minedreams.mi.utils.BlockPosUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * 所有电力设备的父级TE，其中包含了最为基础的方法和循环接口
 * @author EmptyDremas
 * @version V1.1
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_USER")
public abstract class EleSrcUser extends Electricity {
	
	/** 保存电器需要的电压 */
	private EnumVoltage voltage = EnumVoltage.ORDINARY;
	/** 保存所需电能最小值 */
	private int energyMin = 0;
	/** 保存所需电能正常值 */
	private int energy = 0;
	/** 该方块连接的发电机 */
	private final Map<EnumFacing, EleMaker> linkedMaker = new HashMap<>(6);
	/** 连接电线的数量 */
	private final Map<EnumFacing, TileEntity> linkedWire = new HashMap<>(6);
	/** 计数器 */
	public final OverloadCounter COUNTER = new OrdinaryCounter(this);
	/** 过载超时后的操作 */
	protected BiggerVoltage biggerVoltageOperate = new BiggerVoltage(3, EnumBiggerVoltage.BOOM);
	
	/** 设置过载超时后的操作 */
	protected final Electricity setBiggerVoltageOperate(BiggerVoltage bv) {
		biggerVoltageOperate = bv;
		return this;
	}
	/** 获取过载超时后的操作 */
	public final BiggerVoltage getBiggerVoltageOperate() {
		return biggerVoltageOperate;
	}
	
	/**
	 * 连接一个电线
	 * @throws NullPointerException 如果 et == null
	 */
	public void link(EleSrcCable transfer) {
		WaitList.checkNull(transfer, "transfer");
		linkedWire.put(BlockPosUtil.whatFacing(pos, transfer.getPos()), transfer);
	}
	
	/**
	 * 获取已连接的电线
	 * @return 返回值为源数据的副本，可以随意修改
	 */
	public final Map<EnumFacing, TileEntity> getLinkedWire() {
		return new HashMap<>(linkedWire);
	}
	
	/** 是否连接电线 */
	public final boolean isLinkWire() { return !linkedWire.isEmpty(); }
	
	/**
	 * 获取已连接的发电机
	 * @return 返回值为源数据的副本，可以随意修改
	 */
	@Nonnull
	public final Map<EnumFacing, EleMaker> getLinkedMaker() {
		return linkedMaker;
	}
	
	/**
	 * 连接一个发电机，这个方法一般由{@link MachineBlock}调用
	 * @throws NullPointerException 如果 maker == null
	 */
	public void link(EleMaker maker) {
		WaitList.checkNull(maker, "maker");
		linkedMaker.put(BlockPosUtil.whatFacing(pos, maker.getPos()), maker);
	}
	
	/**
	 * 删除一个连接，这个方法一般由{@link MachineBlock}调用
	 * @throws NullPointerException 如果 fromPos == null
	 */
	public final void removeLink(BlockPos fromPos) {
		WaitList.checkNull(fromPos, "fromPos");
		EnumFacing facing = BlockPosUtil.whatFacing(pos, fromPos);
		linkedMaker.remove(facing);
		linkedWire.remove(facing);
	}
	
	public final int getEnergyMin() {
		return energyMin;
	}
	public final void setEnergyMin(int energyMin) {
		this.energyMin = energyMin;
	}
	
	public final int getEnergy() {
		return energy;
	}
	public final void setEnergy(int energy) {
		this.energy = energy;
	}
	
	/**
	 * 设置电器所需电压
	 * @throws NullPointerException 如果 voltage == null
	 */
	public final void setVoltage(EnumVoltage voltage) {
		WaitList.checkNull(voltage, "voltage");
		this.voltage = voltage;
	}
	/** 获取电器所需电压 */
	public final EnumVoltage getVoltage() { return voltage; }
	
	@Override
	public NBTTagCompound getUpdateTag() {
		updateInfo();
		return super.getUpdateTag();
	}
	
	private BlockPos[] _makers;
	private BlockPos[] _transfers;
	
	private void updateInfo() {
		if (_makers == null) return;
		for (BlockPos pos : _makers) {
			linkedMaker.put(BlockPosUtil.whatFacing(getPos(), pos), (EleMaker) world.getTileEntity(pos));
		}
		for (BlockPos pos : _transfers) {
			linkedWire.put(BlockPosUtil.whatFacing(getPos(), pos), world.getTileEntity(pos));
		}
		_makers = null;
		_transfers = null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		int size = compound.getInteger("makers_size");
		_makers = new BlockPos[size];
		for (int i = 0; i < size; ++i) {
			_makers[i] = BlockPosUtil.readBlockPos(compound, "maker_pos_" + i);
		}
		
		size = compound.getInteger("transfers_size");
		_transfers = new BlockPos[size];
		for (int i = 0; i < size; ++i) {
			_transfers[i] = BlockPosUtil.readBlockPos(compound, "transfer_pos_" + i);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		compound.setInteger("makers_size", linkedMaker.size());
		compound.setInteger("transfers_size", linkedWire.size());
		
		int i = 0;
		for (Map.Entry<EnumFacing, EleMaker> entry : linkedMaker.entrySet()) {
			BlockPosUtil.writeBlockPos(compound, entry.getValue().getPos(), "maker_pos_" + i);
			++i;
		}
		
		i = 0;
		for (Map.Entry<EnumFacing, TileEntity> entry : linkedWire.entrySet()) {
			BlockPosUtil.writeBlockPos(compound, entry.getValue().getPos(), "transfer_pos_" + i);
			++i;
		}
		
		return compound;
	}
	
}
